package io.github.cc007.kolostyles.compiler

import kotlinx.css.rem

internal val KOLO_STATE_VARIANTS = setOf("hover", "focus", "focus-visible", "active", "visited")

internal val KOLO_RESPONSIVE_VARIANTS: Map<String, MediaVariant> = mapOf(
    "sm" to MediaVariant("sm", MediaVariantDirection.MIN, 40.rem),
    "md" to MediaVariant("md", MediaVariantDirection.MIN, 48.rem),
    "lg" to MediaVariant("lg", MediaVariantDirection.MIN, 64.rem),
    "xl" to MediaVariant("xl", MediaVariantDirection.MIN, 80.rem),
    "2xl" to MediaVariant("2xl", MediaVariantDirection.MIN, 96.rem),
    "max-sm" to MediaVariant("max-sm", MediaVariantDirection.MAX, 40.rem),
    "max-md" to MediaVariant("max-md", MediaVariantDirection.MAX, 48.rem),
    "max-lg" to MediaVariant("max-lg", MediaVariantDirection.MAX, 64.rem),
    "max-xl" to MediaVariant("max-xl", MediaVariantDirection.MAX, 80.rem),
    "max-2xl" to MediaVariant("max-2xl", MediaVariantDirection.MAX, 96.rem),
)

internal data class ParsedKoloVariants(
    val stateVariants: List<String>,
    val mediaVariant: MediaVariant?,
)

internal fun parseKoloVariants(variants: List<String>): ParsedKoloVariants? {
    if (variants.any { it !in KOLO_STATE_VARIANTS && it !in KOLO_RESPONSIVE_VARIANTS }) {
        return null
    }

    val mediaVariants = variants.mapNotNull(KOLO_RESPONSIVE_VARIANTS::get)
    if (mediaVariants.size > 1) {
        return null
    }

    return ParsedKoloVariants(
        stateVariants = variants.filter { it in KOLO_STATE_VARIANTS },
        mediaVariant = mediaVariants.firstOrNull(),
    )
}
