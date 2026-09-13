package io.github.cc007.kolostyles.utility

import io.github.cc007.kolostyles.compiler.KoloCssCompiler
import io.github.cc007.kolostyles.compiler.font.FontGeneratorHook
import io.github.cc007.kolostyles.compiler.font.FontParserHook
import io.github.cc007.kolostyles.compiler.layout.LayoutGeneratorHook
import io.github.cc007.kolostyles.compiler.layout.LayoutParserHook
import io.github.cc007.kolostyles.compiler.layout.display.DisplayGeneratorHook
import io.github.cc007.kolostyles.compiler.layout.display.DisplayParserHook
import io.github.cc007.kolostyles.compiler.layout.offset.OffsetGeneratorHook
import io.github.cc007.kolostyles.compiler.layout.offset.OffsetParserHook
import io.github.cc007.kolostyles.compiler.sizing.SizingGeneratorHook
import io.github.cc007.kolostyles.compiler.sizing.SizingParserHook
import io.github.cc007.kolostyles.compiler.spacing.SpacingGeneratorHook
import io.github.cc007.kolostyles.compiler.spacing.SpacingParserHook
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ResponsiveVariantsTest {
    private val parsers = listOf(
        SpacingParserHook(),
        DisplayParserHook(),
        LayoutParserHook(),
        OffsetParserHook(),
        FontParserHook(),
        SizingParserHook(),
    )
    private val compiler = KoloCssCompiler(
        parserHooks = parsers,
        generatorHooks = listOf(
            SpacingGeneratorHook(),
            DisplayGeneratorHook(),
            LayoutGeneratorHook(),
            OffsetGeneratorHook(),
            FontGeneratorHook(),
            SizingGeneratorHook(),
        ),
    )

    @Test
    fun `every utility family accepts all named maximum-width variants`() {
        val utilities = listOf("p-4", "flex", "sticky", "top-4", "font-bold", "w-full")
        val variants = listOf("max-sm", "max-md", "max-lg", "max-xl", "max-2xl")

        utilities.forEach { utility ->
            variants.forEach { variant ->
                parsers.firstNotNullOfOrNull { it.parse("$variant:$utility") }
                    .shouldNotBeNull()
                    .raw shouldBe "$variant:$utility"
            }
        }
    }

    @Test
    fun `unknown maximum-width variants are rejected by every utility family`() {
        listOf("p-4", "flex", "sticky", "top-4", "font-bold", "w-full").forEach { utility ->
            parsers.forEach { parser -> parser.parse("max-700:$utility").shouldBeNull() }
        }
    }

    @Test
    fun `maximum-width boundaries state composition and minimum-width output are deterministic`() {
        compiler.compile("max-sm:p-4;max-md:p-4;max-lg:p-4;max-xl:p-4;max-2xl:p-4;hover:max-md:flex;md:p-4") shouldBe
            """
            @media (width < 40rem) {
            .k-max-sm\:p-4 {
            padding: 1.0rem;
            }
            }
            @media (width < 48rem) {
            .k-max-md\:p-4 {
            padding: 1.0rem;
            }
            .k-hover\:max-md\:flex:hover {
            display: flex;
            }
            }
            @media (width < 64rem) {
            .k-max-lg\:p-4 {
            padding: 1.0rem;
            }
            }
            @media (width < 80rem) {
            .k-max-xl\:p-4 {
            padding: 1.0rem;
            }
            }
            @media (width < 96rem) {
            .k-max-2xl\:p-4 {
            padding: 1.0rem;
            }
            }
            @media (width >= 48rem) {
            .k-md\:p-4 {
            padding: 1.0rem;
            }
            }
            
            """.trimIndent()
    }
}
