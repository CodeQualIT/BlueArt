package io.github.cc007.kolostyles.compiler.font

import io.github.cc007.kolostyles.compiler.StyleGeneratorHook
import io.github.cc007.kolostyles.compiler.Token
import kotlinx.css.CssBuilder
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import org.springframework.stereotype.Component

@Component
class FontGeneratorHook : StyleGeneratorHook {
    override fun generate(token: Token, builder: CssBuilder): Boolean {
        if (token !is FontToken) return false
        return builder.emitVariantRule(
            rawToken = token.raw,
            stateVariants = token.stateVariants,
            mediaVariant = token.mediaVariant,
            declaration = buildFontDeclaration(token),
        )
    }

    private fun buildFontDeclaration(token: FontToken): CssBuilder.() -> Unit = {
        when (token) {
            is FontFamilyToken -> fontFamily = token.fontFamilyValue
            is FontWeightToken -> fontWeight = token.fontWeightValue
            is FontSizeToken -> {
                // TODO: Add line-height, like in TailwindCSS
                fontSize = token.fontSizeValue
            }
        }
    }

}
