package io.github.cc007.kolostyles.compiler.layout

import io.github.cc007.kolostyles.compiler.MediaVariant
import io.github.cc007.kolostyles.compiler.Token
import kotlinx.css.BoxSizing
import kotlinx.css.ObjectFit
import kotlinx.css.Overflow
import kotlinx.css.Position

internal interface LayoutToken : Token {
    val stateVariants: List<String>
    val mediaVariant: MediaVariant?
}

internal data class BoxSizingToken(
    override val raw: String,
    override val stateVariants: List<String>,
    override val mediaVariant: MediaVariant?,
    val utility: String,
    val value: BoxSizing,
) : LayoutToken

internal enum class OverflowAxis {
    BOTH,
    X,
    Y,
}

internal data class OverflowToken(
    override val raw: String,
    override val stateVariants: List<String>,
    override val mediaVariant: MediaVariant?,
    val utility: String,
    val axis: OverflowAxis,
    val value: Overflow,
) : LayoutToken

internal data class PositionToken(
    override val raw: String,
    override val stateVariants: List<String>,
    override val mediaVariant: MediaVariant?,
    val utility: String,
    val value: Position,
) : LayoutToken

internal data class ZIndexToken(
    override val raw: String,
    override val stateVariants: List<String>,
    override val mediaVariant: MediaVariant?,
    val utility: String,
    val zIndex: Int?,
) : LayoutToken

internal data class ObjectFitToken(
    override val raw: String,
    override val stateVariants: List<String>,
    override val mediaVariant: MediaVariant?,
    val utility: String,
    val value: ObjectFit,
) : LayoutToken
