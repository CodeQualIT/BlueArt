package com.github.cc007.blueart.components.richtext

import com.github.cc007.blueart.testsupport.parseHtml
import com.github.cc007.blueart.testsupport.selectRequired
import io.github.cc007.kolostyles.dsl.koloStylesheetLink
import io.github.cc007.kolostyles.dsl.renderKoloHtml
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.stream.createHTML
import work.socialhub.kbsky.model.app.bsky.richtext.*
import kotlin.test.Test

class RichTextFacetRendererTest {

    @Test
    fun `renders utf8 byte ranges for link and tag facets`() {
        val text = "A😀 check #art at https://example.com"
        val tagRange = byteRangeOf(text, "#art")
        val linkRange = byteRangeOf(text, "https://example.com")

        val segments = renderFacets(
            text = text,
            facets = listOf(
                tagFacet(tagRange.first, tagRange.second, "art"),
                linkFacet(linkRange.first, linkRange.second, "https://example.com")
            )
        )

        segments.shouldHaveSize(4)
        segments[0].text shouldBe "A😀 check "
        segments[0].href.shouldBeNull()
        segments[1].text shouldBe "#art"
        segments[1].href shouldBe "https://bsky.app/hashtag/art"
        segments[2].text shouldBe " at "
        segments[2].href.shouldBeNull()
        segments[3].text shouldBe "https://example.com"
        segments[3].href shouldBe "https://example.com"
    }

    @Test
    fun `ignores overlapping and malformed facets while keeping valid ranges`() {
        val text = "one two three"
        val twoRange = byteRangeOf(text, "two")
        val overlapRange = byteRangeOf(text, "two three")

        val malformed = linkFacet(40, 45, "https://invalid.example")
        val overlapping = tagFacet(overlapRange.first, overlapRange.second, "overlap")
        val valid = linkFacet(twoRange.first, twoRange.second, "https://two.example")

        val segments = renderFacets(text, listOf(malformed, overlapping, valid))

        segments.shouldHaveSize(3)
        segments[0].text shouldBe "one "
        segments[1].text shouldBe "two"
        segments[1].href shouldBe "https://two.example"
        segments[2].text shouldBe " three"
    }

    @Test
    fun `filters non-http link facets`() {
        val text = "visit ftp://example.com"
        val linkRange = byteRangeOf(text, "ftp://example.com")

        val segments = renderFacets(
            text,
            listOf(linkFacet(linkRange.first, linkRange.second, "ftp://example.com"))
        )

        segments.shouldHaveSize(1)
        segments[0].text shouldBe text
        segments[0].href.shouldBeNull()
    }

    @Test
    fun `renders mention facets as bluesky profile links`() {
        val text = "hi @alice.example"
        val mentionRange = byteRangeOf(text, "@alice.example")

        val segments = renderFacets(
            text,
            listOf(mentionFacet(mentionRange.first, mentionRange.second, "did:plc:alice123"))
        )

        segments.shouldHaveSize(2)
        segments[0].text shouldBe "hi "
        segments[0].href.shouldBeNull()
        segments[1].text shouldBe "@alice.example"
        segments[1].href shouldBe "https://bsky.app/profile/did:plc:alice123"
        segments[1].cssClass shouldBe "richtext-mention"
    }

    @Test
    fun `renders mixed mention tag and link facets in order`() {
        val text = "hi @alice.example check #art https://example.com"
        val mentionRange = byteRangeOf(text, "@alice.example")
        val tagRange = byteRangeOf(text, "#art")
        val linkRange = byteRangeOf(text, "https://example.com")

        val segments = renderFacets(
            text,
            listOf(
                linkFacet(linkRange.first, linkRange.second, "https://example.com"),
                mentionFacet(mentionRange.first, mentionRange.second, "did:plc:alice123"),
                tagFacet(tagRange.first, tagRange.second, "art")
            )
        )

        segments shouldContainExactly listOf(
            TextSegment("hi "),
            TextSegment("@alice.example", "https://bsky.app/profile/did:plc:alice123", "richtext-mention"),
            TextSegment(" check "),
            TextSegment("#art", "https://bsky.app/hashtag/art", "richtext-tag"),
            TextSegment(" "),
            TextSegment("https://example.com", "https://example.com", "richtext-link")
        )
    }

    @Test
    fun `filters malformed mention facets`() {
        val text = "hi @alice.example"
        val mentionRange = byteRangeOf(text, "@alice.example")

        val segments = renderFacets(
            text,
            listOf(mentionFacet(mentionRange.first, mentionRange.second, "alice.example"))
        )

        segments.shouldHaveSize(1)
        segments[0].text shouldBe text
        segments[0].href.shouldBeNull()
    }

    @Test
    fun `renders newlines as br tags for plain text segments`() {
        val html = createHTML().p {
            renderRichText("line one\nline two\nline three", null)
        }

        html shouldBe "<p>line one<br>line two<br>line three</p>\n"
    }

    @Test
    fun `renders newlines as br tags inside linked segments`() {
        val text = "https://example.com/path\nmore"
        val linkRange = 0 to text.toByteArray(Charsets.UTF_8).size
        val html = createHTML().p {
            renderRichText(text, listOf(linkFacet(linkRange.first, linkRange.second, "https://example.com/path")))
        }

        html shouldBe "<p><a href=\"https://example.com/path\" class=\"richtext-link\" target=\"_blank\" rel=\"noopener noreferrer nofollow\">https://example.com/path<br>more</a></p>\n"
    }

    @Test
    fun `mention and tag links emit font semibold kolo utility during render`() {
        val text = "hi @alice #art"
        val mentionRange = byteRangeOf(text, "@alice")
        val tagRange = byteRangeOf(text, "#art")

        val html = renderKoloHtml(version = "abc123") {
            head { koloStylesheetLink() }
            body {
                p {
                    renderRichText(
                        text,
                        listOf(
                            mentionFacet(mentionRange.first, mentionRange.second, "did:plc:alice123"),
                            tagFacet(tagRange.first, tagRange.second, "art"),
                        )
                    )
                }
            }
        }

        val document = html.parseHtml()
        document.selectRequired("a.richtext-mention.k-font-semibold")
        document.selectRequired("a.richtext-tag.k-font-semibold")
        document.selectRequired("link[rel=stylesheet]").attr("href").shouldContain("kolo=font-semibold")
    }

    private fun linkFacet(start: Int, end: Int, uri: String): RichtextFacet {
        return RichtextFacet().apply {
            index = RichtextFacetByteSlice().apply {
                byteStart = start
                byteEnd = end
            }
            features = mutableListOf(
                RichtextFacetLink().apply {
                    this.uri = uri
                }
            )
        }
    }

    private fun mentionFacet(start: Int, end: Int, did: String): RichtextFacet {
        return RichtextFacet().apply {
            index = RichtextFacetByteSlice().apply {
                byteStart = start
                byteEnd = end
            }
            features = mutableListOf(
                RichtextFacetMention().apply {
                    this.did = did
                }
            )
        }
    }

    private fun tagFacet(start: Int, end: Int, tag: String): RichtextFacet {
        return RichtextFacet().apply {
            index = RichtextFacetByteSlice().apply {
                byteStart = start
                byteEnd = end
            }
            features = mutableListOf(
                RichtextFacetTag().apply {
                    this.tag = tag
                }
            )
        }
    }

    private fun byteRangeOf(text: String, part: String): Pair<Int, Int> {
        val startChar = text.indexOf(part)
        require(startChar >= 0) { "Missing fragment '$part' in '$text'" }
        val endChar = startChar + part.length
        val startByte = text.substring(0, startChar).toByteArray(Charsets.UTF_8).size
        val endByte = text.substring(0, endChar).toByteArray(Charsets.UTF_8).size
        return startByte to endByte
    }
}
