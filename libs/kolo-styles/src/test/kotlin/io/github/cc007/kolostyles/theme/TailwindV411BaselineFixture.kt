package io.github.cc007.kolostyles.theme

/**
 * Independent expected inventory from Tailwind v4.1.11's `@theme default`.
 * This reference verifies completeness; it never supplies runtime values.
 */
internal object TailwindV411BaselineFixture {
    private val colorFamilies = listOf(
        "red", "orange", "amber", "yellow", "lime", "green", "emerald", "teal", "cyan",
        "sky", "blue", "indigo", "violet", "purple", "fuchsia", "pink", "rose", "slate",
        "gray", "zinc", "neutral", "stone",
    )
    private val colorScales = listOf("50", "100", "200", "300", "400", "500", "600", "700", "800", "900", "950")

    val propertyNames: List<String> = buildList {
        addAll(colorFamilies.flatMap { family -> colorScales.map { scale -> "--k-color-$family-$scale" } })
        addAll(listOf("--k-color-black", "--k-color-white"))
        add("--k-spacing")
        addAll(listOf("--k-font-sans", "--k-font-serif", "--k-font-mono"))
        addAll(listOf("sm", "md", "lg", "xl", "2xl").map { "--k-breakpoint-$it" })
        addAll(listOf("xs", "sm", "base", "lg", "xl", "2xl", "3xl", "4xl", "5xl", "6xl", "7xl", "8xl", "9xl").flatMap {
            listOf("--k-text-$it", "--k-text-$it--line-height")
        })
        addAll(listOf("xs", "sm", "md", "lg", "xl", "2xl", "3xl", "4xl").map { "--k-radius-$it" })
        addAll(listOf("xs", "sm", "md", "lg", "xl", "2xl", "3xl").map { "--k-blur-$it" })
        addAll(listOf("xs", "sm", "md", "lg", "xl", "2xl").map { "--k-drop-shadow-$it" })
        addAll(listOf("2xs", "xs", "sm", "md", "lg", "xl", "2xl").map { "--k-shadow-$it" })
        addAll(listOf("2xs", "xs", "sm", "md", "lg").map { "--k-text-shadow-$it" })
        addAll(listOf("2xs", "xs", "sm").map { "--k-inset-shadow-$it" })
        addAll(listOf("3xs", "2xs", "xs", "sm", "md", "lg", "xl", "2xl", "3xl", "4xl", "5xl", "6xl", "7xl").map { "--k-container-$it" })
        addAll(listOf("thin", "extralight", "light", "normal", "medium", "semibold", "bold", "extrabold", "black").map { "--k-font-weight-$it" })
        addAll(listOf("tighter", "tight", "normal", "wide", "wider", "widest").map { "--k-tracking-$it" })
        addAll(listOf("tight", "snug", "normal", "relaxed", "loose").map { "--k-leading-$it" })
        addAll(listOf("in", "out", "in-out").map { "--k-ease-$it" })
        addAll(listOf("spin", "ping", "pulse", "bounce").map { "--k-animate-$it" })
        addAll(
            listOf(
                "--k-perspective-dramatic", "--k-perspective-near", "--k-perspective-normal",
                "--k-perspective-midrange", "--k-perspective-distant", "--k-aspect-video",
                "--k-default-transition-duration", "--k-default-transition-timing-function",
                "--k-default-font-family", "--k-default-font-feature-settings", "--k-default-font-variation-settings",
                "--k-default-mono-font-family", "--k-default-mono-font-feature-settings", "--k-default-mono-font-variation-settings",
            ),
        )
    }

    val keyframeAssociations = mapOf(
        "--k-animate-spin" to "spin",
        "--k-animate-ping" to "ping",
        "--k-animate-pulse" to "pulse",
        "--k-animate-bounce" to "bounce",
    )
}
