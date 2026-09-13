package com.github.cc007.blueart.kolostyles.utility

import com.github.cc007.blueart.kolostyles.compiler.KoloCssCompiler
import com.github.cc007.blueart.kolostyles.compiler.font.FontGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.font.FontParserHook
import com.github.cc007.blueart.kolostyles.compiler.layout.LayoutGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.layout.LayoutParserHook
import com.github.cc007.blueart.kolostyles.compiler.layout.display.DisplayGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.layout.display.DisplayParserHook
import com.github.cc007.blueart.kolostyles.compiler.layout.offset.OffsetGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.layout.offset.OffsetParserHook
import com.github.cc007.blueart.kolostyles.compiler.sizing.SizingGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.sizing.SizingParserHook
import com.github.cc007.blueart.kolostyles.compiler.spacing.SpacingGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.spacing.SpacingParserHook
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.css.CssBuilder
import kotlin.test.Test

class LayoutUtilitiesTest {
    private val layoutParser = LayoutParserHook()
    private val offsetParser = OffsetParserHook()
    private val layoutGenerator = LayoutGeneratorHook()
    private val offsetGenerator = OffsetGeneratorHook()
    private val layoutOnlyCompiler = KoloCssCompiler(
        parserHooks = listOf(layoutParser, offsetParser),
        generatorHooks = listOf(layoutGenerator, offsetGenerator),
    )
    private val mixedCompiler = KoloCssCompiler(
        parserHooks = listOf(SpacingParserHook(), DisplayParserHook(), layoutParser, offsetParser, FontParserHook(), SizingParserHook()),
        generatorHooks = listOf(SpacingGeneratorHook(), DisplayGeneratorHook(), layoutGenerator, offsetGenerator, FontGeneratorHook(), SizingGeneratorHook()),
    )

    @Test
    fun `parser accepts tokens for all non-display layout families`() {
        val tokens = listOf(
            "box-border",
            "box-content",
            "overflow-hidden",
            "overflow-x-auto",
            "overflow-y-scroll",
            "sticky",
            "relative",
            "top-0",
            "top-18",
            "left-auto",
            "right-full",
            "inset-1/2",
            "inset-x-px",
            "inset-y-4",
            "z-auto",
            "z-1",
            "z-10",
            "z-999",
            "object-cover",
            "object-contain",
            "object-scale-down",
            "hover:overflow-clip",
            "md:sticky",
            "md:top-18",
        )
        tokens.forEach { token ->
            val parsed = parseLayoutToken(token)
            parsed.shouldNotBeNull()
            parsed.raw shouldBe token
        }
    }

    @Test
    fun `parser rejects unsupported and malformed layout tokens`() {
        parseLayoutToken("z-0").shouldBeNull()
        parseLayoutToken("z--1").shouldBeNull()
        parseLayoutToken("z-[10]").shouldBeNull()
        parseLayoutToken("top--1").shouldBeNull()
        parseLayoutToken("top-[4px]").shouldBeNull()
        parseLayoutToken("overflow-z-auto").shouldBeNull()
        parseLayoutToken("sm:md:sticky").shouldBeNull()
        parseLayoutToken("unknown:top-4").shouldBeNull()
    }

    @Test
    fun `generator emits base pseudo and media layout declarations`() {
        val base = parseLayoutToken("box-border")
        base.shouldNotBeNull()
        val baseBuilder = CssBuilder()
        generateLayoutToken(base, baseBuilder).shouldBeTrue()
        baseBuilder.toString() shouldBe
            """
            .k-box-border {
            box-sizing: border-box;
            }
            
            """.trimIndent()

        val pseudo = parseLayoutToken("hover:overflow-hidden")
        pseudo.shouldNotBeNull()
        val pseudoBuilder = CssBuilder()
        generateLayoutToken(pseudo, pseudoBuilder).shouldBeTrue()
        pseudoBuilder.toString() shouldBe
            """
            .k-hover\:overflow-hidden:hover {
            overflow: hidden;
            }
            
            """.trimIndent()

        val media = parseLayoutToken("md:top-18")
        media.shouldNotBeNull()
        val mediaBuilder = CssBuilder()
        generateLayoutToken(media, mediaBuilder).shouldBeTrue()
        mediaBuilder.toString() shouldBe
            """
            @media (width >= 48rem) {
            .k-md\:top-18 {
            top: 4.5rem;
            }
            }
            
            """.trimIndent()
    }

    @Test
    fun `generator emits z auto and positive integer declarations`() {
        val auto = parseLayoutToken("z-auto")
        auto.shouldNotBeNull()
        val autoBuilder = CssBuilder()
        generateLayoutToken(auto, autoBuilder).shouldBeTrue()
        autoBuilder.toString() shouldBe
            """
            .k-z-auto {
            z-index: auto;
            }
            
            """.trimIndent()

        val positive = parseLayoutToken("z-999")
        positive.shouldNotBeNull()
        val positiveBuilder = CssBuilder()
        generateLayoutToken(positive, positiveBuilder).shouldBeTrue()
        positiveBuilder.toString() shouldBe
            """
            .k-z-999 {
            z-index: 999;
            }
            
            """.trimIndent()
    }

    private fun parseLayoutToken(token: String) =
        layoutParser.parse(token) ?: offsetParser.parse(token)

    private fun generateLayoutToken(
        token: com.github.cc007.blueart.kolostyles.compiler.Token,
        builder: CssBuilder,
    ): Boolean {
        return layoutGenerator.generate(token, builder) || offsetGenerator.generate(token, builder)
    }

    @Test
    fun `mixed spacing layout display font and sizing compilation preserves input order`() {
        val css = mixedCompiler.compile("md:grid;overflow-hidden;mt-2;font-semibold;size-full;sticky;top-0;z-10;object-cover")
        css shouldBe
            """
            @media (width >= 48rem) {
            .k-md\:grid {
            display: grid;
            }
            }
            .k-overflow-hidden {
            overflow: hidden;
            }
            .k-mt-2 {
            margin-top: 0.5rem;
            }
            .k-font-semibold {
            font-weight: 600;
            }
            .k-size-full {
            width: 100%;
            height: 100%;
            }
            .k-sticky {
            position: sticky;
            }
            .k-top-0 {
            top: 0.0rem;
            }
            .k-z-10 {
            z-index: 10;
            }
            .k-object-cover {
            object-fit: cover;
            }
            
            """.trimIndent()
    }

    @Test
    fun `layout compiler marks unsupported and malformed layout tokens deterministically`() {
        layoutOnlyCompiler.compile("z-0") shouldBe
            """
            :root {
            --kolo-unsupported-0: "z-0";
            }
            
            """.trimIndent()
        layoutOnlyCompiler.compile("top-[4px]") shouldBe
            """
            :root {
            --kolo-unparsed-0: "top-[4px]";
            }
            
            """.trimIndent()
    }
}
