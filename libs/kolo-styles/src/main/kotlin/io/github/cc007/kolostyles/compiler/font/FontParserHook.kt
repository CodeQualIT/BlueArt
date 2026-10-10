package io.github.cc007.kolostyles.compiler.font

import io.github.cc007.kolostyles.compiler.StyleParserHook
import io.github.cc007.kolostyles.compiler.Token
import io.github.cc007.kolostyles.compiler.parseKoloVariants
import io.github.cc007.kolostyles.dsl.font.FontFamily
import io.github.cc007.kolostyles.dsl.font.FontWeight
import io.github.cc007.kolostyles.theme.EffectiveKoloTheme
import io.github.cc007.kolostyles.theme.KoloDefaultTheme
import org.springframework.stereotype.Component

private val FONT_SIZE_PROPERTIES = listOf(
    "xs", "sm", "base", "lg", "xl", "2xl", "3xl", "4xl", "5xl", "6xl", "7xl", "8xl", "9xl",
).associate { "text-$it" to "--k-text-$it" }

@Component
class FontParserHook(
    effectiveTheme: EffectiveKoloTheme = KoloDefaultTheme.baseline(),
) : StyleParserHook {
    private val availableProperties = effectiveTheme.entries.map { it.property }.toSet()
    private val fontWeightProperties = effectiveTheme.entries
        .filter { it.property.startsWith("--k-font-weight-") }
        .map { it.property.removePrefix("--k-font-weight-") }
        .filter(FontWeight::isValidName)
        .associate { "font-$it" to "--k-font-weight-$it" }
    private val fontFamilyProperties = effectiveTheme.entries
        .filter { it.property.startsWith("--k-font-") }
        .map { it.property.removePrefix("--k-font-") }
        .filter(FontFamily::isValidName)
        .associate { "font-$it" to "--k-font-$it" }

    init {
        val ambiguousUtilities = fontFamilyProperties.keys.intersect(fontWeightProperties.keys)
        require(ambiguousUtilities.isEmpty()) {
            "Ambiguous font-family and font-weight theme utilities: ${ambiguousUtilities.joinToString()}"
        }
    }

    override fun parse(token: String): Token? {
        val parts = token.split(':')
        if (parts.any { it.isBlank() }) {
            return null
        }

        val utility = parts.last()
        val variants = parts.dropLast(1)
        val parsedVariants = parseKoloVariants(variants) ?: return null

        fontFamilyProperties[utility]?.let { themeProperty ->
            return FontFamilyToken(
                raw = token,
                stateVariants = parsedVariants.stateVariants,
                mediaVariant = parsedVariants.mediaVariant,
                utility = utility,
                themeProperty = themeProperty,
            )
        }

        FONT_SIZE_PROPERTIES[utility]?.takeIf { it in availableProperties }?.let { themeProperty ->
            return FontSizeToken(
                raw = token,
                stateVariants = parsedVariants.stateVariants,
                mediaVariant = parsedVariants.mediaVariant,
                utility = utility,
                themeProperty = themeProperty,
            )
        }

        fontWeightProperties[utility]?.let { themeProperty ->
            return FontWeightToken(
                raw = token,
                stateVariants = parsedVariants.stateVariants,
                mediaVariant = parsedVariants.mediaVariant,
                utility = utility,
                themeProperty = themeProperty,
            )
        }

        return null
    }
}
