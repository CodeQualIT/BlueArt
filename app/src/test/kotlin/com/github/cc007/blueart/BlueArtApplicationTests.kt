package com.github.cc007.blueart

import io.github.cc007.kolostyles.compiler.KoloCssCompiler
import io.github.cc007.kolostyles.theme.EffectiveKoloTheme
import io.github.cc007.kolostyles.theme.ThemeContribution
import io.github.cc007.kolostyles.theme.ThemeEntry
import io.github.cc007.kolostyles.web.KoloCssController
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import kotlin.test.assertNotNull

@Import(TestcontainersConfiguration::class, BlueArtApplicationTests.TypographyTestConfiguration::class)
@SpringBootTest
class BlueArtApplicationTests(
    private val applicationContext: ApplicationContext,
) {

    @Test
    fun contextLoads() {
    }

    @Test
    fun `loads Kolo styles through auto-configuration`() {
        assertNotNull(applicationContext.getBean<KoloCssCompiler>())
        assertNotNull(applicationContext.getBean<KoloCssController>())
    }

    @Test
    fun `composes the BlueArt semantic overlay once at application startup`() {
        val theme = applicationContext.getBean<EffectiveKoloTheme>()

        theme.entry("--k-color-blueart-page-background").value shouldBe "#c2dff5"
        theme.entry("--k-color-blueart-link").value shouldBe "#085898"
        theme.entry("--k-font-sans").value shouldBe "Inter, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif"
    }

    @Test
    fun `BlueArt font utility emits the overlay value through the shared root block`() {
        val stylesheet = applicationContext.getBean<KoloCssCompiler>().compile("font-sans")

        stylesheet shouldContain "--k-font-sans: Inter, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;"
        stylesheet shouldContain "font-family: var(--k-font-sans);"
        stylesheet.contains("--k-font-serif:") shouldBe false
    }

    @Test
    fun `auto-configured font parser consumes contributed families and typography overrides`() {
        val stylesheet = applicationContext.getBean<KoloCssCompiler>().compile("font-display;font-bold;font-book;text-2xl")

        stylesheet shouldContain "--k-font-display: 'Display Face', sans-serif;"
        stylesheet shouldContain "--k-font-weight-bold: 750;"
        stylesheet shouldContain "--k-text-2xl: 1.75rem;"
        stylesheet shouldContain "--k-text-2xl--line-height: 1.6;"
        stylesheet shouldContain "--k-font-weight-book: 450;"
        stylesheet shouldContain "font-family: var(--k-font-display);"
        stylesheet shouldContain "font-weight: var(--k-font-weight-bold);"
        stylesheet shouldContain "font-size: var(--k-text-2xl);"
        stylesheet shouldContain "line-height: var(--k-text-2xl--line-height);"
        stylesheet shouldContain "font-weight: var(--k-font-weight-book);"
    }

    @TestConfiguration(proxyBeanMethods = false)
    class TypographyTestConfiguration {
        @Bean
        fun typographyTestContribution() = ThemeContribution(
            "typography-test", 100,
            listOf(
                ThemeEntry("--k-font-display", "'Display Face', sans-serif"),
                ThemeEntry("--k-font-weight-bold", "750"),
                ThemeEntry("--k-text-2xl", "1.75rem"),
                ThemeEntry("--k-text-2xl--line-height", "1.6"),
                ThemeEntry("--k-font-weight-book", "450"),
            ),
        )
    }
}
