package io.github.cc007.kolostyles.compiler

import kotlinx.css.LinearDimension

enum class MediaVariantDirection {
    MIN,
    MAX,
}

data class MediaVariant(
    val variant: String,
    val direction: MediaVariantDirection,
    val boundary: LinearDimension,
)
