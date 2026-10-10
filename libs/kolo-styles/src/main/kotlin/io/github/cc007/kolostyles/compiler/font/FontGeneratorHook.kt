package io.github.cc007.kolostyles.compiler.font

import io.github.cc007.kolostyles.compiler.StyleGeneratorHook
import io.github.cc007.kolostyles.compiler.Token
import io.github.cc007.kolostyles.theme.ThemePropertyCollector
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import org.springframework.stereotype.Component

@Component
class FontGeneratorHook(
    private val themePropertyCollector: ThemePropertyCollector = ThemePropertyCollector(),
) : StyleGeneratorHook {
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
        themePropertyCollector.request(token.themeProperty)
        when (token) {
            is FontFamilyToken -> fontFamily = "var(${token.themeProperty})"
            is FontWeightToken -> fontWeight = FontWeight("var(${token.themeProperty})")
            is FontSizeToken -> {
                fontSize = LinearDimension("var(${token.themeProperty})")
                val lineHeightProperty = "${token.themeProperty}--line-height"
                themePropertyCollector.request(lineHeightProperty)
                lineHeight = LineHeight("var($lineHeightProperty)")
            }
        }
    }

}
