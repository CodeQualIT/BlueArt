package io.github.cc007.kolostyles.compiler.sizing

import io.github.cc007.kolostyles.compiler.StyleGeneratorHook
import io.github.cc007.kolostyles.compiler.Token
import kotlinx.css.*
import org.springframework.stereotype.Component

@Component
class SizingGeneratorHook : StyleGeneratorHook {
    override fun generate(token: Token, builder: CssBuilder): Boolean {
        if (token !is SizingToken) return false
        val declaration = buildSizingDeclaration(token) ?: return false
        return builder.emitVariantRule(
            rawToken = token.raw,
            stateVariants = token.stateVariants,
            mediaVariant = token.mediaVariant,
            declaration = declaration,
        )
    }

    private fun buildSizingDeclaration(token: SizingToken): (CssBuilder.() -> Unit)? {
        return when (token.utility) {
            "w" -> { { width = token.value } }
            "h" -> { { height = token.value } }
            "min-w" -> { { minWidth = token.value } }
            "max-w" -> { { maxWidth = token.value } }
            "min-h" -> { { minHeight = token.value } }
            "max-h" -> { { maxHeight = token.value } }
            "size" -> { { width = token.value; height = token.value } }
            else -> null
        }
    }
}
