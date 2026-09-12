package com.github.cc007.blueart.kolostyles.dsl

import com.github.cc007.blueart.kolostyles.dsl.font.*
import com.github.cc007.blueart.kolostyles.dsl.layout.*
import com.github.cc007.blueart.kolostyles.dsl.layout.display.*
import com.github.cc007.blueart.kolostyles.dsl.layout.offset.insetX
import com.github.cc007.blueart.kolostyles.dsl.layout.offset.top
import com.github.cc007.blueart.kolostyles.dsl.sizing.*
import com.github.cc007.blueart.kolostyles.dsl.sizing.SizingAlpha.MD
import com.github.cc007.blueart.kolostyles.dsl.sizing.SizingNamed.*
import com.github.cc007.blueart.kolostyles.dsl.spacing.*
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.stream.createHTML
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import kotlin.test.Test

class KoloHtmlRuntimeTest {

    @Test
    fun `canonicalize dedupes sorts and drops unsupported tokens`() {
        val canonical = canonicalizeKoloTokens(
            listOf(
                " md:mt-2 ",
                "flex",
                "hover:bg-sky-500",
                "md:mt-2",
                "mt-[2px]",
                "invalid;token",
                "",
            )
        )

        canonical shouldBe "hover:bg-sky-500;flex;invalid;md:mt-2;token"
    }

    @Test
    fun `renderKoloHtml emits canonicalized stylesheet href from collected tokens`() {
        val html = renderKoloHtml(version = "abc123") {
            head {
                koloStylesheetLink()
            }
            body {
                div {
                    kolo {
                        variant("md").recordBase("mt-2")
                        recordBase("flex")
                        variant("hover").recordBase("bg-sky-500")
                        variant("md").recordBase("mt-2")
                    }
                }
            }
        }

        val expected = """
            <html>
              <head>
                <link href="/css/generated/kolo.css?version=abc123&kolo=hover%3Abg-sky-500%3Bflex%3Bmd%3Amt-2" rel="stylesheet">
              </head>
              <body>
                <div class="k-md:mt-2 k-flex k-hover:bg-sky-500"></div>
              </body>
            </html>
        """.trimIndent() + "\n"
        html shouldBe expected
    }

    @Test
    fun `kolo can attach classes via mapper when explicitly provided`() {
        val html = renderKoloHtml(
            version = "abc123",
            classNameMapper = { token -> "k-${token}" }
        ) {
            head {
                koloStylesheetLink()
            }
            body {
                div(classes = "existing") {
                    kolo {
                        recordBase("flex")
                    }
                }
            }
        }

        val expected = """
            <html>
              <head>
                <link href="/css/generated/kolo.css?version=abc123&kolo=flex" rel="stylesheet">
              </head>
              <body>
                <div class="existing k-flex"></div>
              </body>
            </html>
        """.trimIndent() + "\n"
        html shouldBe expected
    }

    @Test
    fun `kolo variant scaffold can compose nested variant tokens`() {
        val html = renderKoloHtml(version = "abc123") {
            head {
                koloStylesheetLink()
            }
            body {
                div {
                    kolo {
                        variant("dark").variant("md").recordBase("bg-sky-500")
                    }
                }
            }
        }

        val expected = """
            <html>
              <head>
                <link href="/css/generated/kolo.css?version=abc123&kolo=dark%3Amd%3Abg-sky-500" rel="stylesheet">
              </head>
              <body>
                <div class="k-dark:md:bg-sky-500"></div>
              </body>
            </html>
        """.trimIndent() + "\n"
        html shouldBe expected
    }

    @Test
    fun `kolo collects tokens across multiple elements deduplicating overlaps in the stylesheet href`() {
        val html = renderKoloHtml(
            version = "abc123",
            classNameMapper = { token -> "k-$token" },
        ) {
            head {
                koloStylesheetLink()
            }
            body {
                div {
                    kolo {
                        recordBase("flex")  // overlapping
                        recordBase("mt-2")  // unique to this element
                    }
                }
                div {
                    kolo {
                        recordBase("flex")  // overlapping
                        recordBase("px-4")  // unique to this element
                    }
                }
            }
        }

        // The href contains the deduplicated union of all tokens across all elements
        // Per-element classes reflect only that element's own tokens
        val expected = """
            <html>
              <head>
                <link href="/css/generated/kolo.css?version=abc123&kolo=flex%3Bmt-2%3Bpx-4" rel="stylesheet">
              </head>
              <body>
                <div class="k-flex k-mt-2"></div>
                <div class="k-flex k-px-4"></div>
              </body>
            </html>
        """.trimIndent() + "\n"
        html shouldBe expected
    }

    @Test
    fun `kolo noops when called outside kolo rendering context`() {
        val html = createHTML().html {
            body {
                div {
                    kolo {
                        recordBase("flex")
                    }
                }
            }
        }

        val expected = """
            <html>
              <body>
                <div></div>
              </body>
            </html>
        """.trimIndent() + "\n"
        html shouldBe expected
    }

    @Test
    fun `kolo spacing dsl supports auto spacing variants`() {
        val html = renderKoloHtml(version = "abc123") {
            head {
                koloStylesheetLink()
            }
            body {
                div {
                    kolo {
                        mAuto
                        mtAuto
                        mrAuto
                        mbAuto
                        mlAuto
                        mxAuto
                        myAuto
                        pAuto
                        ptAuto
                        prAuto
                        pbAuto
                        plAuto
                        pxAuto
                        pyAuto
                        variant("md").mtAuto
                        variant("md").ptAuto
                    }
                }
            }
        }

        val expected = """
            <html>
              <head>
                <link href="/css/generated/kolo.css?version=abc123&kolo=m-auto%3Bmb-auto%3Bml-auto%3Bmr-auto%3Bmt-auto%3Bmd%3Amt-auto%3Bmx-auto%3Bmy-auto%3Bp-auto%3Bpb-auto%3Bpl-auto%3Bpr-auto%3Bpt-auto%3Bmd%3Apt-auto%3Bpx-auto%3Bpy-auto" rel="stylesheet">
              </head>
              <body>
                <div class="k-m-auto k-mt-auto k-mr-auto k-mb-auto k-ml-auto k-mx-auto k-my-auto k-p-auto k-pt-auto k-pr-auto k-pb-auto k-pl-auto k-px-auto k-py-auto k-md:mt-auto k-md:pt-auto"></div>
              </body>
            </html>
        """.trimIndent() + "\n"
        html shouldBe expected
    }

    @Test
    fun `renderKoloHtml emits canonicalized href and class names for display dsl tokens`() {
        val html = renderKoloHtml(version = "abc123") {
            head {
                koloStylesheetLink()
            }
            body {
                div {
                    kolo {
                        flex
                        inlineGrid
                        variant("hover").inlineFlex
                        variant("md").grid
                    }
                }
            }
        }

        val expected = """
            <html>
              <head>
                <link href="/css/generated/kolo.css?version=abc123&kolo=flex%3Bmd%3Agrid%3Binline-grid%3Bhover%3Ainline-flex" rel="stylesheet">
              </head>
              <body>
                <div class="k-flex k-inline-grid k-hover:inline-flex k-md:grid"></div>
              </body>
            </html>
        """.trimIndent() + "\n"
        html shouldBe expected
    }

    @Test
    fun `display dsl helper mapping emits exact tailwind token strings`() {
        val html = renderKoloHtml(version = "abc123") {
            head { koloStylesheetLink() }
            body {
                div {
                    kolo {
                        block
                        `inline`
                        inlineBlock
                        flowRoot
                        flex
                        inlineFlex
                        grid
                        inlineGrid
                        contents
                        listItem
                        hidden
                        table
                        inlineTable
                        tableCaption
                        tableCell
                        tableColumn
                        tableColumnGroup
                        tableHeaderGroup
                        tableRowGroup
                        tableRow
                        tableFooterGroup
                        variant("md").block
                        variant("md").`inline`
                        variant("md").inlineBlock
                        variant("md").flowRoot
                        variant("md").flex
                        variant("md").inlineFlex
                        variant("md").grid
                        variant("md").inlineGrid
                        variant("md").contents
                        variant("md").listItem
                        variant("md").hidden
                        variant("md").table
                        variant("md").inlineTable
                        variant("md").tableCaption
                        variant("md").tableCell
                        variant("md").tableColumn
                        variant("md").tableColumnGroup
                        variant("md").tableHeaderGroup
                        variant("md").tableRowGroup
                        variant("md").tableRow
                        variant("md").tableFooterGroup
                    }
                }
            }
        }

        val expectedTokens = listOf(
            "block", "md:block", "contents", "md:contents", "flex", "md:flex", "flow-root", "md:flow-root",
            "grid", "md:grid", "hidden", "md:hidden", "inline", "inline-block", "inline-flex", "inline-grid",
            "inline-table", "md:inline", "md:inline-block", "md:inline-flex", "md:inline-grid", "md:inline-table",
            "list-item", "md:list-item", "table", "table-caption", "table-cell", "table-column",
            "table-column-group", "table-footer-group", "table-header-group", "table-row", "table-row-group",
            "md:table", "md:table-caption", "md:table-cell", "md:table-column", "md:table-column-group",
            "md:table-footer-group", "md:table-header-group", "md:table-row", "md:table-row-group",
        )
        val canonizedTokens = canonicalizeKoloTokens(expectedTokens)
        val expectedHref = "/css/generated/kolo.css?version=abc123&kolo=" + URLEncoder.encode(canonizedTokens, UTF_8)

        extractHeadStylesheetHref(html) shouldBe expectedHref
        extractFirstBodyDivClasses(html) shouldContainExactlyInAnyOrder expectedTokens.map { "k-$it" }
    }

    @Test
    fun `renderKoloHtml emits canonicalized href and class names for font dsl tokens`() {
        val html = renderKoloHtml(version = "abc123") {
            head {
                koloStylesheetLink()
            }
            body {
                div {
                    kolo {
                        fontSans
                        text2xl
                        fontSemiBold
                        variant("hover").fontBlack
                        variant("md").textBase
                    }
                }
            }
        }

        val expected = """
            <html>
              <head>
                <link href="/css/generated/kolo.css?version=abc123&kolo=font-sans%3Bfont-semibold%3Bhover%3Afont-black%3Btext-2xl%3Bmd%3Atext-base" rel="stylesheet">
              </head>
              <body>
                <div class="k-font-sans k-text-2xl k-font-semibold k-hover:font-black k-md:text-base"></div>
              </body>
            </html>
        """.trimIndent() + "\n"
        html shouldBe expected
    }

    @Test
    fun `font dsl helper mapping emits exact tailwind token strings`() {
        val html = renderKoloHtml(version = "abc123") {
            head { koloStylesheetLink() }
            body {
                div {
                    kolo {
                        fontSans
                        fontSerif
                        fontMono
                        textXs
                        textSm
                        textBase
                        textLg
                        textXl
                        text2xl
                        text3xl
                        text4xl
                        text5xl
                        text6xl
                        text7xl
                        text8xl
                        text9xl
                        fontThin
                        fontExtraLight
                        fontLight
                        fontNormal
                        fontMedium
                        fontSemiBold
                        fontBold
                        fontExtraBold
                        fontBlack
                        variant("md").fontSans
                        variant("md").fontSerif
                        variant("md").fontMono
                        variant("md").textXs
                        variant("md").textSm
                        variant("md").textBase
                        variant("md").textLg
                        variant("md").textXl
                        variant("md").text2xl
                        variant("md").text3xl
                        variant("md").text4xl
                        variant("md").text5xl
                        variant("md").text6xl
                        variant("md").text7xl
                        variant("md").text8xl
                        variant("md").text9xl
                        variant("md").fontThin
                        variant("md").fontExtraLight
                        variant("md").fontLight
                        variant("md").fontNormal
                        variant("md").fontMedium
                        variant("md").fontSemiBold
                        variant("md").fontBold
                        variant("md").fontExtraBold
                        variant("md").fontBlack
                    }
                }
            }
        }

        val expectedTokens = listOf(
            "font-sans", "font-serif", "font-mono",
            "text-xs", "text-sm", "text-base", "text-lg", "text-xl", "text-2xl", "text-3xl", "text-4xl", "text-5xl", "text-6xl", "text-7xl", "text-8xl", "text-9xl",
            "font-thin", "font-extralight", "font-light", "font-normal", "font-medium", "font-semibold", "font-bold", "font-extrabold", "font-black",
            "md:font-sans", "md:font-serif", "md:font-mono",
            "md:text-xs", "md:text-sm", "md:text-base", "md:text-lg", "md:text-xl", "md:text-2xl", "md:text-3xl", "md:text-4xl", "md:text-5xl", "md:text-6xl", "md:text-7xl", "md:text-8xl", "md:text-9xl",
            "md:font-thin", "md:font-extralight", "md:font-light", "md:font-normal", "md:font-medium", "md:font-semibold", "md:font-bold", "md:font-extrabold", "md:font-black",
        )
        val canonizedTokens = canonicalizeKoloTokens(expectedTokens)
        val expectedHref = "/css/generated/kolo.css?version=abc123&kolo=" + URLEncoder.encode(canonizedTokens, UTF_8)

        extractHeadStylesheetHref(html) shouldBe expectedHref
        extractFirstBodyDivClasses(html) shouldContainExactlyInAnyOrder expectedTokens.map { "k-$it" }
    }

    @Test
    fun `renderKoloHtml emits canonicalized href and class names for sizing dsl tokens`() {
        val html = renderKoloHtml(version = "abc123") {
            head { koloStylesheetLink() }
            body {
                div {
                    kolo {
                        w(FULL)
                        h(SCREEN)
                        minW(0)
                        maxW(MD)
                        size(1, 2)
                        variant("md").maxH(DVH)
                    }
                }
            }
        }

        val expectedTokens = listOf("w-full", "h-screen", "min-w-0", "max-w-md", "size-1/2", "md:max-h-dvh")
        val canonizedTokens = canonicalizeKoloTokens(expectedTokens)
        val expectedHref = "/css/generated/kolo.css?version=abc123&kolo=" + URLEncoder.encode(canonizedTokens, UTF_8)

        extractHeadStylesheetHref(html) shouldBe expectedHref
        extractFirstBodyDivClasses(html) shouldContainExactlyInAnyOrder expectedTokens.map { "k-$it" }
    }

    @Test
    fun `renderKoloHtml emits canonicalized href and class names for layout dsl tokens`() {
        val html = renderKoloHtml(version = "abc123") {
            head { koloStylesheetLink() }
            body {
                div {
                    kolo {
                        sticky
                        top(18)
                        z(10)
                        overflowHidden
                        objectCover
                        boxBorder
                        variant("hover").overflowVisible
                        variant("md").insetX(4)
                    }
                }
            }
        }

        val expectedTokens = listOf(
            "sticky",
            "top-18",
            "z-10",
            "overflow-hidden",
            "object-cover",
            "box-border",
            "hover:overflow-visible",
            "md:inset-x-4",
        )
        val canonizedTokens = canonicalizeKoloTokens(expectedTokens)
        val expectedHref = "/css/generated/kolo.css?version=abc123&kolo=" + URLEncoder.encode(canonizedTokens, UTF_8)

        extractHeadStylesheetHref(html) shouldBe expectedHref
        extractFirstBodyDivClasses(html) shouldContainExactlyInAnyOrder expectedTokens.map { "k-$it" }
    }

    @Test
    fun `maximum-width variant helpers preserve canonical nested state tokens`() {
        val html = renderKoloHtml(version = "abc123") {
            head { koloStylesheetLink() }
            body {
                div {
                    kolo {
                        maxSm.p(4)
                        maxMd.variant("hover").flex
                        variant("max-md").variant("hover").grid
                    }
                }
            }
        }

        val expectedTokens = listOf("max-sm:p-4", "max-md:hover:flex", "max-md:hover:grid")
        val canonicalTokens = canonicalizeKoloTokens(expectedTokens)
        val expectedHref = "/css/generated/kolo.css?version=abc123&kolo=" + URLEncoder.encode(canonicalTokens, UTF_8)

        extractHeadStylesheetHref(html) shouldBe expectedHref
        extractFirstBodyDivClasses(html) shouldContainExactlyInAnyOrder expectedTokens.map { "k-$it" }
    }

    private fun extractHeadStylesheetHref(html: String): String {
        val document = Jsoup.parse(html)
        return document.selectFirst("head > link[rel=stylesheet]")
            .shouldNotBeNull()
            .attr("href")
    }

    private fun extractFirstBodyDivClasses(html: String): List<String> {
        val document = Jsoup.parse(html)
        return document.selectFirst("body > div")
            .shouldNotBeNull()
            .classNames()
            .toList()
    }
}
