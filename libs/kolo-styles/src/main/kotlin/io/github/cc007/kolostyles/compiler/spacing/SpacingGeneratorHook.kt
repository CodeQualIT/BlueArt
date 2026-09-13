package io.github.cc007.kolostyles.compiler.spacing

import io.github.cc007.kolostyles.compiler.StyleGeneratorHook
import io.github.cc007.kolostyles.compiler.Token
import kotlinx.css.*
import org.springframework.stereotype.Component

@Component
class SpacingGeneratorHook : StyleGeneratorHook {
    override fun generate(token: Token, builder: CssBuilder): Boolean {
        if (token !is SpacingToken) return false
        val declaration = buildSpacingDeclaration(token) ?: return false
        return builder.emitVariantRule(
            rawToken = token.raw,
            stateVariants = token.stateVariants,
            mediaVariant = token.mediaVariant,
            declaration = declaration,
        )
    }

    private fun buildSpacingDeclaration(token: SpacingToken): (CssBuilder.() -> Unit)? {
        return when (token.utility) {
            "m" -> { { margin = Margin(token.value) } }
            "mt" -> { { marginTop = token.value } }
            "mr" -> { { marginRight = token.value } }
            "mb" -> { { marginBottom = token.value } }
            "ml" -> { { marginLeft = token.value } }
            "mx" -> { { marginLeft = token.value; marginRight = token.value } }
            "my" -> { { marginTop = token.value; marginBottom = token.value } }
            "p" -> { { padding = Padding(token.value) } }
            "pt" -> { { paddingTop = token.value } }
            "pr" -> { { paddingRight = token.value } }
            "pb" -> { { paddingBottom = token.value } }
            "pl" -> { { paddingLeft = token.value } }
            "px" -> { { paddingLeft = token.value; paddingRight = token.value } }
            "py" -> { { paddingTop = token.value; paddingBottom = token.value } }
            else -> null
        }
    }
}