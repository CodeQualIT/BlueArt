package io.github.cc007.kolostyles.utility

import io.github.cc007.kolostyles.compiler.KoloCssCompiler
import io.github.cc007.kolostyles.compiler.layout.display.DisplayGeneratorHook
import io.github.cc007.kolostyles.compiler.layout.display.DisplayParserHook
import io.github.cc007.kolostyles.compiler.spacing.SpacingGeneratorHook
import io.github.cc007.kolostyles.compiler.spacing.SpacingParserHook
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.css.CssBuilder
import kotlin.test.Test

class DisplayUtilitiesTest {

    private val parser = DisplayParserHook()
    private val generator = DisplayGeneratorHook()
    private val displayOnlyCompiler = KoloCssCompiler(
        parserHooks = listOf(parser),
        generatorHooks = listOf(generator),
    )
    private val mixedCompiler = KoloCssCompiler(
        parserHooks = listOf(SpacingParserHook(), parser),
        generatorHooks = listOf(SpacingGeneratorHook(), generator),
    )

    @Test
    fun `parser accepts full display allow-list`() {
        val tokens = listOf(
            "block", "inline", "inline-block", "flow-root", "flex", "inline-flex", "grid", "inline-grid",
            "contents", "list-item", "hidden", "table", "inline-table", "table-caption", "table-cell",
            "table-column", "table-column-group", "table-header-group", "table-row-group", "table-row",
            "table-footer-group",
        )

        tokens.forEach { token ->
            val parsed = parser.parse(token)
            parsed.shouldNotBeNull()
            parsed.raw shouldBe token
        }
    }

    @Test
    fun `parser rejects unknown and unsupported display-like tokens`() {
        parser.parse("sr-only").shouldBeNull()
        parser.parse("not-sr-only").shouldBeNull()
        parser.parse("inline-list").shouldBeNull()
    }

    @Test
    fun `parser accepts state and media variants and rejects multiple media variants`() {
        parser.parse("hover:flex").shouldNotBeNull()
        parser.parse("focus-visible:grid").shouldNotBeNull()
        parser.parse("md:inline-grid").shouldNotBeNull()
        parser.parse("sm:md:flex").shouldBeNull()
    }

    @Test
    fun `generator emits base display css for flex`() {
        val token = parser.parse("flex")
        token.shouldNotBeNull()
        val builder = CssBuilder()
        generator.generate(token, builder).shouldBeTrue()
        builder.toString() shouldBe
            """
            .k-flex {
            display: flex;
            }
            
            """.trimIndent()
    }

    @Test
    fun `generator emits pseudo selector for hover display variant`() {
        val token = parser.parse("hover:inline-flex")
        token.shouldNotBeNull()
        val builder = CssBuilder()
        generator.generate(token, builder).shouldBeTrue()
        builder.toString() shouldBe
            """
            .k-hover\:inline-flex:hover {
            display: inline-flex;
            }
            
            """.trimIndent()
    }

    @Test
    fun `generator emits media wrapped selector for md display variant`() {
        val token = parser.parse("md:grid")
        token.shouldNotBeNull()
        val builder = CssBuilder()
        generator.generate(token, builder).shouldBeTrue()
        builder.toString() shouldBe
            """
            @media (width >= 48rem) {
            .k-md\:grid {
            display: grid;
            }
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler marks unknown display token unsupported and malformed display token unparsed`() {
        displayOnlyCompiler.compile("sr-only") shouldBe
            """
            :root {
            --kolo-unsupported-0: "sr-only";
            }
            
            """.trimIndent()
        displayOnlyCompiler.compile("md: flex") shouldBe
            """
            :root {
            --kolo-unparsed-0: "md: flex";
            }
            
            """.trimIndent()
    }

    @Test
    fun `mixed spacing and display compilation preserves input order`() {
        val css = mixedCompiler.compile("md:grid;mt-2;hover:inline-flex")
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
            .k-hover\:inline-flex:hover {
            display: inline-flex;
            }
            
            """.trimIndent()
    }
}
