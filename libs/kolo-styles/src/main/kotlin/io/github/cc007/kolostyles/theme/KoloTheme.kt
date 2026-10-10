package io.github.cc007.kolostyles.theme

import java.util.*

private val themePropertyPattern = Regex("^--k-[a-z0-9][a-z0-9-]*$")
private val themeValuePattern = Regex("^[^;{}]+$")

val themeNamespacePrefixes = setOf(
    "font-", "color-", "spacing", "breakpoint-", "container-", "text-", "font-weight-",
    "tracking-", "leading-", "radius-", "shadow-", "inset-shadow-", "drop-shadow-",
    "text-shadow-", "ease-", "animate-", "blur-", "perspective-", "aspect-", "default-",
)

data class ThemeEntry(
    val property: String,
    val value: String,
    val keyframe: String? = null,
) {
    init {
        require(themePropertyPattern.matches(property)) { "Theme property must use the canonical --k-* namespace: $property" }
        require(themeNamespacePrefixes.any { property.removePrefix("--k-").startsWith(it) }) {
            "Theme property must use a supported Tailwind namespace: $property"
        }
        require(value.isNotBlank() && themeValuePattern.matches(value)) {
            "Theme value must be a non-empty CSS value without declaration delimiters for $property"
        }
    }
}

data class ThemeKeyframe(
    val name: String,
    val css: String,
) {
    init {
        require(name.matches(Regex("[a-z][a-z0-9-]*"))) { "Invalid keyframe name: $name" }
        require(css.isNotBlank()) { "Keyframe $name must have CSS" }
    }
}

data class ThemeContribution(
    val id: String,
    val precedence: Int,
    val entries: List<ThemeEntry>,
    val keyframes: List<ThemeKeyframe> = emptyList(),
) {
    init {
        require(id.matches(Regex("[a-z][a-z0-9-]*"))) { "Invalid theme contribution id: $id" }
        require(precedence >= 0) { "Theme contribution $id must use a non-negative precedence" }
        require(entries.map(ThemeEntry::property).distinct().size == entries.size) {
            "Theme contribution $id defines a property more than once"
        }
    }
}

class EffectiveKoloTheme internal constructor(
    entries: List<ThemeEntry>,
    keyframes: List<ThemeKeyframe>,
) {
    val entries: List<ThemeEntry> = Collections.unmodifiableList(entries.toList())
    val keyframes: List<ThemeKeyframe> = Collections.unmodifiableList(keyframes.toList())
    private val entriesByProperty = this.entries.associateBy(ThemeEntry::property)
    private val keyframesByName = this.keyframes.associateBy(ThemeKeyframe::name)

    fun entry(property: String): ThemeEntry =
        requireNotNull(entriesByProperty[property]) { "Unknown effective theme property: $property" }

    fun keyframe(name: String): ThemeKeyframe =
        requireNotNull(keyframesByName[name]) { "Unknown effective theme keyframe: $name" }
}

object KoloDefaultTheme {
    const val TAILWIND_VERSION = "4.1.11"

    private val colorScales = listOf("50", "100", "200", "300", "400", "500", "600", "700", "800", "900", "950")

    // OKLCH components for shades 50 through 950, in colorScales order.
    private val colorValues = mapOf(
        "red"     to "97.1% 0.013  17.38  | 93.6% 0.032  17.717 | 88.5% 0.062  18.334 | 80.8% 0.114  19.571 | 70.4% 0.191  22.216 | 63.7% 0.237  25.331 | 57.7% 0.245  27.325 | 50.5% 0.213  27.518 | 44.4% 0.177  26.899 | 39.6% 0.141  25.723 | 25.8% 0.092  26.042",
        "orange"  to "98%   0.016  73.684 | 95.4% 0.038  75.164 | 90.1% 0.076  70.697 | 83.7% 0.128  66.29  | 75%   0.183  55.934 | 70.5% 0.213  47.604 | 64.6% 0.222  41.116 | 55.3% 0.195  38.402 | 47%   0.157  37.304 | 40.8% 0.123  38.172 | 26.6% 0.079  36.259",
        "amber"   to "98.7% 0.022  95.277 | 96.2% 0.059  95.617 | 92.4% 0.12   95.746 | 87.9% 0.169  91.605 | 82.8% 0.189  84.429 | 76.9% 0.188  70.08  | 66.6% 0.179  58.318 | 55.5% 0.163  48.998 | 47.3% 0.137  46.201 | 41.4% 0.112  45.904 | 27.9% 0.077  45.635",
        "yellow"  to "98.7% 0.026 102.212 | 97.3% 0.071 103.193 | 94.5% 0.129 101.54  | 90.5% 0.182  98.111 | 85.2% 0.199  91.936 | 79.5% 0.184  86.047 | 68.1% 0.162  75.834 | 55.4% 0.135  66.442 | 47.6% 0.114  61.907 | 42.1% 0.095  57.708 | 28.6% 0.066  53.813",
        "lime"    to "98.6% 0.031 120.757 | 96.7% 0.067 122.328 | 93.8% 0.127 124.321 | 89.7% 0.196 126.665 | 84.1% 0.238 128.85  | 76.8% 0.233 130.85  | 64.8% 0.2   131.684 | 53.2% 0.157 131.589 | 45.3% 0.124 130.933 | 40.5% 0.101 131.063 | 27.4% 0.072 132.109",
        "green"   to "98.2% 0.018 155.826 | 96.2% 0.044 156.743 | 92.5% 0.084 155.995 | 87.1% 0.15  154.449 | 79.2% 0.209 151.711 | 72.3% 0.219 149.579 | 62.7% 0.194 149.214 | 52.7% 0.154 150.069 | 44.8% 0.119 151.328 | 39.3% 0.095 152.535 | 26.6% 0.065 152.934",
        "emerald" to "97.9% 0.021 166.113 | 95%   0.052 163.051 | 90.5% 0.093 164.15  | 84.5% 0.143 164.978 | 76.5% 0.177 163.223 | 69.6% 0.17  162.48  | 59.6% 0.145 163.225 | 50.8% 0.118 165.612 | 43.2% 0.095 166.913 | 37.8% 0.077 168.94  | 26.2% 0.051 172.552",
        "teal"    to "98.4% 0.014 180.72  | 95.3% 0.051 180.801 | 91%   0.096 180.426 | 85.5% 0.138 181.071 | 77.7% 0.152 181.912 | 70.4% 0.14  182.503 | 60%   0.118 184.704 | 51.1% 0.096 186.391 | 43.7% 0.078 188.216 | 38.6% 0.063 188.416 | 27.7% 0.046 192.524",
        "cyan"    to "98.4% 0.019 200.873 | 95.6% 0.045 203.388 | 91.7% 0.08  205.041 | 86.5% 0.127 207.078 | 78.9% 0.154 211.53  | 71.5% 0.143 215.221 | 60.9% 0.126 221.723 | 52%   0.105 223.128 | 45%   0.085 224.283 | 39.8% 0.07  227.392 | 30.2% 0.056 229.695",
        "sky"     to "97.7% 0.013 236.62  | 95.1% 0.026 236.824 | 90.1% 0.058 230.902 | 82.8% 0.111 230.318 | 74.6% 0.16  232.661 | 68.5% 0.169 237.323 | 58.8% 0.158 241.966 | 50%   0.134 242.749 | 44.3% 0.11  240.79  | 39.1% 0.09  240.876 | 29.3% 0.066 243.157",
        "blue"    to "97%   0.014 254.604 | 93.2% 0.032 255.585 | 88.2% 0.059 254.128 | 80.9% 0.105 251.813 | 70.7% 0.165 254.624 | 62.3% 0.214 259.815 | 54.6% 0.245 262.881 | 48.8% 0.243 264.376 | 42.4% 0.199 265.638 | 37.9% 0.146 265.522 | 28.2% 0.091 267.935",
        "indigo"  to "96.2% 0.018 272.314 | 93%   0.034 272.788 | 87%   0.065 274.039 | 78.5% 0.115 274.713 | 67.3% 0.182 276.935 | 58.5% 0.233 277.117 | 51.1% 0.262 276.966 | 45.7% 0.24  277.023 | 39.8% 0.195 277.366 | 35.9% 0.144 278.697 | 25.7% 0.09  281.288",
        "violet"  to "96.9% 0.016 293.756 | 94.3% 0.029 294.588 | 89.4% 0.057 293.283 | 81.1% 0.111 293.571 | 70.2% 0.183 293.541 | 60.6% 0.25  292.717 | 54.1% 0.281 293.009 | 49.1% 0.27  292.581 | 43.2% 0.232 292.759 | 38%   0.189 293.745 | 28.3% 0.141 291.089",
        "purple"  to "97.7% 0.014 308.299 | 94.6% 0.033 307.174 | 90.2% 0.063 306.703 | 82.7% 0.119 306.383 | 71.4% 0.203 305.504 | 62.7% 0.265 303.9   | 55.8% 0.288 302.321 | 49.6% 0.265 301.924 | 43.8% 0.218 303.724 | 38.1% 0.176 304.987 | 29.1% 0.149 302.717",
        "fuchsia" to "97.7% 0.017 320.058 | 95.2% 0.037 318.852 | 90.3% 0.076 319.62  | 83.3% 0.145 321.434 | 74%   0.238 322.16  | 66.7% 0.295 322.15  | 59.1% 0.293 322.896 | 51.8% 0.253 323.949 | 45.2% 0.211 324.591 | 40.1% 0.17  325.612 | 29.3% 0.136 325.661",
        "pink"    to "97.1% 0.014 343.198 | 94.8% 0.028 342.258 | 89.9% 0.061 343.231 | 82.3% 0.12  346.018 | 71.8% 0.202 349.761 | 65.6% 0.241 354.308 | 59.2% 0.249   0.584 | 52.5% 0.223   3.958 | 45.9% 0.187   3.815 | 40.8% 0.153   2.432 | 28.4% 0.109   3.907",
        "rose"    to "96.9% 0.015  12.422 | 94.1% 0.03   12.58  | 89.2% 0.058  10.001 | 81%   0.117  11.638 | 71.2% 0.194  13.428 | 64.5% 0.246  16.439 | 58.6% 0.253  17.585 | 51.4% 0.222 16.935  | 45.5% 0.188  13.697 | 41%   0.159  10.272 | 27.1% 0.105  12.094",
        "slate"   to "98.4% 0.003 247.858 | 96.8% 0.007 247.896 | 92.9% 0.013 255.508 | 86.9% 0.022 252.894 | 70.4% 0.04  256.788 | 55.4% 0.046 257.417 | 44.6% 0.043 257.281 | 37.2% 0.044 257.287 | 27.9% 0.041 260.031 | 20.8% 0.042 265.755 | 12.9% 0.042 264.695",
        "gray"    to "98.5% 0.002 247.839 | 96.7% 0.003 264.542 | 92.8% 0.006 264.531 | 87.2% 0.01  258.338 | 70.7% 0.022 261.325 | 55.1% 0.027 264.364 | 44.6% 0.03  256.802 | 37.3% 0.034 259.733 | 27.8% 0.033 256.848 | 21%   0.034 264.665 | 13%   0.028 261.692",
        "zinc"    to "98.5% 0       0     | 96.7% 0.001 286.375 | 92%   0.004 286.32  | 87.1% 0.006 286.286 | 70.5% 0.015 286.067 | 55.2% 0.016 285.938 | 44.2% 0.017 285.786 | 37%   0.013 285.805 | 27.4% 0.006 286.033 | 21%   0.006 285.885 | 14.1% 0.005 285.823",
        "neutral" to "98.5% 0       0     | 97%   0       0     | 92.2% 0       0     | 87%   0       0     | 70.8% 0       0     | 55.6% 0       0     | 43.9% 0       0     | 37.1% 0       0     | 26.9% 0       0     | 20.5% 0       0     | 14.5% 0       0    ",
        "stone"   to "98.5% 0.001 106.423 | 97%   0.001 106.424 | 92.3% 0.003  48.717 | 86.9% 0.005  56.366 | 70.9% 0.01   56.259 | 55.3% 0.013  58.071 | 44.4% 0.011  73.639 | 37.4% 0.01   67.558 | 26.8% 0.007  34.298 | 21.6% 0.006  56.043 | 14.7% 0.004  49.25 ",
    )

    private val colorEntries = colorValues.flatMap { (family, values) ->
        val shades = values.split('|')
        require(shades.size == colorScales.size) { "Color $family must define every baseline shade" }
        colorScales.zip(shades) { scale, shadeRaw ->
            val shade = shadeRaw.trim().replace(Regex("\\s+"), " ")
            ThemeEntry("--k-color-$family-$scale", "oklch($shade)")
        }
    } + listOf(
        ThemeEntry("--k-color-black", "#000"),
        ThemeEntry("--k-color-white", "#fff"),
    )

    /*
     * This inventory mirrors Tailwind v4.1.11's default theme-variable namespaces.
     * Values stay inert until a generator requests them.
     */
    private val entries = colorEntries + listOf(
        ThemeEntry("--k-font-sans", "ui-sans-serif, system-ui, sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol', 'Noto Color Emoji'"),
        ThemeEntry("--k-font-serif", "ui-serif, Georgia, Cambria, 'Times New Roman', Times, serif"),
        ThemeEntry("--k-font-mono", "ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace"),

        ThemeEntry("--k-text-xs", "0.75rem"),
        ThemeEntry("--k-text-sm", "0.875rem"),
        ThemeEntry("--k-text-base", "1rem"),
        ThemeEntry("--k-text-lg", "1.125rem"),
        ThemeEntry("--k-text-xl", "1.25rem"),
        ThemeEntry("--k-text-2xl", "1.5rem"),
        ThemeEntry("--k-text-3xl", "1.875rem"),
        ThemeEntry("--k-text-4xl", "2.25rem"),
        ThemeEntry("--k-text-5xl", "3rem"),
        ThemeEntry("--k-text-6xl", "3.75rem"),
        ThemeEntry("--k-text-7xl", "4.5rem"),
        ThemeEntry("--k-text-8xl", "6rem"),
        ThemeEntry("--k-text-9xl", "8rem"),

        ThemeEntry("--k-text-xs--line-height", "calc(1 / 0.75)"),
        ThemeEntry("--k-text-sm--line-height", "calc(1.25 / 0.875)"),
        ThemeEntry("--k-text-base--line-height", "calc(1.5 / 1)"),
        ThemeEntry("--k-text-lg--line-height", "calc(1.75 / 1.125)"),
        ThemeEntry("--k-text-xl--line-height", "calc(1.75 / 1.25)"),
        ThemeEntry("--k-text-2xl--line-height", "calc(2 / 1.5)"),
        ThemeEntry("--k-text-3xl--line-height", "calc(2.25 / 1.875)"),
        ThemeEntry("--k-text-4xl--line-height", "calc(2.5 / 2.25)"),
        ThemeEntry("--k-text-5xl--line-height", "1"),
        ThemeEntry("--k-text-6xl--line-height", "1"),
        ThemeEntry("--k-text-7xl--line-height", "1"),
        ThemeEntry("--k-text-8xl--line-height", "1"),
        ThemeEntry("--k-text-9xl--line-height", "1"),

        ThemeEntry("--k-font-weight-thin", "100"),
        ThemeEntry("--k-font-weight-extralight", "200"),
        ThemeEntry("--k-font-weight-light", "300"),
        ThemeEntry("--k-font-weight-normal", "400"),
        ThemeEntry("--k-font-weight-medium", "500"),
        ThemeEntry("--k-font-weight-semibold", "600"),
        ThemeEntry("--k-font-weight-bold", "700"),
        ThemeEntry("--k-font-weight-extrabold", "800"),
        ThemeEntry("--k-font-weight-black", "900"),

        ThemeEntry("--k-tracking-tighter", "-0.05em"),
        ThemeEntry("--k-tracking-tight", "-0.025em"),
        ThemeEntry("--k-tracking-normal", "0em"),
        ThemeEntry("--k-tracking-wide", "0.025em"),
        ThemeEntry("--k-tracking-wider", "0.05em"),
        ThemeEntry("--k-tracking-widest", "0.1em"),

        ThemeEntry("--k-leading-tight", "1.25"),
        ThemeEntry("--k-leading-snug", "1.375"),
        ThemeEntry("--k-leading-normal", "1.5"),
        ThemeEntry("--k-leading-relaxed", "1.625"),
        ThemeEntry("--k-leading-loose", "2"),

        ThemeEntry("--k-spacing", "0.25rem"),

        ThemeEntry("--k-radius-xs", "0.125rem"),
        ThemeEntry("--k-radius-sm", "0.25rem"),
        ThemeEntry("--k-radius-md", "0.375rem"),
        ThemeEntry("--k-radius-lg", "0.5rem"),
        ThemeEntry("--k-radius-xl", "0.75rem"),
        ThemeEntry("--k-radius-2xl", "1rem"),
        ThemeEntry("--k-radius-3xl", "1.5rem"),
        ThemeEntry("--k-radius-4xl", "2rem"),

        ThemeEntry("--k-shadow-2xs", "0 1px rgb(0 0 0 / 0.05)"),
        ThemeEntry("--k-shadow-xs", "0 1px 2px 0 rgb(0 0 0 / 0.05)"),
        ThemeEntry("--k-shadow-sm", "0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)"),
        ThemeEntry("--k-shadow-md", "0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)"),
        ThemeEntry("--k-shadow-lg", "0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)"),
        ThemeEntry("--k-shadow-xl", "0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)"),
        ThemeEntry("--k-shadow-2xl", "0 25px 50px -12px rgb(0 0 0 / 0.25)"),

        ThemeEntry("--k-ease-in", "cubic-bezier(0.4, 0, 1, 1)"),
        ThemeEntry("--k-ease-out", "cubic-bezier(0, 0, 0.2, 1)"),
        ThemeEntry("--k-ease-in-out", "cubic-bezier(0.4, 0, 0.2, 1)"),

        ThemeEntry("--k-animate-spin", "spin 1s linear infinite", "spin"),
        ThemeEntry("--k-animate-ping", "ping 1s cubic-bezier(0, 0, 0.2, 1) infinite", "ping"),
        ThemeEntry("--k-animate-pulse", "pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite", "pulse"),
        ThemeEntry("--k-animate-bounce", "bounce 1s infinite", "bounce"),

        ThemeEntry("--k-breakpoint-sm", "40rem"),
        ThemeEntry("--k-breakpoint-md", "48rem"),
        ThemeEntry("--k-breakpoint-lg", "64rem"),
        ThemeEntry("--k-breakpoint-xl", "80rem"),
        ThemeEntry("--k-breakpoint-2xl", "96rem"),

        ThemeEntry("--k-container-3xs", "16rem"),
        ThemeEntry("--k-container-2xs", "18rem"),
        ThemeEntry("--k-container-xs", "20rem"),
        ThemeEntry("--k-container-sm", "24rem"),
        ThemeEntry("--k-container-md", "28rem"),
        ThemeEntry("--k-container-lg", "32rem"),
        ThemeEntry("--k-container-xl", "36rem"),
        ThemeEntry("--k-container-2xl", "42rem"),
        ThemeEntry("--k-container-3xl", "48rem"),
        ThemeEntry("--k-container-4xl", "56rem"),
        ThemeEntry("--k-container-5xl", "64rem"),
        ThemeEntry("--k-container-6xl", "72rem"),
        ThemeEntry("--k-container-7xl", "80rem"),

        ThemeEntry("--k-default-font-family", "var(--k-font-sans)"),
        ThemeEntry("--k-default-font-feature-settings", "initial"),
        ThemeEntry("--k-default-font-variation-settings", "initial"),
        ThemeEntry("--k-default-mono-font-family", "var(--k-font-mono)"),
        ThemeEntry("--k-default-mono-font-feature-settings", "initial"),
        ThemeEntry("--k-default-mono-font-variation-settings", "initial"),
        ThemeEntry("--k-default-transition-duration", "150ms"),
        ThemeEntry("--k-default-transition-timing-function", "cubic-bezier(0.4, 0, 0.2, 1)"),

        ThemeEntry("--k-blur-xs", "4px"),
        ThemeEntry("--k-blur-sm", "8px"),
        ThemeEntry("--k-blur-md", "12px"),
        ThemeEntry("--k-blur-lg", "16px"),
        ThemeEntry("--k-blur-xl", "24px"),
        ThemeEntry("--k-blur-2xl", "40px"),
        ThemeEntry("--k-blur-3xl", "64px"),

        ThemeEntry("--k-drop-shadow-xs", "0 1px 1px rgb(0 0 0 / 0.05)"),
        ThemeEntry("--k-drop-shadow-sm", "0 1px 2px rgb(0 0 0 / 0.15)"),
        ThemeEntry("--k-drop-shadow-md", "0 3px 3px rgb(0 0 0 / 0.12)"),
        ThemeEntry("--k-drop-shadow-lg", "0 4px 4px rgb(0 0 0 / 0.15)"),
        ThemeEntry("--k-drop-shadow-xl", "0 9px 7px rgb(0 0 0 / 0.1)"),
        ThemeEntry("--k-drop-shadow-2xl", "0 25px 25px rgb(0 0 0 / 0.15)"),

        ThemeEntry("--k-text-shadow-2xs", "0px 1px 0px rgb(0 0 0 / 0.15)"),
        ThemeEntry("--k-text-shadow-xs", "0px 1px 1px rgb(0 0 0 / 0.2)"),
        ThemeEntry("--k-text-shadow-sm", "0px 1px 0px rgb(0 0 0 / 0.075), 0px 1px 1px rgb(0 0 0 / 0.075), 0px 2px 2px rgb(0 0 0 / 0.075)"),
        ThemeEntry("--k-text-shadow-md", "0px 1px 1px rgb(0 0 0 / 0.1), 0px 1px 2px rgb(0 0 0 / 0.1), 0px 2px 4px rgb(0 0 0 / 0.1)"),
        ThemeEntry("--k-text-shadow-lg", "0px 1px 2px rgb(0 0 0 / 0.1), 0px 3px 2px rgb(0 0 0 / 0.1), 0px 4px 8px rgb(0 0 0 / 0.1)"),

        ThemeEntry("--k-inset-shadow-2xs", "inset 0 1px rgb(0 0 0 / 0.05)"),
        ThemeEntry("--k-inset-shadow-xs", "inset 0 1px 1px rgb(0 0 0 / 0.05)"),
        ThemeEntry("--k-inset-shadow-sm", "inset 0 2px 4px rgb(0 0 0 / 0.05)"),

        ThemeEntry("--k-perspective-dramatic", "100px"),
        ThemeEntry("--k-perspective-near", "300px"),
        ThemeEntry("--k-perspective-normal", "500px"),
        ThemeEntry("--k-perspective-midrange", "800px"),
        ThemeEntry("--k-perspective-distant", "1200px"),
        ThemeEntry("--k-aspect-video", "16 / 9"),
    )

    private val keyframes = listOf(
        ThemeKeyframe("spin", "@keyframes spin { to { transform: rotate(360deg); } }"),
        ThemeKeyframe("ping", "@keyframes ping { 75%, 100% { transform: scale(2); opacity: 0; } }"),
        ThemeKeyframe("pulse", "@keyframes pulse { 50% { opacity: 0.5; } }"),
        ThemeKeyframe("bounce", "@keyframes bounce { 0%, 100% { transform: translateY(-25%); animation-timing-function: cubic-bezier(0.8, 0, 1, 1); } 50% { transform: none; animation-timing-function: cubic-bezier(0, 0, 0.2, 1); } }"),
    )

    fun baseline(): EffectiveKoloTheme = EffectiveKoloTheme(entries, keyframes)
}

class KoloThemeComposer(
    private val baseline: EffectiveKoloTheme = KoloDefaultTheme.baseline(),
) {
    fun compose(contributions: List<ThemeContribution>): EffectiveKoloTheme {
        require(contributions.map(ThemeContribution::id).distinct().size == contributions.size) {
            "Theme contribution ids must be unique"
        }
        val effective = baseline.entries.associateBy(ThemeEntry::property).toMutableMap()
        contributions.groupBy(ThemeContribution::precedence).toSortedMap().forEach { (precedence, samePrecedenceContributions) ->
            samePrecedenceContributions.sortedBy(ThemeContribution::id)
                .flatMap { contribution -> contribution.entries.map { contribution to it } }
                .groupBy { (_, entry) -> entry.property }
                .forEach { (property, candidates) ->
                    if (candidates.map { it.second.value to it.second.keyframe }.distinct().size > 1) {
                        val contributors = candidates.joinToString { it.first.id }
                        error("Conflicting theme property $property at precedence $precedence from $contributors")
                    }
                    effective[property] = candidates.first().second
                }
        }
        val suppliedKeyframes = contributions.flatMap(ThemeContribution::keyframes)
        (baseline.keyframes + suppliedKeyframes).groupBy(ThemeKeyframe::name).forEach { (name, candidates) ->
            require(candidates.map(ThemeKeyframe::css).distinct().size == 1) {
                "Conflicting keyframe $name from theme baseline or contributions"
            }
        }
        val allKeyframes = baseline.keyframes + suppliedKeyframes
        effective.values.forEach { entry ->
            entry.keyframe?.let { name -> require(allKeyframes.any { it.name == name }) { "Theme property ${entry.property} references unknown keyframe $name" } }
        }
        return EffectiveKoloTheme(effective.values.toList(), allKeyframes.distinctBy(ThemeKeyframe::name))
    }
}
