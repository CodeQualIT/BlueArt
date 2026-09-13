package com.github.cc007.blueart.components.richtext

import io.github.cc007.kolostyles.dsl.font.fontSemiBold
import io.github.cc007.kolostyles.dsl.kolo
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.br
import work.socialhub.kbsky.model.app.bsky.richtext.RichtextFacet
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class TextSegment(
    val text: String,
    val href: String? = null,
    val cssClass: String? = null,
)

fun renderFacets(text: String, facets: List<RichtextFacet>?): List<TextSegment> {
    if (text.isEmpty()) {
        return emptyList()
    }

    val boundaries = buildByteBoundaries(text)
    val ranges = facets.orEmpty()
        .mapNotNull { toFacetRange(it, boundaries) }
        .sortedWith(compareBy<FacetRange> { it.charStart }.thenBy { it.charEnd })

    if (ranges.isEmpty()) {
        return listOf(TextSegment(text = text))
    }

    val segments = mutableListOf<TextSegment>()
    var cursor = 0

    ranges.forEach { range ->
        if (range.charStart < cursor) {
            return@forEach
        }

        if (range.charStart > cursor) {
            segments += TextSegment(text = text.substring(cursor, range.charStart))
        }

        val content = text.substring(range.charStart, range.charEnd)
        segments += when (val feature = range.feature) {
            is FacetFeature.Link -> TextSegment(
                text = content,
                href = feature.uri,
                cssClass = "richtext-link"
            )

            is FacetFeature.Mention -> TextSegment(
                text = content,
                href = mentionUrl(feature.did),
                cssClass = "richtext-mention"
            )

            is FacetFeature.Tag -> TextSegment(
                text = content,
                href = tagUrl(feature.tag),
                cssClass = "richtext-tag"
            )
        }

        cursor = range.charEnd
    }

    if (cursor < text.length) {
        segments += TextSegment(text = text.substring(cursor))
    }

    return segments
}

fun FlowContent.renderRichText(text: String?, facets: List<RichtextFacet>?) {
    renderFacets(text.orEmpty(), facets).forEach { segment ->
        val href = segment.href
        if (href == null) {
            val lines = segment.text.split('\n')
            lines.forEachIndexed { index, line ->
                +line
                if (index < lines.lastIndex) {
                    br {}
                }
            }
        } else {
            a(href = href, classes = segment.cssClass.orEmpty()) {
                target = "_blank"
                rel = "noopener noreferrer nofollow"
                kolo { fontSemiBold }
                val lines = segment.text.split('\n')
                lines.forEachIndexed { index, line ->
                    +line
                    if (index < lines.lastIndex) {
                        br {}
                    }
                }
            }
        }
    }
}

private data class ByteBoundaries(
    val byteToChar: Map<Int, Int>,
    val totalBytes: Int,
)

private data class FacetRange(
    val charStart: Int,
    val charEnd: Int,
    val feature: FacetFeature,
)

private sealed interface FacetFeature {
    data class Link(val uri: String) : FacetFeature
    data class Mention(val did: String) : FacetFeature
    data class Tag(val tag: String) : FacetFeature
}

private fun toFacetRange(facet: RichtextFacet, boundaries: ByteBoundaries): FacetRange? {
    val index = facet.index ?: return null
    val byteStart = index.byteStart ?: return null
    val byteEnd = index.byteEnd ?: return null

    if (byteStart !in 0..<byteEnd || byteEnd > boundaries.totalBytes) {
        return null
    }

    val charStart = boundaries.byteToChar[byteStart] ?: return null
    val charEnd = boundaries.byteToChar[byteEnd] ?: return null

    val feature = facet.features.orEmpty().firstNotNullOfOrNull { facetFeature ->
        facetFeature.asLink?.uri
            ?.trim()
            ?.takeIf { isExternalHttpUrl(it) }
            ?.let { FacetFeature.Link(it) }
            ?: facetFeature.asMention?.did
                ?.trim()
                ?.takeIf { isDid(it) }
                ?.let { FacetFeature.Mention(it) }
            ?: facetFeature.asTag?.tag
                ?.trim()
                ?.trimStart('#')
                ?.takeIf { it.isNotBlank() }
                ?.let { FacetFeature.Tag(it) }
    } ?: return null

    return FacetRange(
        charStart = charStart,
        charEnd = charEnd,
        feature = feature,
    )
}

private fun buildByteBoundaries(text: String): ByteBoundaries {
    val boundaries = HashMap<Int, Int>()
    var charIndex = 0
    var byteIndex = 0

    while (charIndex < text.length) {
        boundaries[byteIndex] = charIndex
        val codePoint = text.codePointAt(charIndex)
        byteIndex += utf8Length(codePoint)
        charIndex += Character.charCount(codePoint)
    }

    boundaries[byteIndex] = text.length
    return ByteBoundaries(byteToChar = boundaries, totalBytes = byteIndex)
}

private fun utf8Length(codePoint: Int): Int = when {
    codePoint <= 0x7F -> 1
    codePoint <= 0x7FF -> 2
    codePoint <= 0xFFFF -> 3
    else -> 4
}

private fun tagUrl(tag: String): String {
    val encodedTag = URLEncoder.encode(tag, StandardCharsets.UTF_8).replace("+", "%20")
    return "https://bsky.app/hashtag/$encodedTag"
}

private fun mentionUrl(did: String): String = "https://bsky.app/profile/$did"

private fun isExternalHttpUrl(value: String): Boolean {
    return value.startsWith("https://") || value.startsWith("http://")
}

private fun isDid(value: String): Boolean = value.startsWith("did:")
