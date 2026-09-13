package io.github.cc007.kolostyles.compiler.spacing

import io.github.cc007.kolostyles.compiler.MediaVariant
import io.github.cc007.kolostyles.compiler.Token
import kotlinx.css.LinearDimension

internal data class SpacingToken(
    override val raw: String,
    val stateVariants: List<String>,
    val mediaVariant: MediaVariant?,
    val utility: String,
    val value: LinearDimension,
) : Token