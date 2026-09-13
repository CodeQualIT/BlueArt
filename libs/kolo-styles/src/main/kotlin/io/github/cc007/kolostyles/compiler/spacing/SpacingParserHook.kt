package io.github.cc007.kolostyles.compiler.spacing

import io.github.cc007.kolostyles.compiler.StyleParserHook
import io.github.cc007.kolostyles.compiler.Token
import io.github.cc007.kolostyles.compiler.parseKoloVariants
import kotlinx.css.LinearDimension
import kotlinx.css.rem
import org.springframework.stereotype.Component

private val SPACING_UTILITIES = listOf("m", "mt", "mr", "mb", "ml", "mx", "my", "p", "pt", "pr", "pb", "pl", "px", "py")
private val SPACING_TOKEN_PATTERN = Regex("^(${SPACING_UTILITIES.joinToString("|")})-(\\d+|auto)$")

@Component
class SpacingParserHook : StyleParserHook {
    override fun parse(token: String): Token? {
        return parseSpacingToken(token)
    }

    private fun parseSpacingToken(token: String): SpacingToken? {
        val parts = token.split(':')
        if (parts.any { it.isBlank() }) {
            return null
        }

        val utilityPart = parts.last()
        val match = SPACING_TOKEN_PATTERN.matchEntire(utilityPart) ?: return null
        val utility = match.groupValues[1]
        val rawValue = match.groupValues[2]
        val value = if (rawValue == "auto") LinearDimension.auto else toSpacingDimension(rawValue.toIntOrNull() ?: return null)
        val variants = parts.dropLast(1)

        val parsedVariants = parseKoloVariants(variants) ?: return null

        return SpacingToken(
            raw = token,
            stateVariants = parsedVariants.stateVariants,
            mediaVariant = parsedVariants.mediaVariant,
            utility = utility,
            value = value,
        )
    }

    private fun toSpacingDimension(value: Int): LinearDimension {
        return (value.toDouble() / 4.0).rem
    }
}
