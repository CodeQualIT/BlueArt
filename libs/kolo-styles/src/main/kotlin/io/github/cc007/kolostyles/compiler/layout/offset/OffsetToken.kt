package io.github.cc007.kolostyles.compiler.layout.offset

import io.github.cc007.kolostyles.compiler.MediaVariant
import io.github.cc007.kolostyles.compiler.Token
import kotlinx.css.LinearDimension

internal data class OffsetToken(
    override val raw: String,
    val stateVariants: List<String>,
    val mediaVariant: MediaVariant?,
    val utility: String,
    val value: LinearDimension,
) : Token
