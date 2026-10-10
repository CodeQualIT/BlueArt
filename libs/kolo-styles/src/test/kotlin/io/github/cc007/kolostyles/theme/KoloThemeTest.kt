package io.github.cc007.kolostyles.theme

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class KoloThemeTest {
    @Test
    fun `baseline matches the pinned Tailwind fixture property and keyframe inventory`() {
        val baseline = KoloDefaultTheme.baseline()
        val properties = baseline.entries.map(ThemeEntry::property)

        KoloDefaultTheme.TAILWIND_VERSION shouldBe "4.1.11"
        properties.distinct().size shouldBe properties.size
        properties.shouldContainExactlyInAnyOrder(TailwindV411BaselineFixture.propertyNames)
        baseline.keyframes.map(ThemeKeyframe::name).shouldContainExactly(TailwindV411BaselineFixture.keyframeAssociations.values)
        TailwindV411BaselineFixture.keyframeAssociations.forEach { (property, keyframe) ->
            baseline.entry(property).keyframe shouldBe keyframe
            baseline.keyframe(keyframe).name shouldBe keyframe
        }
        themeNamespacePrefixes.forEach { prefix ->
            properties.any { it.removePrefix("--k-").startsWith(prefix) } shouldBe true
        }
        baseline.entry("--k-color-red-500").value shouldBe "oklch(63.7% 0.237 25.331)"
        baseline.entry("--k-color-stone-950").value shouldBe "oklch(14.7% 0.004 49.25)"
        baseline.entries.filter { it.property.startsWith("--k-color-") }.none { it.value == "initial" } shouldBe true
    }

    @Test
    fun `baseline explicitly defines remaining family values without fallback defaults`() {
        val baseline = KoloDefaultTheme.baseline()
        val expectedValues = mapOf(
            "--k-color-black" to "#000",
            "--k-color-white" to "#fff",
            "--k-text-xs--line-height" to "calc(1 / 0.75)",
            "--k-text-4xl--line-height" to "calc(2.5 / 2.25)",
            "--k-text-9xl--line-height" to "1",
            "--k-blur-3xl" to "64px",
            "--k-drop-shadow-2xl" to "0 25px 25px rgb(0 0 0 / 0.15)",
            "--k-text-shadow-2xs" to "0px 1px 0px rgb(0 0 0 / 0.15)",
            "--k-inset-shadow-sm" to "inset 0 2px 4px rgb(0 0 0 / 0.05)",
            "--k-perspective-distant" to "1200px",
            "--k-aspect-video" to "16 / 9",
        )
        expectedValues.forEach { (property, value) -> baseline.entry(property).value shouldBe value }
        baseline.entries.filter { it.value == "initial" }.map(ThemeEntry::property).shouldContainExactly(
            "--k-default-font-feature-settings", "--k-default-font-variation-settings",
            "--k-default-mono-font-feature-settings", "--k-default-mono-font-variation-settings",
        )
        shouldThrow<IllegalArgumentException> { baseline.entry("--k-color-missing-500") }
    }

    @Test
    fun `baseline emits colors first with stable family and shade order`() {
        val properties = KoloDefaultTheme.baseline().entries.map(ThemeEntry::property)
        properties.filter { it.startsWith("--k-color-") }.shouldContainExactly(
            TailwindV411BaselineFixture.propertyNames.filter { it.startsWith("--k-color-") },
        )
        properties.filter { it.startsWith("--k-font-") }.shouldContainExactly(
            "--k-font-sans", "--k-font-serif", "--k-font-mono",
            "--k-font-weight-thin", "--k-font-weight-extralight", "--k-font-weight-light",
            "--k-font-weight-normal", "--k-font-weight-medium", "--k-font-weight-semibold",
            "--k-font-weight-bold", "--k-font-weight-extrabold", "--k-font-weight-black",
        )
        properties.first() shouldBe "--k-color-red-50"
        properties.indexOf("--k-color-white") + 1 shouldBe properties.indexOf("--k-font-sans")
        val sizes = listOf("xs", "sm", "base", "lg", "xl", "2xl", "3xl", "4xl", "5xl", "6xl", "7xl", "8xl", "9xl")
        val textProperties = sizes.map { "--k-text-$it" } + sizes.map { "--k-text-$it--line-height" }
        properties.subList(properties.indexOf("--k-text-xs"), properties.indexOf("--k-font-weight-thin"))
            .shouldContainExactly(textProperties)
        properties.last() shouldBe "--k-aspect-video"
    }

    @Test
    fun `composition replaces baseline entries while retaining baseline order and adding by contribution order`() {
        val composer = KoloThemeComposer(
            EffectiveKoloTheme(
                entries = listOf(ThemeEntry("--k-font-sans", "baseline"), ThemeEntry("--k-spacing", "1rem")),
                keyframes = emptyList(),
            ),
        )

        val theme = composer.compose(
            listOf(
                ThemeContribution("later", 20, listOf(ThemeEntry("--k-color-blueart-link", "#1"))),
                ThemeContribution("earlier", 10, listOf(ThemeEntry("--k-font-sans", "replacement"), ThemeEntry("--k-color-blueart-page-background", "#2"))),
            ),
        )

        theme.entries.map(ThemeEntry::property) shouldContainExactly listOf(
            "--k-font-sans", "--k-spacing", "--k-color-blueart-page-background", "--k-color-blueart-link",
        )
        theme.entry("--k-font-sans").value shouldBe "replacement"
    }

    @Test
    fun `composition orders equal precedence contributions by id and accepts identical entries`() {
        val composer = KoloThemeComposer(EffectiveKoloTheme(emptyList(), emptyList()))
        val shared = ThemeEntry("--k-color-blueart-link", "#111")
        val contributions = listOf(
            ThemeContribution("second", 1, listOf(ThemeEntry("--k-color-blueart-background", "#222"), shared)),
            ThemeContribution("first", 1, listOf(shared, ThemeEntry("--k-font-sans", "sans-serif"))),
        )

        val theme = composer.compose(contributions)

        theme.entries shouldContainExactly listOf(
            shared, ThemeEntry("--k-font-sans", "sans-serif"), ThemeEntry("--k-color-blueart-background", "#222"),
        )
        composer.compose(contributions.reversed()).entries shouldContainExactly theme.entries
    }

    @Test
    fun `composition rejects invalid and conflicting contributions with diagnostics`() {
        shouldThrow<IllegalArgumentException> {
            ThemeContribution("invalid", -1, emptyList())
        }
        shouldThrow<IllegalArgumentException> {
            ThemeEntry("--k-unknown-value", "anything")
        }
        shouldThrow<IllegalStateException> {
            KoloThemeComposer().compose(
                listOf(
                    ThemeContribution("first", 1, listOf(ThemeEntry("--k-color-blueart-link", "#111"))),
                    ThemeContribution("second", 1, listOf(ThemeEntry("--k-color-blueart-link", "#222"))),
                ),
            )
        }.message shouldBe "Conflicting theme property --k-color-blueart-link at precedence 1 from first, second"
    }

    @Test
    fun `composition rejects duplicate ids and unknown keyframes`() {
        shouldThrow<IllegalArgumentException> {
            KoloThemeComposer().compose(
                listOf(
                    ThemeContribution("same", 1, emptyList()),
                    ThemeContribution("same", 2, emptyList()),
                ),
            )
        }
        shouldThrow<IllegalArgumentException> {
            KoloThemeComposer().compose(
                listOf(ThemeContribution("animations", 1, listOf(ThemeEntry("--k-animate-custom", "custom 1s", "missing")))),
            )
        }
    }

    @Test
    fun `effective theme exposes immutable request-time metadata`() {
        val theme = KoloDefaultTheme.baseline()

        shouldThrow<UnsupportedOperationException> {
            @Suppress("UNCHECKED_CAST")
            (theme.entries as MutableList<ThemeEntry>).add(ThemeEntry("--k-color-blueart-link", "#000"))
        }
    }
}
