package io.github.cc007.kolostyles.compiler.font

import io.github.cc007.kolostyles.compiler.MediaVariant
import io.github.cc007.kolostyles.compiler.Token

internal sealed interface FontToken : Token {
    val stateVariants: List<String>
    val mediaVariant: MediaVariant?
    val utility: String
    val themeProperty: String
}

internal data class FontFamilyToken(
    override val raw: String,
    override val stateVariants: List<String>,
    override val mediaVariant: MediaVariant?,
    override val utility: String,
    override val themeProperty: String,
) : FontToken

internal data class FontSizeToken(
    override val raw: String,
    override val stateVariants: List<String>,
    override val mediaVariant: MediaVariant?,
    override val utility: String,
    override val themeProperty: String,
) : FontToken

internal data class FontWeightToken(
    override val raw: String,
    override val stateVariants: List<String>,
    override val mediaVariant: MediaVariant?,
    override val utility: String,
    override val themeProperty: String,
) : FontToken
