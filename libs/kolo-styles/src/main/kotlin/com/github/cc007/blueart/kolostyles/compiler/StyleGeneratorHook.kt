package com.github.cc007.blueart.kolostyles.compiler

import kotlinx.css.CssBuilder

/**
 * Produces CSS output for a parsed token when supported.
 */
fun interface StyleGeneratorHook {
    fun generate(token: Token, builder: CssBuilder): Boolean

    fun CssBuilder.emitVariantRule(
        rawToken: String,
        stateVariants: List<String>,
        mediaVariant: MediaVariant?,
        declaration: CssBuilder.() -> Unit,
    ): Boolean {
        val pseudoSuffix = stateVariants.joinToString(separator = "") { variant -> ":$variant" }
        val selectorText = ".k-${rawToken.escapeCssClass()}$pseudoSuffix"
        if (mediaVariant == null) {
            selectorText { declaration() }
        } else {
            val mediaProperty = when (mediaVariant.direction) {
                MediaVariantDirection.MIN -> "min-width"
                MediaVariantDirection.MAX -> "max-width"
            }
            media("($mediaProperty: ${mediaVariant.boundary})") {
                selectorText { declaration() }
            }
        }
        return true
    }

    private fun String.escapeCssClass(): String = replace(":", "\\:")
}
