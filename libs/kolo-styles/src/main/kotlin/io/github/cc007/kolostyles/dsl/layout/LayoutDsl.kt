package io.github.cc007.kolostyles.dsl.layout

import io.github.cc007.kolostyles.dsl.KoloScope
import io.github.cc007.kolostyles.dsl.KoloVariantScope

private fun KoloScope.recordLayout(token: String) {
    recordBase(token)
}

private fun KoloVariantScope.recordLayout(token: String) {
    recordBase(token)
}

val KoloScope.boxBorder: Unit get() = recordLayout("box-border")
val KoloScope.boxContent: Unit get() = recordLayout("box-content")
val KoloVariantScope.boxBorder: Unit get() = recordLayout("box-border")
val KoloVariantScope.boxContent: Unit get() = recordLayout("box-content")

val KoloScope.overflowAuto: Unit get() = recordLayout("overflow-auto")
val KoloScope.overflowHidden: Unit get() = recordLayout("overflow-hidden")
val KoloScope.overflowClip: Unit get() = recordLayout("overflow-clip")
val KoloScope.overflowVisible: Unit get() = recordLayout("overflow-visible")
val KoloScope.overflowScroll: Unit get() = recordLayout("overflow-scroll")
val KoloScope.overflowXAuto: Unit get() = recordLayout("overflow-x-auto")
val KoloScope.overflowXHidden: Unit get() = recordLayout("overflow-x-hidden")
val KoloScope.overflowXClip: Unit get() = recordLayout("overflow-x-clip")
val KoloScope.overflowXVisible: Unit get() = recordLayout("overflow-x-visible")
val KoloScope.overflowXScroll: Unit get() = recordLayout("overflow-x-scroll")
val KoloScope.overflowYAuto: Unit get() = recordLayout("overflow-y-auto")
val KoloScope.overflowYHidden: Unit get() = recordLayout("overflow-y-hidden")
val KoloScope.overflowYClip: Unit get() = recordLayout("overflow-y-clip")
val KoloScope.overflowYVisible: Unit get() = recordLayout("overflow-y-visible")
val KoloScope.overflowYScroll: Unit get() = recordLayout("overflow-y-scroll")
val KoloVariantScope.overflowAuto: Unit get() = recordLayout("overflow-auto")
val KoloVariantScope.overflowHidden: Unit get() = recordLayout("overflow-hidden")
val KoloVariantScope.overflowClip: Unit get() = recordLayout("overflow-clip")
val KoloVariantScope.overflowVisible: Unit get() = recordLayout("overflow-visible")
val KoloVariantScope.overflowScroll: Unit get() = recordLayout("overflow-scroll")
val KoloVariantScope.overflowXAuto: Unit get() = recordLayout("overflow-x-auto")
val KoloVariantScope.overflowXHidden: Unit get() = recordLayout("overflow-x-hidden")
val KoloVariantScope.overflowXClip: Unit get() = recordLayout("overflow-x-clip")
val KoloVariantScope.overflowXVisible: Unit get() = recordLayout("overflow-x-visible")
val KoloVariantScope.overflowXScroll: Unit get() = recordLayout("overflow-x-scroll")
val KoloVariantScope.overflowYAuto: Unit get() = recordLayout("overflow-y-auto")
val KoloVariantScope.overflowYHidden: Unit get() = recordLayout("overflow-y-hidden")
val KoloVariantScope.overflowYClip: Unit get() = recordLayout("overflow-y-clip")
val KoloVariantScope.overflowYVisible: Unit get() = recordLayout("overflow-y-visible")
val KoloVariantScope.overflowYScroll: Unit get() = recordLayout("overflow-y-scroll")

val KoloScope.static: Unit get() = recordLayout("static")
val KoloScope.relative: Unit get() = recordLayout("relative")
val KoloScope.absolute: Unit get() = recordLayout("absolute")
val KoloScope.fixed: Unit get() = recordLayout("fixed")
val KoloScope.sticky: Unit get() = recordLayout("sticky")
val KoloVariantScope.static: Unit get() = recordLayout("static")
val KoloVariantScope.relative: Unit get() = recordLayout("relative")
val KoloVariantScope.absolute: Unit get() = recordLayout("absolute")
val KoloVariantScope.fixed: Unit get() = recordLayout("fixed")
val KoloVariantScope.sticky: Unit get() = recordLayout("sticky")

val KoloScope.zAuto: Unit get() = recordLayout("z-auto")
val KoloVariantScope.zAuto: Unit get() = recordLayout("z-auto")
fun KoloScope.z(value: Int) {
    require(value > 0) { "z-index utilities require positive integers or zAuto" }
    recordLayout("z-$value")
}

fun KoloVariantScope.z(value: Int) {
    require(value > 0) { "z-index utilities require positive integers or zAuto" }
    recordLayout("z-$value")
}

val KoloScope.objectContain: Unit get() = recordLayout("object-contain")
val KoloScope.objectCover: Unit get() = recordLayout("object-cover")
val KoloScope.objectFill: Unit get() = recordLayout("object-fill")
val KoloScope.objectNone: Unit get() = recordLayout("object-none")
val KoloScope.objectScaleDown: Unit get() = recordLayout("object-scale-down")
val KoloVariantScope.objectContain: Unit get() = recordLayout("object-contain")
val KoloVariantScope.objectCover: Unit get() = recordLayout("object-cover")
val KoloVariantScope.objectFill: Unit get() = recordLayout("object-fill")
val KoloVariantScope.objectNone: Unit get() = recordLayout("object-none")
val KoloVariantScope.objectScaleDown: Unit get() = recordLayout("object-scale-down")
