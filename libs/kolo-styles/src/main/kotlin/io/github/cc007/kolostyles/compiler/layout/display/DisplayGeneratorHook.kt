package io.github.cc007.kolostyles.compiler.layout.display

import io.github.cc007.kolostyles.compiler.StyleGeneratorHook
import io.github.cc007.kolostyles.compiler.Token
import kotlinx.css.CssBuilder
import kotlinx.css.display
import org.springframework.stereotype.Component

@Component
class DisplayGeneratorHook : StyleGeneratorHook {
    override fun generate(token: Token, builder: CssBuilder): Boolean {
        val displayToken = token as? DisplayToken ?: return false
        return builder.emitVariantRule(
            rawToken = displayToken.raw,
            stateVariants = displayToken.stateVariants,
            mediaVariant = displayToken.mediaVariant,
        ) {
            display = displayToken.displayValue
        }
    }
}
