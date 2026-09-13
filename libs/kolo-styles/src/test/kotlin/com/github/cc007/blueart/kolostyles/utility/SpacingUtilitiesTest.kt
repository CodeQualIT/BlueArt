package com.github.cc007.blueart.kolostyles.utility

import com.github.cc007.blueart.kolostyles.compiler.KoloCssCompiler
import com.github.cc007.blueart.kolostyles.compiler.Token
import com.github.cc007.blueart.kolostyles.compiler.spacing.SpacingGeneratorHook
import com.github.cc007.blueart.kolostyles.compiler.spacing.SpacingParserHook
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.css.CssBuilder
import kotlin.test.Test

class SpacingUtilitiesTest {

    private val parser = SpacingParserHook()
    private val generator = SpacingGeneratorHook()
    private val compiler = KoloCssCompiler(
        parserHooks = listOf(parser),
        generatorHooks = listOf(generator),
    )

    // --- parser ---

    @Test
    fun `parser accepts m-0 and returns spacing token`() {
        val def = parser.parse("m-0")
        def.shouldNotBeNull()
        def.raw shouldBe "m-0"
    }

    @Test
    fun `parser accepts mt-4`() {
        val def = parser.parse("mt-4")
        def.shouldNotBeNull()
        def.raw shouldBe "mt-4"
    }

    @Test
    fun `parser accepts mb-3`() {
        val def = parser.parse("mb-3")
        def.shouldNotBeNull()
        def.raw shouldBe "mb-3"
    }

    @Test
    fun `parser accepts p-2`() {
        val def = parser.parse("p-2")
        def.shouldNotBeNull()
        def.raw shouldBe "p-2"
    }

    @Test
    fun `parser accepts mx-4`() {
        val def = parser.parse("mx-4")
        def.shouldNotBeNull()
        def.raw shouldBe "mx-4"
    }

    @Test
    fun `parser accepts mx-auto`() {
        val def = parser.parse("mx-auto")
        def.shouldNotBeNull()
        def.raw shouldBe "mx-auto"
    }

    @Test
    fun `parser accepts mt-auto`() {
        val def = parser.parse("mt-auto")
        def.shouldNotBeNull()
        def.raw shouldBe "mt-auto"
    }

    @Test
    fun `parser accepts p-auto`() {
        val def = parser.parse("p-auto")
        def.shouldNotBeNull()
        def.raw shouldBe "p-auto"
    }

    @Test
    fun `parser accepts py-2`() {
        val def = parser.parse("py-2")
        def.shouldNotBeNull()
        def.raw shouldBe "py-2"
    }

    @Test
    fun `parser accepts hover variant`() {
        val def = parser.parse("hover:mt-2")
        def.shouldNotBeNull()
        def.raw shouldBe "hover:mt-2"
    }

    @Test
    fun `parser accepts md media variant`() {
        val def = parser.parse("md:p-4")
        def.shouldNotBeNull()
        def.raw shouldBe "md:p-4"
    }

    @Test
    fun `parser rejects multiple media variants`() {
        parser.parse("sm:md:p-4").shouldBeNull()
    }

    @Test
    fun `parser rejects unknown token`() {
        parser.parse("flex").shouldBeNull()
    }

    @Test
    fun `parser rejects arbitrary value token`() {
        parser.parse("mt-[4px]").shouldBeNull()
    }

    @Test
    fun `parser rejects malformed negative step`() {
        parser.parse("mt--2").shouldBeNull()
    }

    @Test
    fun `parser rejects unsupported auto utility`() {
        parser.parse("gap-auto").shouldBeNull()
    }

    // --- generator ---

    @Test
    fun `generator produces k- prefixed rule for m-0`() {
        val def = parser.parse("m-0")
        def.shouldNotBeNull()
        val cssBuilder = CssBuilder()
        generator.generate(def, cssBuilder).shouldBeTrue()
        cssBuilder.toString() shouldBe
            """
            .k-m-0 {
            margin: 0.0rem;
            }
            
            """.trimIndent()
    }

    @Test
    fun `generator produces k- prefixed rule with escaped colon for hover variant`() {
        val def = parser.parse("hover:mt-2")
        def.shouldNotBeNull()
        val cssBuilder = CssBuilder()
        generator.generate(def, cssBuilder).shouldBeTrue()
        cssBuilder.toString() shouldBe
            """
            .k-hover\:mt-2:hover {
            margin-top: 0.5rem;
            }
            
            """.trimIndent()
    }

    @Test
    fun `generator wraps md variant in media query`() {
        val def = parser.parse("md:p-4")
        def.shouldNotBeNull()
        val cssBuilder = CssBuilder()
        generator.generate(def, cssBuilder).shouldBeTrue()
        cssBuilder.toString() shouldBe
            """
            @media (width >= 48rem) {
            .k-md\:p-4 {
            padding: 1.0rem;
            }
            }
            
            """.trimIndent()
    }

    @Test
    fun `generator returns false for unsupported token`() {
        val token = UnsupportedToken("flex")
        val cssBuilder = CssBuilder()
        generator.generate(token, cssBuilder).shouldBeFalse()
        cssBuilder.toString() shouldBe ""
    }

    // --- compiler integration ---

    @Test
    fun `compiler generates real CSS for m-0 with spacing hooks`() {
        val css = compiler.compile("m-0")
        css shouldBe
            """
            .k-m-0 {
            margin: 0.0rem;
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler generates real CSS for mt-4 with spacing hooks`() {
        val css = compiler.compile("mt-4")
        css shouldBe
            """
            .k-mt-4 {
            margin-top: 1.0rem;
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler generates real CSS for p-4 with spacing hooks`() {
        val css = compiler.compile("p-4")
        css shouldBe
            """
            .k-p-4 {
            padding: 1.0rem;
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler generates real CSS for p-2 with spacing hooks`() {
        val css = compiler.compile("p-2")
        css shouldBe
            """
            .k-p-2 {
            padding: 0.5rem;
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler generates real CSS for mx-auto with spacing hooks`() {
        val css = compiler.compile("mx-auto")
        css shouldBe
            """
            .k-mx-auto {
            margin-left: auto;
            margin-right: auto;
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler generates real CSS for mt-auto with spacing hooks`() {
        val css = compiler.compile("mt-auto")
        css shouldBe
            """
            .k-mt-auto {
            margin-top: auto;
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler generates real CSS for p-auto with spacing hooks`() {
        val css = compiler.compile("p-auto")
        css shouldBe
            """
            .k-p-auto {
            padding: auto;
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler generates hover variant css with spacing hooks`() {
        val css = compiler.compile("hover:mt-2")
        css shouldBe
            """
            .k-hover\:mt-2:hover {
            margin-top: 0.5rem;
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler marks flex as unsupported since it is not a spacing token`() {
        val css = compiler.compile("flex")
        css shouldBe
            """
            :root {
            --kolo-unsupported-0: "flex";
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler generates multiple tokens in sequence`() {
        val css = compiler.compile("m-0;mb-3")
        css shouldBe
            """
            .k-m-0 {
            margin: 0.0rem;
            }
            .k-mb-3 {
            margin-bottom: 0.75rem;
            }
            
            """.trimIndent()
    }

    private data class UnsupportedToken(
        override val raw: String
    ) : Token
}
