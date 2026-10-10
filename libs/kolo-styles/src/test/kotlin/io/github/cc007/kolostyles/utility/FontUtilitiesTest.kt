package io.github.cc007.kolostyles.utility

import io.github.cc007.kolostyles.compiler.KoloCssCompiler
import io.github.cc007.kolostyles.compiler.font.FontGeneratorHook
import io.github.cc007.kolostyles.compiler.font.FontParserHook
import io.github.cc007.kolostyles.compiler.layout.display.DisplayGeneratorHook
import io.github.cc007.kolostyles.compiler.layout.display.DisplayParserHook
import io.github.cc007.kolostyles.compiler.spacing.SpacingGeneratorHook
import io.github.cc007.kolostyles.compiler.spacing.SpacingParserHook
import io.github.cc007.kolostyles.dsl.font.FontFamily
import io.github.cc007.kolostyles.dsl.font.FontWeight
import io.github.cc007.kolostyles.theme.*
import io.github.cc007.kolostyles.web.KoloCssController
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
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
    fun `parser accepts baseline font weights and rejects unregistered weights`() {
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
            font-family: var(--k-font-sans);
            }
            
            """.trimIndent()
    }

    @Test
    fun `compiler emits only the requested canonical font property without a raw font stack`() {
        val css = fontOnlyCompiler.compile("font-serif;font-serif")

        css shouldContain "--k-font-serif: ui-serif, Georgia, Cambria, 'Times New Roman', Times, serif;"
        css shouldContain "font-family: var(--k-font-serif);"
        css.split("--k-font-serif:").size - 1 shouldBe 1
    }

    @Test
    fun `all supported sizes and weights request their baseline values without literals in utility rules`() {
        val baseline = KoloDefaultTheme.baseline()
        listOf(
            "text-xs", "text-sm", "text-base", "text-lg", "text-xl", "text-2xl", "text-3xl",
            "text-4xl", "text-5xl", "text-6xl", "text-7xl", "text-8xl", "text-9xl",
        ).forEach { utility ->
            val css = fontOnlyCompiler.compile(utility)
            val property = "--k-$utility"
            css shouldContain "$property: ${baseline.entry(property).value};"
            css shouldContain "font-size: var($property);"
            css shouldContain "$property--line-height: ${baseline.entry("$property--line-height").value};"
            css shouldContain "line-height: var($property--line-height);"
        }
        listOf(
            "thin", "extralight", "light", "normal", "medium", "semibold", "bold", "extrabold", "black",
        ).forEach { name ->
            val css = fontOnlyCompiler.compile("font-$name")
            val property = "--k-font-weight-$name"
            css shouldContain "$property: ${baseline.entry(property).value};"
            css shouldContain "font-weight: var($property);"
        }
    }

    @Test
    fun `contributed fonts and overridden sizes and weights resolve through the effective theme`() {
        val theme = KoloThemeComposer().compose(
            listOf(
                ThemeContribution(
                    "typography", 1,
                    listOf(
                        ThemeEntry("--k-font-display", "'Display Face', sans-serif"),
                        ThemeEntry("--k-font-bold", "'Not A Weight', serif"),
                        ThemeEntry("--k-font-sans--font-feature-settings", "'liga'"),
                        ThemeEntry("--k-font-weight-bold", "750"),
                        ThemeEntry("--k-font-weight-book", "450"),
                        ThemeEntry("--k-font-weight-950", "950"),
                        ThemeEntry("--k-text-2xl", "1.75rem"),
                        ThemeEntry("--k-text-2xl--line-height", "1.6"),
                    ),
                ),
            ),
        )
        val themeParser = FontParserHook(theme)
        val compiler = KoloCssCompiler(
            parserHooks = listOf(themeParser), generatorHooks = listOf(generator), effectiveTheme = theme,
        )
        val css = KoloCssController(compiler).koloStylesheet(
            version = "test", kolo = "font-display;hover:font-bold;md:text-2xl;font-display;hover:font-book;font-950",
        ).body!!

        css shouldContain "--k-font-display: 'Display Face', sans-serif;"
        css shouldContain "--k-font-weight-bold: 750;"
        css shouldContain "--k-text-2xl: 1.75rem;"
        css shouldContain "--k-text-2xl--line-height: 1.6;"
        css shouldContain "--k-font-weight-book: 450;"
        css shouldContain "--k-font-weight-950: 950;"
        css shouldContain "font-family: var(--k-font-display);"
        css shouldContain "font-weight: var(--k-font-weight-bold);"
        css shouldContain "font-size: var(--k-text-2xl);"
        css shouldContain "line-height: var(--k-text-2xl--line-height);"
        css shouldContain "font-weight: var(--k-font-weight-book);"
        css shouldContain "font-weight: var(--k-font-weight-950);"
        css.split("--k-font-display:").size - 1 shouldBe 1
        css.contains("--k-font-bold:") shouldBe false
        css.contains("--k-font-sans:") shouldBe false
        themeParser.parse("font-unknown").shouldBeNull()
        themeParser.parse("font-weight-bold").shouldBeNull()
        themeParser.parse("font-sans--font-feature-settings").shouldBeNull()
        compiler.compile("font-unknown") shouldContain "--kolo-unsupported-0: \"font-unknown\";"
    }

    @Test
    fun `font size companion properties are requested once across variants`() {
        val css = fontOnlyCompiler.compile("text-base;hover:text-base;md:text-base")

        css.split("--k-text-base:").size - 1 shouldBe 1
        css.split("--k-text-base--line-height:").size - 1 shouldBe 1
        css.split("line-height: var(--k-text-base--line-height);").size - 1 shouldBe 3
        css.contains("--k-text-lg") shouldBe false
    }

    @Test
    fun `weight metadata is not a custom weight utility`() {
        val theme = KoloThemeComposer().compose(
            listOf(
                ThemeContribution(
                    "weight-metadata", 1,
                    listOf(ThemeEntry("--k-font-weight-book--metadata", "450")),
                ),
            ),
        )
        FontParserHook(theme).parse("font-book--metadata").shouldBeNull()
    }

    @Test
    fun `parser initialization rejects ambiguous family and weight utilities`() {
        val theme = KoloThemeComposer().compose(
            listOf(
                ThemeContribution(
                    "ambiguous-fonts", 1,
                    listOf(
                        ThemeEntry("--k-font-book", "'Book Face', serif"),
                        ThemeEntry("--k-font-weight-book", "450"),
                    ),
                ),
            ),
        )

        shouldThrow<IllegalArgumentException> { FontParserHook(theme) }.message shouldContain "font-book"
    }

    @Test
    fun `overriding a size retains its baseline companion line height`() {
        val theme = KoloThemeComposer().compose(
            listOf(ThemeContribution("size-override", 1, listOf(ThemeEntry("--k-text-lg", "1.2rem")))),
        )
        val compiler = KoloCssCompiler(listOf(FontParserHook(theme)), listOf(generator), theme)
        val css = compiler.compile("text-lg")

        css shouldContain "--k-text-lg: 1.2rem;"
        css shouldContain "--k-text-lg--line-height: calc(1.75 / 1.125);"
        css shouldContain "font-size: var(--k-text-lg);"
        css shouldContain "line-height: var(--k-text-lg--line-height);"
    }

    @Test
    fun `font weight names reject arbitrary CSS and metadata suffixes`() {
        listOf("[450]", "book--metadata", "450; color: red", "").forEach {
            shouldThrow<IllegalArgumentException> { FontWeight(it) }
        }
        FontWeight("book").name shouldBe "book"
        FontWeight("950").name shouldBe "950"
    }

    @Test
    fun `font parsing requires an existing effective property`() {
        val emptyParser = FontParserHook(EffectiveKoloTheme(emptyList(), emptyList()))

        listOf("font-sans", "font-bold", "text-2xl").forEach { emptyParser.parse(it).shouldBeNull() }
    }

    @Test
    fun `font family names reject raw CSS and reserved utility names`() {
        listOf("bold", "weight-bold", "sans--font-feature-settings", "950", "[Arial]", "Arial, serif").forEach {
            shouldThrow<IllegalArgumentException> { FontFamily(it) }
        }
        FontFamily("display-face").name shouldBe "display-face"
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
            font-weight: var(--k-font-weight-semibold);
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
            font-size: var(--k-text-2xl);
            line-height: var(--k-text-2xl--line-height);
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
            :root {
            --k-font-weight-semibold: 600;
            }

            @media (width >= 48rem) {
            .k-md\:grid {
            display: grid;
            }
            }
            .k-mt-2 {
            margin-top: 0.5rem;
            }
            .k-font-semibold {
            font-weight: var(--k-font-weight-semibold);
            }
            .k-hover\:inline-flex:hover {
            display: inline-flex;
            }
            
            """.trimIndent()
    }
}
