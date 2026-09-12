package com.github.cc007.blueart.kolostyles.utility

import com.github.cc007.blueart.kolostyles.compiler.KoloCssCompiler
import com.github.cc007.blueart.kolostyles.compiler.Token
import com.github.cc007.blueart.kolostyles.compiler.font.FontGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.font.FontParserHook
import com.github.cc007.blueart.kolostyles.compiler.layout.display.DisplayGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.layout.display.DisplayParserHook
import com.github.cc007.blueart.kolostyles.compiler.sizing.SizingGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.sizing.SizingParserHook
import com.github.cc007.blueart.kolostyles.compiler.spacing.SpacingGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.spacing.SpacingParserHook
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.css.CssBuilder
import kotlin.test.Test

class SizingUtilitiesTest {

    private val parser = SizingParserHook()
    private val generator = SizingGeneratorHook()
    private val sizingOnlyCompiler = KoloCssCompiler(
        parserHooks = listOf(parser),
        generatorHooks = listOf(generator),
    )
    private val mixedCompiler = KoloCssCompiler(
        parserHooks = listOf(SpacingParserHook(), DisplayParserHook(), FontParserHook(), parser),
        generatorHooks = listOf(SpacingGeneratorHook(), DisplayGeneratorHook(), FontGeneratorHook(), generator),
    )

    @Test
    fun `parser accepts tokens for all sizing utility families`() {
        val tokens = listOf(
            "w-full",
            "w-1/2",
            "w-1/1",
            "w-dvw",
            "h-screen",
            "h-svh",
            "min-w-1/2",
            "max-w-3xl",
            "min-h-dvh",
            "max-h-fit",
            "size-3",
            "size-lvw",
            "size-1/3",
            "size-2/7",
            "w-13",
            "hover:w-4",
            "md:max-h-screen",
        )

        tokens.forEach { token ->
            val parsed = parser.parse(token)
            parsed.shouldNotBeNull()
            parsed.raw shouldBe token
        }
    }

    @Test
    fun `parser rejects unsupported sizing tokens and malformed variants`() {
        parser.parse("w-[170px]").shouldBeNull()
        parser.parse("h-72vh").shouldBeNull()
        parser.parse("max-w-none").shouldBeNull()
        parser.parse("h-3xl").shouldBeNull()
        parser.parse("min-h-md").shouldBeNull()
        parser.parse("max-h-2xl").shouldBeNull()
        parser.parse("size-xs").shouldBeNull()
        parser.parse("w-1/0").shouldBeNull()
        parser.parse("sm:md:w-4").shouldBeNull()
        parser.parse("unknown:w-4").shouldBeNull()
    }

    @Test
    fun `generator emits declarations for all sizing prefixes including size dual output`() {
        val tokens = listOf("w-4", "w-1/1", "h-full", "min-w-0", "max-w-md", "min-h-screen", "max-h-fit", "size-full")
        val css = tokens.joinToString(separator = "") { token ->
            val parsed = parser.parse(token)
            parsed.shouldNotBeNull()
            val builder = CssBuilder()
            generator.generate(parsed, builder).shouldBeTrue()
            builder.toString()
        }

        css.shouldContain(".k-w-4 {\nwidth: 1.0rem;\n}")
        css.shouldContain(".k-w-1/1 {\nwidth: calc(1/1 * 100%);\n}")
        css.shouldContain(".k-h-full {\nheight: 100%;\n}")
        css.shouldContain(".k-min-w-0 {\nmin-width: 0.0rem;\n}")
        css.shouldContain(".k-max-w-md {\nmax-width: 28rem;\n}")
        css.shouldContain(".k-min-h-screen {\nmin-height: 100vh;\n}")
        css.shouldContain(".k-max-h-fit {\nmax-height: fit-content;\n}")
        css.shouldContain(".k-size-full {\nwidth: 100%;\nheight: 100%;\n}")
    }

    @Test
    fun `generator emits pseudo and media variants for sizing utilities`() {
        val pseudoToken = parser.parse("hover:w-4")
        pseudoToken.shouldNotBeNull()
        val pseudoBuilder = CssBuilder()
        generator.generate(pseudoToken, pseudoBuilder).shouldBeTrue()
        pseudoBuilder.toString() shouldBe
            """
            .k-hover\:w-4:hover {
            width: 1.0rem;
            }
            
            """.trimIndent()

        val mediaToken = parser.parse("md:min-h-screen")
        mediaToken.shouldNotBeNull()
        val mediaBuilder = CssBuilder()
        generator.generate(mediaToken, mediaBuilder).shouldBeTrue()
        mediaBuilder.toString() shouldBe
            """
            @media (width >= 48rem) {
            .k-md\:min-h-screen {
            min-height: 100vh;
            }
            }
            
            """.trimIndent()
    }

    @Test
    fun `generator returns false for unsupported token type`() {
        val unsupported = UnsupportedToken("w-4")
        val builder = CssBuilder()
        generator.generate(unsupported, builder).shouldBeFalse()
        builder.toString() shouldBe ""
    }

    @Test
    fun `compiler marks unsupported sizing token and generates mixed utility output in order`() {
        sizingOnlyCompiler.compile("max-w-none") shouldBe
            """
            :root {
            --kolo-unsupported-0: "max-w-none";
            }
            
            """.trimIndent()

        val css = mixedCompiler.compile("md:grid;w-full;mt-2;font-semibold;size-1/2")
        css shouldBe
            """
            @media (width >= 48rem) {
            .k-md\:grid {
            display: grid;
            }
            }
            .k-w-full {
            width: 100%;
            }
            .k-mt-2 {
            margin-top: 0.5rem;
            }
            .k-font-semibold {
            font-weight: 600;
            }
            .k-size-1/2 {
            width: calc(1/2 * 100%);
            height: calc(1/2 * 100%);
            }
            
            """.trimIndent()
    }

    private data class UnsupportedToken(
        override val raw: String
    ) : Token
}
