package io.github.cc007.kolostyles.dsl.spacing

import io.github.cc007.kolostyles.dsl.KoloScope
import io.github.cc007.kolostyles.dsl.KoloVariantScope

private fun KoloScope.recordSpacing(prefix: String, value: Int) {
    require(value >= 0) { "Spacing utilities require non-negative values" }
    recordBase("$prefix-$value")
}

private fun KoloVariantScope.recordSpacing(prefix: String, value: Int) {
    require(value >= 0) { "Spacing utilities require non-negative values" }
    recordBase("$prefix-$value")
}

private fun KoloScope.recordAutoSpacing(prefix: String) {
    recordBase("$prefix-auto")
}

private fun KoloVariantScope.recordAutoSpacing(prefix: String) {
    recordBase("$prefix-auto")
}

fun KoloScope.m(value: Int) = recordSpacing("m", value)
val KoloScope.mAuto: Unit get() = recordAutoSpacing("m")
fun KoloScope.mt(value: Int) = recordSpacing("mt", value)
val KoloScope.mtAuto: Unit get() = recordAutoSpacing("mt")
fun KoloScope.mr(value: Int) = recordSpacing("mr", value)
val KoloScope.mrAuto: Unit get() = recordAutoSpacing("mr")
fun KoloScope.mb(value: Int) = recordSpacing("mb", value)
val KoloScope.mbAuto: Unit get() = recordAutoSpacing("mb")
fun KoloScope.ml(value: Int) = recordSpacing("ml", value)
val KoloScope.mlAuto: Unit get() = recordAutoSpacing("ml")
fun KoloScope.mx(value: Int) = recordSpacing("mx", value)
val KoloScope.mxAuto: Unit get() = recordAutoSpacing("mx")
fun KoloScope.my(value: Int) = recordSpacing("my", value)
val KoloScope.myAuto: Unit get() = recordAutoSpacing("my")
fun KoloScope.p(value: Int) = recordSpacing("p", value)
val KoloScope.pAuto: Unit get() = recordAutoSpacing("p")
fun KoloScope.pt(value: Int) = recordSpacing("pt", value)
val KoloScope.ptAuto: Unit get() = recordAutoSpacing("pt")
fun KoloScope.pr(value: Int) = recordSpacing("pr", value)
val KoloScope.prAuto: Unit get() = recordAutoSpacing("pr")
fun KoloScope.pb(value: Int) = recordSpacing("pb", value)
val KoloScope.pbAuto: Unit get() = recordAutoSpacing("pb")
fun KoloScope.pl(value: Int) = recordSpacing("pl", value)
val KoloScope.plAuto: Unit get() = recordAutoSpacing("pl")
fun KoloScope.px(value: Int) = recordSpacing("px", value)
val KoloScope.pxAuto: Unit get() = recordAutoSpacing("px")
fun KoloScope.py(value: Int) = recordSpacing("py", value)
val KoloScope.pyAuto: Unit get() = recordAutoSpacing("py")

fun KoloVariantScope.m(value: Int) = recordSpacing("m", value)
val KoloVariantScope.mAuto: Unit get() = recordAutoSpacing("m")
fun KoloVariantScope.mt(value: Int) = recordSpacing("mt", value)
val KoloVariantScope.mtAuto: Unit get() = recordAutoSpacing("mt")
fun KoloVariantScope.mr(value: Int) = recordSpacing("mr", value)
val KoloVariantScope.mrAuto: Unit get() = recordAutoSpacing("mr")
fun KoloVariantScope.mb(value: Int) = recordSpacing("mb", value)
val KoloVariantScope.mbAuto: Unit get() = recordAutoSpacing("mb")
fun KoloVariantScope.ml(value: Int) = recordSpacing("ml", value)
val KoloVariantScope.mlAuto: Unit get() = recordAutoSpacing("ml")
fun KoloVariantScope.mx(value: Int) = recordSpacing("mx", value)
val KoloVariantScope.mxAuto: Unit get() = recordAutoSpacing("mx")
fun KoloVariantScope.my(value: Int) = recordSpacing("my", value)
val KoloVariantScope.myAuto: Unit get() = recordAutoSpacing("my")
fun KoloVariantScope.p(value: Int) = recordSpacing("p", value)
val KoloVariantScope.pAuto: Unit get() = recordAutoSpacing("p")
fun KoloVariantScope.pt(value: Int) = recordSpacing("pt", value)
val KoloVariantScope.ptAuto: Unit get() = recordAutoSpacing("pt")
fun KoloVariantScope.pr(value: Int) = recordSpacing("pr", value)
val KoloVariantScope.prAuto: Unit get() = recordAutoSpacing("pr")
fun KoloVariantScope.pb(value: Int) = recordSpacing("pb", value)
val KoloVariantScope.pbAuto: Unit get() = recordAutoSpacing("pb")
fun KoloVariantScope.pl(value: Int) = recordSpacing("pl", value)
val KoloVariantScope.plAuto: Unit get() = recordAutoSpacing("pl")
fun KoloVariantScope.px(value: Int) = recordSpacing("px", value)
val KoloVariantScope.pxAuto: Unit get() = recordAutoSpacing("px")
fun KoloVariantScope.py(value: Int) = recordSpacing("py", value)
val KoloVariantScope.pyAuto: Unit get() = recordAutoSpacing("py")
