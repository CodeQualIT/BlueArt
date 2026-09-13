package io.github.cc007.kolostyles.dsl.sizing

import io.github.cc007.kolostyles.dsl.KoloScope
import io.github.cc007.kolostyles.dsl.KoloVariantScope

enum class SizingNamed(internal val token: String) {
    AUTO("auto"),
    PX("px"),
    FULL("full"),
    SCREEN("screen"),
    DVW("dvw"),
    DVH("dvh"),
    LVW("lvw"),
    LVH("lvh"),
    SVW("svw"),
    SVH("svh"),
    FIT("fit"),
    MIN("min"),
    MAX("max"),
}

enum class SizingAlpha(internal val token: String) {
    XS("xs"),
    SM("sm"),
    MD("md"),
    LG("lg"),
    XL("xl"),
    XL2("2xl"),
    XL3("3xl"),
    XL4("4xl"),
    XL5("5xl"),
    XL6("6xl"),
    XL7("7xl"),
}

private fun KoloScope.recordSizing(prefix: String, token: String) {
    recordBase("$prefix-$token")
}

private fun KoloVariantScope.recordSizing(prefix: String, token: String) {
    recordBase("$prefix-$token")
}

private fun fractionToken(numerator: Int, denominator: Int): String {
    require(numerator >= 0) { "Sizing fraction numerator must be non-negative" }
    require(denominator > 0) { "Sizing fraction denominator must be greater than zero" }
    return "$numerator/$denominator"
}

private fun KoloScope.recordIntegerSizing(prefix: String, value: Int) {
    require(value >= 0) { "Sizing utilities require non-negative integer values" }
    recordSizing(prefix, value.toString())
}

private fun KoloVariantScope.recordIntegerSizing(prefix: String, value: Int) {
    require(value >= 0) { "Sizing utilities require non-negative integer values" }
    recordSizing(prefix, value.toString())
}

fun KoloScope.w(value: Int) = recordIntegerSizing("w", value)
fun KoloScope.w(numerator: Int, denominator: Int) = recordSizing("w", fractionToken(numerator, denominator))
fun KoloScope.w(value: SizingNamed) = recordSizing("w", value.token)
fun KoloScope.w(value: SizingAlpha) = recordSizing("w", value.token)

fun KoloScope.h(value: Int) = recordIntegerSizing("h", value)
fun KoloScope.h(numerator: Int, denominator: Int) = recordSizing("h", fractionToken(numerator, denominator))
fun KoloScope.h(value: SizingNamed) = recordSizing("h", value.token)

fun KoloScope.minW(value: Int) = recordIntegerSizing("min-w", value)
fun KoloScope.minW(numerator: Int, denominator: Int) = recordSizing("min-w", fractionToken(numerator, denominator))
fun KoloScope.minW(value: SizingNamed) = recordSizing("min-w", value.token)
fun KoloScope.minW(value: SizingAlpha) = recordSizing("min-w", value.token)

fun KoloScope.maxW(value: Int) = recordIntegerSizing("max-w", value)
fun KoloScope.maxW(numerator: Int, denominator: Int) = recordSizing("max-w", fractionToken(numerator, denominator))
fun KoloScope.maxW(value: SizingNamed) = recordSizing("max-w", value.token)
fun KoloScope.maxW(value: SizingAlpha) = recordSizing("max-w", value.token)

fun KoloScope.minH(value: Int) = recordIntegerSizing("min-h", value)
fun KoloScope.minH(numerator: Int, denominator: Int) = recordSizing("min-h", fractionToken(numerator, denominator))
fun KoloScope.minH(value: SizingNamed) = recordSizing("min-h", value.token)

fun KoloScope.maxH(value: Int) = recordIntegerSizing("max-h", value)
fun KoloScope.maxH(numerator: Int, denominator: Int) = recordSizing("max-h", fractionToken(numerator, denominator))
fun KoloScope.maxH(value: SizingNamed) = recordSizing("max-h", value.token)

fun KoloScope.size(value: Int) = recordIntegerSizing("size", value)
fun KoloScope.size(numerator: Int, denominator: Int) = recordSizing("size", fractionToken(numerator, denominator))
fun KoloScope.size(value: SizingNamed) = recordSizing("size", value.token)

fun KoloVariantScope.w(value: Int) = recordIntegerSizing("w", value)
fun KoloVariantScope.w(numerator: Int, denominator: Int) = recordSizing("w", fractionToken(numerator, denominator))
fun KoloVariantScope.w(value: SizingNamed) = recordSizing("w", value.token)
fun KoloVariantScope.w(value: SizingAlpha) = recordSizing("w", value.token)

fun KoloVariantScope.h(value: Int) = recordIntegerSizing("h", value)
fun KoloVariantScope.h(numerator: Int, denominator: Int) = recordSizing("h", fractionToken(numerator, denominator))
fun KoloVariantScope.h(value: SizingNamed) = recordSizing("h", value.token)

fun KoloVariantScope.minW(value: Int) = recordIntegerSizing("min-w", value)
fun KoloVariantScope.minW(numerator: Int, denominator: Int) = recordSizing("min-w", fractionToken(numerator, denominator))
fun KoloVariantScope.minW(value: SizingNamed) = recordSizing("min-w", value.token)
fun KoloVariantScope.minW(value: SizingAlpha) = recordSizing("min-w", value.token)

fun KoloVariantScope.maxW(value: Int) = recordIntegerSizing("max-w", value)
fun KoloVariantScope.maxW(numerator: Int, denominator: Int) = recordSizing("max-w", fractionToken(numerator, denominator))
fun KoloVariantScope.maxW(value: SizingNamed) = recordSizing("max-w", value.token)
fun KoloVariantScope.maxW(value: SizingAlpha) = recordSizing("max-w", value.token)

fun KoloVariantScope.minH(value: Int) = recordIntegerSizing("min-h", value)
fun KoloVariantScope.minH(numerator: Int, denominator: Int) = recordSizing("min-h", fractionToken(numerator, denominator))
fun KoloVariantScope.minH(value: SizingNamed) = recordSizing("min-h", value.token)

fun KoloVariantScope.maxH(value: Int) = recordIntegerSizing("max-h", value)
fun KoloVariantScope.maxH(numerator: Int, denominator: Int) = recordSizing("max-h", fractionToken(numerator, denominator))
fun KoloVariantScope.maxH(value: SizingNamed) = recordSizing("max-h", value.token)

fun KoloVariantScope.size(value: Int) = recordIntegerSizing("size", value)
fun KoloVariantScope.size(numerator: Int, denominator: Int) = recordSizing("size", fractionToken(numerator, denominator))
fun KoloVariantScope.size(value: SizingNamed) = recordSizing("size", value.token)
