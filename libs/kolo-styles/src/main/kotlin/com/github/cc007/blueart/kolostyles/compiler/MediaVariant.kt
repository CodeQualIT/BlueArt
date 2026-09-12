package com.github.cc007.blueart.kolostyles.compiler

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
