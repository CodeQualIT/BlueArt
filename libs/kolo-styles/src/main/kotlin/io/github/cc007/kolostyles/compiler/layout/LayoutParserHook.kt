package io.github.cc007.kolostyles.compiler.layout

import io.github.cc007.kolostyles.compiler.MediaVariant
import io.github.cc007.kolostyles.compiler.StyleParserHook
import io.github.cc007.kolostyles.compiler.Token
import io.github.cc007.kolostyles.compiler.parseKoloVariants
import kotlinx.css.BoxSizing
import kotlinx.css.ObjectFit
import kotlinx.css.Overflow
import kotlinx.css.Position
import org.springframework.stereotype.Component

private val BOX_SIZING_VALUES = linkedMapOf(
    "box-border" to BoxSizing.borderBox,
    "box-content" to BoxSizing.contentBox,
)

private val OVERFLOW_VALUES = linkedMapOf(
    "overflow-auto" to (OverflowAxis.BOTH to Overflow.auto),
    "overflow-hidden" to (OverflowAxis.BOTH to Overflow.hidden),
    "overflow-clip" to (OverflowAxis.BOTH to Overflow.hidden),
    "overflow-visible" to (OverflowAxis.BOTH to Overflow.visible),
    "overflow-scroll" to (OverflowAxis.BOTH to Overflow.scroll),
    "overflow-x-auto" to (OverflowAxis.X to Overflow.auto),
    "overflow-x-hidden" to (OverflowAxis.X to Overflow.hidden),
    "overflow-x-clip" to (OverflowAxis.X to Overflow.hidden),
    "overflow-x-visible" to (OverflowAxis.X to Overflow.visible),
    "overflow-x-scroll" to (OverflowAxis.X to Overflow.scroll),
    "overflow-y-auto" to (OverflowAxis.Y to Overflow.auto),
    "overflow-y-hidden" to (OverflowAxis.Y to Overflow.hidden),
    "overflow-y-clip" to (OverflowAxis.Y to Overflow.hidden),
    "overflow-y-visible" to (OverflowAxis.Y to Overflow.visible),
    "overflow-y-scroll" to (OverflowAxis.Y to Overflow.scroll),
)

private val POSITION_VALUES = linkedMapOf(
    "static" to Position.static,
    "relative" to Position.relative,
    "absolute" to Position.absolute,
    "fixed" to Position.fixed,
    "sticky" to Position.sticky,
)

private val OBJECT_FIT_VALUES = linkedMapOf(
    "object-contain" to ObjectFit.contain,
    "object-cover" to ObjectFit.cover,
    "object-fill" to ObjectFit.fill,
    "object-none" to ObjectFit.none,
    "object-scale-down" to ObjectFit.scaleDown,
)

private val POSITIVE_INTEGER_PATTERN = Regex("^[1-9]\\d*$")

@Component
class LayoutParserHook : StyleParserHook {
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

        BOX_SIZING_VALUES[utility]?.let { boxSizing ->
            return BoxSizingToken(
                raw = token,
                stateVariants = stateVariants,
                mediaVariant = mediaVariant,
                utility = utility,
                value = boxSizing,
            )
        }

        OVERFLOW_VALUES[utility]?.let { overflow ->
            return OverflowToken(
                raw = token,
                stateVariants = stateVariants,
                mediaVariant = mediaVariant,
                utility = utility,
                axis = overflow.first,
                value = overflow.second,
            )
        }

        POSITION_VALUES[utility]?.let { position ->
            return PositionToken(
                raw = token,
                stateVariants = stateVariants,
                mediaVariant = mediaVariant,
                utility = utility,
                value = position,
            )
        }

        if (utility == "z-auto") {
            return ZIndexToken(
                raw = token,
                stateVariants = stateVariants,
                mediaVariant = mediaVariant,
                utility = utility,
                zIndex = null,
            )
        }
        parseZIndexValue(utility)?.let { zIndex ->
            return ZIndexToken(
                raw = token,
                stateVariants = stateVariants,
                mediaVariant = mediaVariant,
                utility = utility,
                zIndex = zIndex,
            )
        }

        OBJECT_FIT_VALUES[utility]?.let { objectFit ->
            return ObjectFitToken(
                raw = token,
                stateVariants = stateVariants,
                mediaVariant = mediaVariant,
                utility = utility,
                value = objectFit,
            )
        }

        return null
    }

    private fun parseVariants(variants: List<String>): Pair<List<String>, MediaVariant?>? {
        val parsedVariants = parseKoloVariants(variants) ?: return null
        return parsedVariants.stateVariants to parsedVariants.mediaVariant
    }
}

private fun parseZIndexValue(utility: String): Int? {
    if (!utility.startsWith("z-")) {
        return null
    }
    val value = utility.removePrefix("z-")
    if (!value.matches(POSITIVE_INTEGER_PATTERN)) {
        return null
    }
    return value.toIntOrNull() ?: return null
}
