package io.github.cc007.kolostyles.compiler.sizing

import io.github.cc007.kolostyles.compiler.StyleParserHook
import io.github.cc007.kolostyles.compiler.Token
import io.github.cc007.kolostyles.compiler.parseKoloVariants
import kotlinx.css.*
import org.springframework.stereotype.Component

private val SIZING_UTILITY_PREFIXES = listOf("min-w", "max-w", "min-h", "max-h", "size", "w", "h")

private val FRACTION_TOKEN_PATTERN = Regex("^(\\d+)/(\\d+)$")

private val ALPHA_SIZING_VALUES = linkedMapOf(
    "xs" to 20.rem,
    "sm" to 24.rem,
    "md" to 28.rem,
    "lg" to 32.rem,
    "xl" to 36.rem,
    "2xl" to 42.rem,
    "3xl" to 48.rem,
    "4xl" to 56.rem,
    "5xl" to 64.rem,
    "6xl" to 72.rem,
    "7xl" to 80.rem,
)

private val COMMON_NAMED_VALUES = linkedMapOf(
    "auto" to LinearDimension.auto,
    "px" to 1.px,
    "full" to 100.pct,
    "dvw" to 100.dvw,
    "dvh" to 100.dvh,
    "lvw" to 100.lvw,
    "lvh" to 100.lvh,
    "svw" to 100.svw,
    "svh" to 100.svh,
    "fit" to LinearDimension.fitContent,
    "min" to LinearDimension.minContent,
    "max" to LinearDimension.maxContent,
)

private val WIDTH_UTILITIES = setOf("w", "min-w", "max-w")
private val HEIGHT_UTILITIES = setOf("h", "min-h", "max-h")

@Component
class SizingParserHook : StyleParserHook {
    override fun parse(token: String): Token? {
        val parts = token.split(':')
        if (parts.any { it.isBlank() }) {
            return null
        }

        val utilityPart = parts.last()
        val utilityAndValue = parseUtilityAndValue(utilityPart) ?: return null
        val utility = utilityAndValue.first
        val rawValue = utilityAndValue.second
        val value = resolveSizingValue(utility, rawValue) ?: return null

        val variants = parts.dropLast(1)
        val parsedVariants = parseKoloVariants(variants) ?: return null

        return SizingToken(
            raw = token,
            stateVariants = parsedVariants.stateVariants,
            mediaVariant = parsedVariants.mediaVariant,
            utility = utility,
            value = value,
        )
    }

    private fun parseUtilityAndValue(utilityPart: String): Pair<String, String>? {
        for (utilityPrefix in SIZING_UTILITY_PREFIXES) {
            val fullPrefix = "$utilityPrefix-"
            if (!utilityPart.startsWith(fullPrefix)) {
                continue
            }
            val value = utilityPart.removePrefix(fullPrefix)
            if (value.isBlank()) {
                return null
            }
            return utilityPrefix to value
        }
        return null
    }
}

private fun resolveSizingValue(utility: String, rawValue: String): LinearDimension? {
    if (rawValue == "screen") {
        return when (utility) {
            in HEIGHT_UTILITIES -> 100.vh
            in WIDTH_UTILITIES -> 100.vw
            else -> null
        }
    }

    COMMON_NAMED_VALUES[rawValue]?.let { return it }
    parseFractionValue(rawValue)?.let { return it }

    if (utility in WIDTH_UTILITIES) {
        ALPHA_SIZING_VALUES[rawValue]?.let { return it }
    }

    if (rawValue.matches(Regex("^\\d+$"))) {
        val intValue = rawValue.toIntOrNull() ?: return null
        return (intValue.toDouble() / 4.0).rem
    }

    return null
}

private fun parseFractionValue(rawValue: String): LinearDimension? {
    val match = FRACTION_TOKEN_PATTERN.matchEntire(rawValue) ?: return null
    val numerator = match.groupValues[1].toIntOrNull() ?: return null
    val denominator = match.groupValues[2].toIntOrNull() ?: return null
    if (denominator == 0) {
        return null
    }
    return LinearDimension("calc($numerator/$denominator * 100%)")
}
