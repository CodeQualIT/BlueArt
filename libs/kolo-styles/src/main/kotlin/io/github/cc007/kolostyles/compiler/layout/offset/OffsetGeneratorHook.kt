package io.github.cc007.kolostyles.compiler.layout.offset

import io.github.cc007.kolostyles.compiler.StyleGeneratorHook
import io.github.cc007.kolostyles.compiler.Token
import kotlinx.css.*
import org.springframework.stereotype.Component

@Component
class OffsetGeneratorHook : StyleGeneratorHook {
    override fun generate(token: Token, builder: CssBuilder): Boolean {
        if (token !is OffsetToken) return false
        return builder.emitVariantRule(token.raw, token.stateVariants, token.mediaVariant) {
            when (token.utility) {
                "inset" -> {
                    inset = Inset(token.value)
                }
                "inset-x" -> {
                    insetInline = InsetInline(token.value)
                }
                "inset-y" -> {
                    insetBlock = InsetBlock(token.value)
                }
                "top" -> top = token.value
                "right" -> right = token.value
                "bottom" -> bottom = token.value
                "left" -> left = token.value
            }
        }
    }
}
