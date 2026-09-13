package io.github.cc007.kolostyles.compiler.layout.offset

import io.github.cc007.kolostyles.compiler.MediaVariant
import io.github.cc007.kolostyles.compiler.StyleParserHook
import io.github.cc007.kolostyles.compiler.Token
import io.github.cc007.kolostyles.compiler.parseKoloVariants
import kotlinx.css.LinearDimension
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.rem
import org.springframework.stereotype.Component

private val OFFSET_PREFIXES = listOf("inset-x", "inset-y", "inset", "top", "right", "bottom", "left")
private val FRACTION_TOKEN_PATTERN = Regex("^(\\d+)/(\\d+)$")

@Component
class OffsetParserHook : StyleParserHook {
    override fun parse(token: String): Token? {
        val parts = token.split(':')
        if (parts.any { it.isBlank() }) {
            return null
        }

        val utility = parts.last()
        val variants = parts.dropLast(1)
        val parsedVariants = parseVariants(variants) ?: return null
        val stateVariants = parsedVariants.first
        val mediaVariant = parsedVariants.second

        parseOffsetUtility(utility)?.let { offset ->
            return OffsetToken(
                raw = token,
                stateVariants = stateVariants,
                mediaVariant = mediaVariant,
                utility = offset.first,
                value = offset.second,
            )
        }

        return null
    }

    private fun parseVariants(variants: List<String>): Pair<List<String>, MediaVariant?>? {
        val parsedVariants = parseKoloVariants(variants) ?: return null
        return parsedVariants.stateVariants to parsedVariants.mediaVariant
    }
}

private fun parseOffsetUtility(utility: String): Pair<String, LinearDimension>? {
    val prefix = OFFSET_PREFIXES.firstOrNull { utility.startsWith("$it-") } ?: return null
    val rawValue = utility.removePrefix("$prefix-")
    if (rawValue.isBlank()) {
        return null
    }
    val value = resolveOffsetValue(rawValue) ?: return null
    return prefix to value
}

private fun resolveOffsetValue(rawValue: String): LinearDimension? {
    return when (rawValue) {
        "auto" -> LinearDimension.auto
        "px" -> 1.px
        "full" -> 100.pct
        else -> {
            parseFractionValue(rawValue)
                ?: rawValue.toIntOrNull()?.takeIf { it >= 0 }?.let { (it.toDouble() / 4.0).rem }
        }
    }
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
