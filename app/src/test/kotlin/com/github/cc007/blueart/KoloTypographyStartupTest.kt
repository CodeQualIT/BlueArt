package com.github.cc007.blueart

import io.github.cc007.kolostyles.KoloStylesAutoConfiguration
import io.github.cc007.kolostyles.theme.ThemeContribution
import io.github.cc007.kolostyles.theme.ThemeEntry
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class KoloTypographyStartupTest {
    @Test
    fun `ambiguous contributed font names fail application startup`() {
        ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KoloStylesAutoConfiguration::class.java))
            .withBean(ThemeContribution::class.java, {
                ThemeContribution(
                    "ambiguous-fonts", 1,
                    listOf(
                        ThemeEntry("--k-font-book", "'Book Face', serif"),
                        ThemeEntry("--k-font-weight-book", "450"),
                    ),
                )
            })
            .run { context ->
                val failure = context.startupFailure.shouldNotBeNull()
                generateSequence(failure) { it.cause }.last().message shouldContain
                    "Ambiguous font-family and font-weight theme utilities: font-book"
            }
    }
}
