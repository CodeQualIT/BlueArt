package com.github.cc007.blueart.visual

import com.microsoft.playwright.*
import com.microsoft.playwright.options.LoadState
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern
import javax.imageio.ImageIO

enum class VisualBrowser(val id: String) {
    CHROMIUM("chromium"),
    FIREFOX("firefox");

    fun launch(playwright: Playwright): Browser {
        val options = BrowserTypeOptionsFactory.headlessLaunchOptions()
        return when (this) {
            CHROMIUM -> playwright.chromium().launch(options)
            FIREFOX -> playwright.firefox().launch(options)
        }
    }
}

private object BrowserTypeOptionsFactory {
    fun headlessLaunchOptions(): BrowserType.LaunchOptions = BrowserType.LaunchOptions().setHeadless(true)
}

object VisualTestRuntime {
    private const val VIEWPORT_WIDTH = 1366
    private const val VIEWPORT_HEIGHT = 900
    private val gifFrames = ConcurrentHashMap<String, ByteArray>()

    fun newContext(
        browser: Browser,
        width: Int = VIEWPORT_WIDTH,
        height: Int = VIEWPORT_HEIGHT,
    ): BrowserContext {
        val context = browser.newContext(
            Browser.NewContextOptions()
                .setViewportSize(width, height)
                .setLocale("en-US")
                .setTimezoneId("UTC")
        )

        // GIF playback is independent of CSS animations. Decode frame zero before
        // the browser sees the response, without changing the application or its URLs.
        context.route(Pattern.compile(".*\\.gif(?:\\?.*)?$", Pattern.CASE_INSENSITIVE)) { route ->
            val png = gifFrames.computeIfAbsent(route.request().url()) {
                val response = route.fetch()
                try {
                    check(response.ok()) { "GIF request failed: ${response.status()} ${response.url()}" }
                    val frame = requireNotNull(ImageIO.read(ByteArrayInputStream(response.body()))) {
                        "Cannot decode GIF: ${response.url()}"
                    }
                    ByteArrayOutputStream().use { output ->
                        check(ImageIO.write(frame, "png", output)) { "PNG encoder is unavailable" }
                        output.toByteArray()
                    }
                } finally {
                    response.dispose()
                }
            }
            route.fulfill(
                Route.FulfillOptions()
                    .setContentType("image/png")
                    .setBodyBytes(png)
            )
        }

        context.addInitScript(
            """
            (() => {
              const reducedMotionQuery = '(prefers-reduced-motion: reduce)';
              const originalMatchMedia = window.matchMedia.bind(window);
              window.matchMedia = (query) => {
                if (query.includes('prefers-reduced-motion')) {
                  return {
                    matches: query === reducedMotionQuery || query.includes('reduce'),
                    media: query,
                    onchange: null,
                    addListener: () => {},
                    removeListener: () => {},
                    addEventListener: () => {},
                    removeEventListener: () => {},
                    dispatchEvent: () => false,
                  };
                }
                return originalMatchMedia(query);
              };
            })();
            """.trimIndent()
        )

        return context
    }

    fun disableAnimations(page: Page) {
        page.addStyleTag(
            Page.AddStyleTagOptions().setContent(
                """
                *, *::before, *::after {
                  animation-duration: 0s !important;
                  animation-delay: 0s !important;
                  transition-duration: 0s !important;
                  transition-delay: 0s !important;
                  caret-color: transparent !important;
                }
                """.trimIndent()
            )
        )
    }

    fun waitForStableRendering(page: Page) {
        page.waitForLoadState(LoadState.NETWORKIDLE)
        page.waitForFunction("() => (document.fonts ? document.fonts.status === 'loaded' : true)")
        page.waitForFunction("() => Array.from(document.images).every((image) => image.complete)")
    }

    fun loginAsDummy(page: Page, baseUrl: String) {
        page.navigate("$baseUrl/login")
        page.locator("input[name='username']").fill("dummy.localhost")
        page.locator("input[name='password']").fill("1234")
        page.locator("input[name='pdsUrl']").fill("localhost")
        page.locator("input[type='submit'][value='Login']").click()
        page.waitForURL("**/browse")
    }
}
