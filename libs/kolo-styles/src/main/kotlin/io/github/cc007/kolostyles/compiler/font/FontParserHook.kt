package io.github.cc007.kolostyles.compiler.font

import io.github.cc007.kolostyles.compiler.StyleParserHook
import io.github.cc007.kolostyles.compiler.Token
import io.github.cc007.kolostyles.compiler.parseKoloVariants
import kotlinx.css.FontWeight
import kotlinx.css.rem
import org.springframework.stereotype.Component

private val FONT_FAMILY_VALUES: Map<String, String> = linkedMapOf(
    "font-sans" to "var(--font-sans)",
    "font-serif" to "var(--font-serif)",
    "font-mono" to "var(--font-mono)",
)

private val FONT_SIZE_VALUES = linkedMapOf(
    "text-xs" to 0.75.rem,
    "text-sm" to 0.875.rem,
    "text-base" to 1.rem,
    "text-lg" to 1.125.rem,
    "text-xl" to 1.25.rem,
    "text-2xl" to 1.5.rem,
    "text-3xl" to 1.875.rem,
    "text-4xl" to 2.25.rem,
    "text-5xl" to 3.rem,
    "text-6xl" to 3.75.rem,
    "text-7xl" to 4.5.rem,
    "text-8xl" to 6.rem,
    "text-9xl" to 8.rem,
)

private val FONT_WEIGHT_VALUES = linkedMapOf(
    "font-thin" to FontWeight.w100,
    "font-extralight" to FontWeight.w200,
    "font-light" to FontWeight.w300,
    "font-normal" to FontWeight.normal,
    "font-medium" to FontWeight.w500,
    "font-semibold" to FontWeight.w600,
    "font-bold" to FontWeight.bold,
    "font-extrabold" to FontWeight.w800,
    "font-black" to FontWeight.w900,
)

@Component
class FontParserHook : StyleParserHook {
    override fun parse(token: String): Token? {
        val parts = token.split(':')
        if (parts.any { it.isBlank() }) {
            return null
        }

        val utility = parts.last()
        val variants = parts.dropLast(1)
        val parsedVariants = parseKoloVariants(variants) ?: return null

        FONT_FAMILY_VALUES[utility]?.let { fontFamily ->
            return FontFamilyToken(
                raw = token,
                stateVariants = parsedVariants.stateVariants,
                mediaVariant = parsedVariants.mediaVariant,
                utility = utility,
                fontFamilyValue = fontFamily,
            )
        }

        FONT_SIZE_VALUES[utility]?.let { fontSize ->
            return FontSizeToken(
                raw = token,
                stateVariants = parsedVariants.stateVariants,
                mediaVariant = parsedVariants.mediaVariant,
                utility = utility,
                fontSizeValue = fontSize,
            )
        }

        FONT_WEIGHT_VALUES[utility]?.let { fontWeight ->
            return FontWeightToken(
                raw = token,
                stateVariants = parsedVariants.stateVariants,
                mediaVariant = parsedVariants.mediaVariant,
                utility = utility,
                fontWeightValue = fontWeight,
            )
        }

        return null
    }
}
