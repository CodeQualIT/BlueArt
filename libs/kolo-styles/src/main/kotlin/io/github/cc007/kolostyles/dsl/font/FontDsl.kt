package io.github.cc007.kolostyles.dsl.font

import io.github.cc007.kolostyles.dsl.KoloScope
import io.github.cc007.kolostyles.dsl.KoloVariantScope

private fun KoloScope.recordFont(token: String) {
    recordBase(token)
}

private fun KoloVariantScope.recordFont(token: String) {
    recordBase(token)
}

val KoloScope.fontSans: Unit get() = recordFont("font-sans")
val KoloScope.fontSerif: Unit get() = recordFont("font-serif")
val KoloScope.fontMono: Unit get() = recordFont("font-mono")

val KoloScope.textXs: Unit get() = recordFont("text-xs")
val KoloScope.textSm: Unit get() = recordFont("text-sm")
val KoloScope.textBase: Unit get() = recordFont("text-base")
val KoloScope.textLg: Unit get() = recordFont("text-lg")
val KoloScope.textXl: Unit get() = recordFont("text-xl")
val KoloScope.text2xl: Unit get() = recordFont("text-2xl")
val KoloScope.text3xl: Unit get() = recordFont("text-3xl")
val KoloScope.text4xl: Unit get() = recordFont("text-4xl")
val KoloScope.text5xl: Unit get() = recordFont("text-5xl")
val KoloScope.text6xl: Unit get() = recordFont("text-6xl")
val KoloScope.text7xl: Unit get() = recordFont("text-7xl")
val KoloScope.text8xl: Unit get() = recordFont("text-8xl")
val KoloScope.text9xl: Unit get() = recordFont("text-9xl")

val KoloScope.fontThin: Unit get() = recordFont("font-thin")
val KoloScope.fontExtraLight: Unit get() = recordFont("font-extralight")
val KoloScope.fontLight: Unit get() = recordFont("font-light")
val KoloScope.fontNormal: Unit get() = recordFont("font-normal")
val KoloScope.fontMedium: Unit get() = recordFont("font-medium")
val KoloScope.fontSemiBold: Unit get() = recordFont("font-semibold")
val KoloScope.fontBold: Unit get() = recordFont("font-bold")
val KoloScope.fontExtraBold: Unit get() = recordFont("font-extrabold")
val KoloScope.fontBlack: Unit get() = recordFont("font-black")

val KoloVariantScope.fontSans: Unit get() = recordFont("font-sans")
val KoloVariantScope.fontSerif: Unit get() = recordFont("font-serif")
val KoloVariantScope.fontMono: Unit get() = recordFont("font-mono")

val KoloVariantScope.textXs: Unit get() = recordFont("text-xs")
val KoloVariantScope.textSm: Unit get() = recordFont("text-sm")
val KoloVariantScope.textBase: Unit get() = recordFont("text-base")
val KoloVariantScope.textLg: Unit get() = recordFont("text-lg")
val KoloVariantScope.textXl: Unit get() = recordFont("text-xl")
val KoloVariantScope.text2xl: Unit get() = recordFont("text-2xl")
val KoloVariantScope.text3xl: Unit get() = recordFont("text-3xl")
val KoloVariantScope.text4xl: Unit get() = recordFont("text-4xl")
val KoloVariantScope.text5xl: Unit get() = recordFont("text-5xl")
val KoloVariantScope.text6xl: Unit get() = recordFont("text-6xl")
val KoloVariantScope.text7xl: Unit get() = recordFont("text-7xl")
val KoloVariantScope.text8xl: Unit get() = recordFont("text-8xl")
val KoloVariantScope.text9xl: Unit get() = recordFont("text-9xl")

val KoloVariantScope.fontThin: Unit get() = recordFont("font-thin")
val KoloVariantScope.fontExtraLight: Unit get() = recordFont("font-extralight")
val KoloVariantScope.fontLight: Unit get() = recordFont("font-light")
val KoloVariantScope.fontNormal: Unit get() = recordFont("font-normal")
val KoloVariantScope.fontMedium: Unit get() = recordFont("font-medium")
val KoloVariantScope.fontSemiBold: Unit get() = recordFont("font-semibold")
val KoloVariantScope.fontBold: Unit get() = recordFont("font-bold")
val KoloVariantScope.fontExtraBold: Unit get() = recordFont("font-extrabold")
val KoloVariantScope.fontBlack: Unit get() = recordFont("font-black")
