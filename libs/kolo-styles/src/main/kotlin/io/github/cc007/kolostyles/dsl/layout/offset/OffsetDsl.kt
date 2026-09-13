package io.github.cc007.kolostyles.dsl.layout.offset

import io.github.cc007.kolostyles.dsl.KoloScope
import io.github.cc007.kolostyles.dsl.KoloVariantScope

private fun KoloScope.recordOffset(prefix: String, value: String) {
    recordBase("$prefix-$value")
}

private fun KoloVariantScope.recordOffset(prefix: String, value: String) {
    recordBase("$prefix-$value")
}

private fun Int.toOffsetTokenValue(): String {
    require(this >= 0) { "Offset utilities require non-negative values" }
    return toString()
}

private fun Pair<Int, Int>.toOffsetFractionTokenValue(): String {
    val numerator = first
    val denominator = second
    require(numerator >= 0) { "Offset fraction numerators must be non-negative" }
    require(denominator > 0) { "Offset fraction denominators must be positive" }
    return "$numerator/$denominator"
}

private fun KoloScope.offsetValue(prefix: String, value: Int) {
    recordOffset(prefix, value.toOffsetTokenValue())
}

private fun KoloVariantScope.offsetValue(prefix: String, value: Int) {
    recordOffset(prefix, value.toOffsetTokenValue())
}

private fun KoloScope.offsetFraction(prefix: String, numerator: Int, denominator: Int) {
    recordOffset(prefix, (numerator to denominator).toOffsetFractionTokenValue())
}

private fun KoloVariantScope.offsetFraction(prefix: String, numerator: Int, denominator: Int) {
    recordOffset(prefix, (numerator to denominator).toOffsetFractionTokenValue())
}

private fun KoloScope.offsetAuto(prefix: String) = recordOffset(prefix, "auto")
private fun KoloVariantScope.offsetAuto(prefix: String) = recordOffset(prefix, "auto")
private fun KoloScope.offsetPx(prefix: String) = recordOffset(prefix, "px")
private fun KoloVariantScope.offsetPx(prefix: String) = recordOffset(prefix, "px")
private fun KoloScope.offsetFull(prefix: String) = recordOffset(prefix, "full")
private fun KoloVariantScope.offsetFull(prefix: String) = recordOffset(prefix, "full")

fun KoloScope.inset(value: Int) = offsetValue("inset", value)
fun KoloScope.inset(numerator: Int, denominator: Int) = offsetFraction("inset", numerator, denominator)
val KoloScope.insetAuto: Unit get() = offsetAuto("inset")
val KoloScope.insetPx: Unit get() = offsetPx("inset")
val KoloScope.insetFull: Unit get() = offsetFull("inset")
fun KoloVariantScope.inset(value: Int) = offsetValue("inset", value)
fun KoloVariantScope.inset(numerator: Int, denominator: Int) = offsetFraction("inset", numerator, denominator)
val KoloVariantScope.insetAuto: Unit get() = offsetAuto("inset")
val KoloVariantScope.insetPx: Unit get() = offsetPx("inset")
val KoloVariantScope.insetFull: Unit get() = offsetFull("inset")

fun KoloScope.insetX(value: Int) = offsetValue("inset-x", value)
fun KoloScope.insetX(numerator: Int, denominator: Int) = offsetFraction("inset-x", numerator, denominator)
val KoloScope.insetXAuto: Unit get() = offsetAuto("inset-x")
val KoloScope.insetXPx: Unit get() = offsetPx("inset-x")
val KoloScope.insetXFull: Unit get() = offsetFull("inset-x")
fun KoloVariantScope.insetX(value: Int) = offsetValue("inset-x", value)
fun KoloVariantScope.insetX(numerator: Int, denominator: Int) = offsetFraction("inset-x", numerator, denominator)
val KoloVariantScope.insetXAuto: Unit get() = offsetAuto("inset-x")
val KoloVariantScope.insetXPx: Unit get() = offsetPx("inset-x")
val KoloVariantScope.insetXFull: Unit get() = offsetFull("inset-x")

fun KoloScope.insetY(value: Int) = offsetValue("inset-y", value)
fun KoloScope.insetY(numerator: Int, denominator: Int) = offsetFraction("inset-y", numerator, denominator)
val KoloScope.insetYAuto: Unit get() = offsetAuto("inset-y")
val KoloScope.insetYPx: Unit get() = offsetPx("inset-y")
val KoloScope.insetYFull: Unit get() = offsetFull("inset-y")
fun KoloVariantScope.insetY(value: Int) = offsetValue("inset-y", value)
fun KoloVariantScope.insetY(numerator: Int, denominator: Int) = offsetFraction("inset-y", numerator, denominator)
val KoloVariantScope.insetYAuto: Unit get() = offsetAuto("inset-y")
val KoloVariantScope.insetYPx: Unit get() = offsetPx("inset-y")
val KoloVariantScope.insetYFull: Unit get() = offsetFull("inset-y")

fun KoloScope.top(value: Int) = offsetValue("top", value)
fun KoloScope.top(numerator: Int, denominator: Int) = offsetFraction("top", numerator, denominator)
val KoloScope.topAuto: Unit get() = offsetAuto("top")
val KoloScope.topPx: Unit get() = offsetPx("top")
val KoloScope.topFull: Unit get() = offsetFull("top")
fun KoloVariantScope.top(value: Int) = offsetValue("top", value)
fun KoloVariantScope.top(numerator: Int, denominator: Int) = offsetFraction("top", numerator, denominator)
val KoloVariantScope.topAuto: Unit get() = offsetAuto("top")
val KoloVariantScope.topPx: Unit get() = offsetPx("top")
val KoloVariantScope.topFull: Unit get() = offsetFull("top")

fun KoloScope.right(value: Int) = offsetValue("right", value)
fun KoloScope.right(numerator: Int, denominator: Int) = offsetFraction("right", numerator, denominator)
val KoloScope.rightAuto: Unit get() = offsetAuto("right")
val KoloScope.rightPx: Unit get() = offsetPx("right")
val KoloScope.rightFull: Unit get() = offsetFull("right")
fun KoloVariantScope.right(value: Int) = offsetValue("right", value)
fun KoloVariantScope.right(numerator: Int, denominator: Int) = offsetFraction("right", numerator, denominator)
val KoloVariantScope.rightAuto: Unit get() = offsetAuto("right")
val KoloVariantScope.rightPx: Unit get() = offsetPx("right")
val KoloVariantScope.rightFull: Unit get() = offsetFull("right")

fun KoloScope.bottom(value: Int) = offsetValue("bottom", value)
fun KoloScope.bottom(numerator: Int, denominator: Int) = offsetFraction("bottom", numerator, denominator)
val KoloScope.bottomAuto: Unit get() = offsetAuto("bottom")
val KoloScope.bottomPx: Unit get() = offsetPx("bottom")
val KoloScope.bottomFull: Unit get() = offsetFull("bottom")
fun KoloVariantScope.bottom(value: Int) = offsetValue("bottom", value)
fun KoloVariantScope.bottom(numerator: Int, denominator: Int) = offsetFraction("bottom", numerator, denominator)
val KoloVariantScope.bottomAuto: Unit get() = offsetAuto("bottom")
val KoloVariantScope.bottomPx: Unit get() = offsetPx("bottom")
val KoloVariantScope.bottomFull: Unit get() = offsetFull("bottom")

fun KoloScope.left(value: Int) = offsetValue("left", value)
fun KoloScope.left(numerator: Int, denominator: Int) = offsetFraction("left", numerator, denominator)
val KoloScope.leftAuto: Unit get() = offsetAuto("left")
val KoloScope.leftPx: Unit get() = offsetPx("left")
val KoloScope.leftFull: Unit get() = offsetFull("left")
fun KoloVariantScope.left(value: Int) = offsetValue("left", value)
fun KoloVariantScope.left(numerator: Int, denominator: Int) = offsetFraction("left", numerator, denominator)
val KoloVariantScope.leftAuto: Unit get() = offsetAuto("left")
val KoloVariantScope.leftPx: Unit get() = offsetPx("left")
val KoloVariantScope.leftFull: Unit get() = offsetFull("left")
