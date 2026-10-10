package com.github.cc007.blueart.endpoints.auth

import com.github.cc007.blueart.testsupport.parseHtml
import com.github.cc007.blueart.testsupport.selectRequired
import com.github.cc007.blueart.testsupport.shouldContainText
import io.kotest.assertions.fail
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.CookieManager
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DummyLoginFlowTest(
    @LocalServerPort private val port: Int,
) {
    private val cookieManager = CookieManager()
    private val client = HttpClient.newBuilder()
        .cookieHandler(cookieManager)
        .followRedirects(HttpClient.Redirect.NEVER)
        .build()

    @Test
    fun `localhost login uses dummy auth and browse detail flow`() {
        val loginPage = get("/login")
        val csrfToken = Regex("""name="_csrf"[^>]*value="([^"]+)"""")
            .find(loginPage.body())
            ?.groupValues
            ?.get(1)
            ?: fail("CSRF token not found on login page")

        val login = postForm(
            "/login",
            mapOf(
                "username" to "dummy.localhost",
                "password" to "1234",
                "pdsUrl" to "localhost",
                "_csrf" to csrfToken,
            ),
        )

        login.statusCode() shouldBe 302
        val locationHeader = login.headers().firstValue("location")
        locationHeader shouldBePresent { it shouldEndWith "/browse" }

        val browse = get("/browse")
        browse.statusCode() shouldBe 200
        val browseDocument = browse.body().parseHtml()
        browseDocument.selectRequired(".content-top h1") shouldContainText "Browse Timeline"
        browseDocument.selectRequired(".post-author .author-name") shouldContainText "BlueArt Dummy"
        browseDocument.selectRequired(".embed-media-grid")
        browseDocument.selectRequired("p.post-open-link a") shouldContainText "Open artwork"
        browseDocument.selectRequired("p.post-text.feed-follow") shouldContainText "@dummy.localhost followed @dummy-follow-target"
        browseDocument.select("p.post-text em").eachText() shouldNotContain "This type of post is not yet supported"

        val detailUri = "at://dummy.localhost/app.bsky.feed.post/image-gallery"
        val detail = get("/art/bafyreidummyimage-gallery?uri=${URLEncoder.encode(detailUri, StandardCharsets.UTF_8)}")
        detail.statusCode() shouldBe 200
        val detailDocument = detail.body().parseHtml()
        detailDocument.selectRequired(".art-description h2").text() shouldBe "Description"
        detailDocument.selectRequired("p.art-text") shouldContainText "Multi-image embed to exercise the gallery layout."
        detailDocument.selectRequired(".art-image-grid")
        detailDocument.select(".art-image-grid img.art-image").size shouldBe 2

        listOf(browseDocument to "browse", detailDocument to "art").forEach { (document, page) ->
            document.selectRequired("body.k-font-sans")
            document.selectRequired("link[href='/css/generated/$page.css']")
            val koloHref = document.selectRequired("link[href^='/css/generated/kolo.css']").attr("href")
            val koloCss = get(koloHref)
            koloCss.statusCode() shouldBe 200
            koloCss.body() shouldContain "--k-font-sans: Inter, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;"
            koloCss.body() shouldContain "font-family: var(--k-font-sans);"
        }
    }

    private fun get(path: String): HttpResponse<String> {
        return client.send(
            HttpRequest.newBuilder(uri(path))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString(),
        )
    }

    private fun postForm(path: String, form: Map<String, String>): HttpResponse<String> {
        val body = form.entries.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, StandardCharsets.UTF_8)}=${URLEncoder.encode(value, StandardCharsets.UTF_8)}"
        }
        return client.send(
            HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            HttpResponse.BodyHandlers.ofString(),
        )
    }

    private fun uri(path: String): URI = URI.create("http://localhost:$port$path")
}
