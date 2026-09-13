package io.github.cc007.kolostyles.compiler.layout.display

import io.github.cc007.kolostyles.compiler.MediaVariant
import io.github.cc007.kolostyles.compiler.layout.LayoutToken
import kotlinx.css.Display

internal data class DisplayToken(
    override val raw: String,
    override val stateVariants: List<String>,
    override val mediaVariant: MediaVariant?,
    val utility: String,
    val displayValue: Display,
) : LayoutToken
