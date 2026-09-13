package io.github.cc007.kolostyles.compiler.layout

import io.github.cc007.kolostyles.compiler.StyleGeneratorHook
import io.github.cc007.kolostyles.compiler.Token
import kotlinx.css.*
import org.springframework.stereotype.Component

@Component
class LayoutGeneratorHook : StyleGeneratorHook {
    override fun generate(token: Token, builder: CssBuilder): Boolean {
        val layoutToken = token as? LayoutToken ?: return false
        return builder.emitLayoutRule(layoutToken)
    }

    private fun CssBuilder.emitLayoutRule(token: LayoutToken): Boolean {
        return when (token) {
            is BoxSizingToken -> emitVariantRule(token.raw, token.stateVariants, token.mediaVariant) {
                boxSizing = token.value
            }

            is OverflowToken -> emitVariantRule(token.raw, token.stateVariants, token.mediaVariant) {
                val isClipUtility = token.utility.endsWith("-clip")
                when (token.axis) {
                    OverflowAxis.BOTH -> if (isClipUtility) put("overflow", "clip") else overflow = token.value
                    OverflowAxis.X -> if (isClipUtility) put("overflow-x", "clip") else overflowX = token.value
                    OverflowAxis.Y -> if (isClipUtility) put("overflow-y", "clip") else overflowY = token.value
                }
            }

            is PositionToken -> emitVariantRule(token.raw, token.stateVariants, token.mediaVariant) {
                position = token.value
            }

            is ZIndexToken -> emitVariantRule(token.raw, token.stateVariants, token.mediaVariant) {
                if (token.utility == "z-auto") {
                    put("z-index", "auto")
                } else {
                    zIndex = token.zIndex ?: return@emitVariantRule
                }
            }

            is ObjectFitToken -> emitVariantRule(token.raw, token.stateVariants, token.mediaVariant) {
                objectFit = token.value
            }

            else -> false
        }
    }
}
