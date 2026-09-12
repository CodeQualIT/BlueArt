package com.github.cc007.blueart.kolostyles.utility

import com.github.cc007.blueart.kolostyles.compiler.KoloCssCompiler
import com.github.cc007.blueart.kolostyles.compiler.font.FontGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.font.FontParserHook
import com.github.cc007.blueart.kolostyles.compiler.layout.display.DisplayGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.layout.display.DisplayParserHook
import com.github.cc007.blueart.kolostyles.compiler.spacing.SpacingGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.spacing.SpacingParserHook
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.css.CssBuilder
import kotlin.test.Test

class FontUtilitiesTest {

    private val parser = FontParserHook()
    private val generator = FontGeneratorHook()
    private val fontOnlyCompiler = KoloCssCompiler(
        parserHooks = listOf(parser),
        generatorHooks = listOf(generator),
    )
    private val mixedCompiler = KoloCssCompiler(
        parserHooks = listOf(SpacingParserHook(), DisplayParserHook(), parser),
        generatorHooks = listOf(SpacingGeneratorHook(), DisplayGeneratorHook(), generator),
    )

    @Test
    fun `parser accepts full font-family allow-list`() {
        val tokens = listOf("font-sans", "font-serif", "font-mono")
        tokens.forEach { token ->
            val parsed = parser.parse(token)
            parsed.shouldNotBeNull()
            parsed.raw shouldBe token
        }
    }

    @Test
    fun `parser accepts full font-size allow-list`() {
        val tokens = listOf(
            "text-xs", "text-sm", "text-base", "text-lg", "text-xl",
            "text-2xl", "text-3xl", "text-4xl", "text-5xl", "text-6xl", "text-7xl", "text-8xl", "text-9xl",
        )
        tokens.forEach { token ->
            val parsed = parser.parse(token)
            parsed.shouldNotBeNull()
            parsed.raw shouldBe token
        }
    }

    @Test
    fun `parser accepts full font-weight allow-list and rejects custom weights`() {
        val accepted = listOf(
            "font-thin", "font-extralight", "font-light", "font-normal", "font-medium",
            "font-semibold", "font-bold", "font-extrabold", "font-black",
        )
        accepted.forEach { token ->
            val parsed = parser.parse(token)
            parsed.shouldNotBeNull()
            parsed.raw shouldBe token
        }

        parser.parse("font-100").shouldBeNull()
        parser.parse("font-950").shouldBeNull()
        parser.parse("font-[600]").shouldBeNull()
    }

    @Test
    fun `parser accepts state and media variants and rejects multiple media variants`() {
        parser.parse("hover:font-semibold").shouldNotBeNull()
        parser.parse("focus-visible:text-lg").shouldNotBeNull()
        parser.parse("md:font-sans").shouldNotBeNull()
        parser.parse("sm:md:text-lg").shouldBeNull()
    }

    @Test
    fun `generator emits base font family css`() {
        val token = parser.parse("font-sans")
        token.shouldNotBeNull()
        val builder = CssBuilder()
        generator.generate(token, builder).shouldBeTrue()
        builder.toString() shouldBe
            """
            .k-font-sans {
            font-family: var(--font-sans);
            }
            
            """.trimIndent()
    }

    @Test
    fun `generator emits pseudo selector for hover font weight variant`() {
        val token = parser.parse("hover:font-semibold")
        token.shouldNotBeNull()
        val builder = CssBuilder()
        generator.generate(token, builder).shouldBeTrue()
        builder.toString() shouldBe
            """
            .k-hover\:font-semibold:hover {
            font-weight: 600;
            }
            
            """.trimIndent()
    }

    @Test
    fun `generator emits media wrapped selector for md font size variant`() {
        val token = parser.parse("md:text-2xl")
        token.shouldNotBeNull()
        val builder = CssBuilder()
        generator.generate(token, builder).shouldBeTrue()
        builder.toString() shouldBe
            """
            @media (width >= 48rem) {
            .k-md\:text-2xl {
            font-size: 1.5rem;
            }
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler marks unknown font token unsupported and malformed font token unparsed`() {
        fontOnlyCompiler.compile("font-950") shouldBe
            """
            :root {
            --kolo-unsupported-0: "font-950";
            }
            
            """.trimIndent()
        fontOnlyCompiler.compile("md: text-xl") shouldBe
            """
            :root {
            --kolo-unparsed-0: "md: text-xl";
            }
            
            """.trimIndent()
    }

    @Test
    fun `mixed spacing display and font compilation preserves input order`() {
        val css = mixedCompiler.compile("md:grid;mt-2;font-semibold;hover:inline-flex")
        css shouldBe
            """
            @media (width >= 48rem) {
            .k-md\:grid {
            display: grid;
            }
            }
            .k-mt-2 {
            margin-top: 0.5rem;
            }
            .k-font-semibold {
            font-weight: 600;
            }
            .k-hover\:inline-flex:hover {
            display: inline-flex;
            }
            
            """.trimIndent()
    }
}
