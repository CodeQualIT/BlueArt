package com.github.cc007.blueart.endpoints.styling

import io.kotest.assertions.fail
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlin.test.Test

class CssControllerTest {

    private val controller = CssController()

    @Test
    fun `browse stylesheet encodes all browse css rules in generated output`() {
        val css = controller.browseStylesheet()
        val legacyCss = readResource("/static/css/browse.css")

        css shouldNotContain "@import"
        assertContainsAllRuleHeaders(
            legacyCss,
            css,
            ignoredLegacyHeaders = setOf(".browse-content"),
        )
    }

    @Test
    fun `art stylesheet encodes all art css rules in generated output`() {
        val css = controller.artStylesheet()
        val legacyCss = readResource("/static/css/art.css")

        css shouldNotContain "@import"
        assertContainsAllRuleHeaders(legacyCss, css)
    }

    @Test
    fun `browse stylesheet omits display declarations for migrated selectors`() {
        val css = controller.browseStylesheet()
        listOf(
            "body.browse-body",
            ".top-banner",
            ".browse-layout",
            ".sidebar-nav",
            ".content-top",
            ".filter-row",
            ".feed-grid",
            ".post-card",
            ".post-author",
            ".author-meta",
            ".author-name",
            ".embed-media",
            ".embed-media-grid",
            ".embed-media-grid-side",
            ".embed-blur-clip",
            ".post-stats",
            ".post-stat-item",
            ".post-stat-icon",
        ).forEach { selector -> assertSelectorHasNoDisplay(css, selector) }
    }

    @Test
    fun `art stylesheet omits display declarations for migrated selectors`() {
        val css = controller.artStylesheet()
        listOf(
            ".art-content",
            ".content-top",
            ".art-image-grid",
            ".art-image",
            ".comments",
            ".comment",
            ".comment-header",
            ".comment-meta",
        ).forEach { selector -> assertSelectorHasNoDisplay(css, selector) }
    }

    @Test
    fun `browse stylesheet omits migrated font declarations`() {
        val css = controller.browseStylesheet()
        listOf(
            "body.browse-body",
            ".richtext-mention",
            ".richtext-tag",
        ).forEach { selector -> assertSelectorHasNoFontDeclarations(css, selector) }
    }

    @Test
    fun `art stylesheet omits migrated font declarations`() {
        val css = controller.artStylesheet()
        listOf(
            "body.art-body",
            ".art-title",
            ".art-description h2, .comments h2",
            ".richtext-mention",
            ".richtext-tag",
        ).forEach { selector -> assertSelectorHasNoFontDeclarations(css, selector) }
    }

    @Test
    fun `stylesheets retain non migrated typography declarations as explicit exceptions`() {
        val browseCss = controller.browseStylesheet()
        val artCss = controller.artStylesheet()

        assertSelectorContainsDeclaration(browseCss, ".brand h1", "font-size:")
        assertSelectorContainsDeclaration(browseCss, ".content-top h1", "font-size:")
        assertSelectorContainsDeclaration(artCss, ".art-byline", "font-size:")
        assertSelectorContainsDeclaration(artCss, ".comment-author", "font-size:")
    }

    @Test
    fun `browse stylesheet omits sizing declarations for migrated selectors`() {
        val css = controller.browseStylesheet()
        listOf(
            "body.browse-body",
            ".browse-layout",
            ".browse-content",
            ".post-card",
            ".parent-post .post-card",
            ".post-content",
            ".embed-media",
            ".embed-blur-clip",
            ".embed-media-grid",
            ".post-card-media .embed-media-single",
            ".post-card-media .embed-blur-clip",
        ).forEach { selector -> assertSelectorHasNoSizingDeclarations(css, selector) }
    }

    @Test
    fun `art stylesheet omits sizing declarations for migrated selectors`() {
        val css = controller.artStylesheet()
        listOf(
            "body.art-body",
            ".art-layout",
            ".art-image",
        ).forEach { selector -> assertSelectorHasNoSizingDeclarations(css, selector) }
    }

    @Test
    fun `stylesheets retain non migrated sizing declarations as explicit exceptions`() {
        val browseCss = controller.browseStylesheet()
        assertSelectorContainsDeclaration(
            browseCss,
            ".post-stat-icon",
            "height:"
        )
    }

    @Test
    fun `browse stylesheet omits migrated layout declarations`() {
        val css = controller.browseStylesheet()
        listOf(
            "body.browse-body",
            ".top-banner",
            ".browse-content",
            ".post-card",
            ".post-content",
            ".embed-media",
            ".embed-blur-clip",
            ".parent-post .post-card",
        ).forEach { selector -> assertSelectorHasNoLayoutDeclarations(css, selector) }
    }

    @Test
    fun `art stylesheet omits migrated layout declarations`() {
        val css = controller.artStylesheet()
        listOf(
            ".top-banner",
            ".art-image",
        ).forEach { selector -> assertSelectorHasNoLayoutDeclarations(css, selector) }
    }

    @Test
    fun `stylesheets retain non migrated layout exceptions`() {
        val browseCss = controller.browseStylesheet()
        val artCss = controller.artStylesheet()
        val browseMerged = findSelectorDeclarations(browseCss, ".browse-sidebar", allowMissing = true).joinToString("\n")
        val browseUniversal = findSelectorDeclarations(browseCss, "*", allowMissing = true).joinToString("\n")
        val artUniversal = findSelectorDeclarations(artCss, "*", allowMissing = true).joinToString("\n")
        val clampedText = findSelectorDeclarations(browseCss, ".post-card-text-only .post-text", allowMissing = true).joinToString("\n")

        browseMerged shouldContain "position: static;"
        browseUniversal shouldContain "box-sizing: border-box;"
        artUniversal shouldContain "box-sizing: border-box;"
        clampedText shouldContain "overflow: hidden;"
    }

    private fun assertContainsAllRuleHeaders(
        legacyCss: String,
        generatedCss: String,
        ignoredLegacyHeaders: Set<String> = emptySet(),
    ) {
        val legacyHeaders = extractRuleHeaders(legacyCss)
        val generatedHeaders = extractRuleHeaders(generatedCss)
        val missingHeaders = (legacyHeaders - ignoredLegacyHeaders) - generatedHeaders
        missingHeaders.shouldBeEmpty()
    }

    private fun readResource(path: String): String {
        return javaClass.getResource(path)?.readText()
            ?: fail("Missing test resource: $path")
    }

    private fun extractRuleHeaders(css: String): Set<String> {
        val headers = mutableSetOf<String>()
        var segmentStart = 0
        var index = 0

        while (index < css.length) {
            when (css[index]) {
                '{' -> {
                    val header = css.substring(segmentStart, index)
                        .trim()
                        .replace(Regex("\\s+"), " ")
                    if (header.isNotEmpty()) {
                        headers.add(header)
                    }
                    segmentStart = index + 1
                }

                '}' -> {
                    segmentStart = index + 1
                }
            }
            index += 1
        }

        return headers
    }

    private fun assertSelectorHasNoDisplay(css: String, selector: String) {
        val declarations = findSelectorDeclarations(css, selector)
        declarations.joinToString("\n") shouldNotContain "display:"
    }

    private fun assertSelectorHasNoFontDeclarations(css: String, selector: String) {
        val declarations = findSelectorDeclarations(css, selector, allowMissing = true).joinToString("\n")
        if (declarations.isEmpty()) {
            return
        }
        declarations shouldNotContain "font-family:"
        declarations shouldNotContain "font-size:"
        declarations shouldNotContain "font-weight:"
    }

    private fun assertSelectorHasNoSizingDeclarations(css: String, selector: String) {
        val declarations = findSelectorDeclarations(css, selector, allowMissing = true).joinToString("\n")
        if (declarations.isEmpty()) {
            return
        }
        declarations shouldNotContain "width:"
        declarations shouldNotContain "height:"
        declarations shouldNotContain "min-width:"
        declarations shouldNotContain "max-width:"
        declarations shouldNotContain "min-height:"
        declarations shouldNotContain "max-height:"
    }

    private fun assertSelectorContainsDeclaration(css: String, selector: String, declarationPrefix: String) {
        val declarations = findSelectorDeclarations(css, selector).joinToString("\n")
        declarations shouldContain declarationPrefix
    }

    private fun assertSelectorHasNoLayoutDeclarations(css: String, selector: String) {
        val declarations = findSelectorDeclarations(css, selector, allowMissing = true).joinToString("\n")
        if (declarations.isEmpty()) {
            return
        }
        val forbidden = setOf(
            "box-sizing",
            "overflow",
            "overflow-x",
            "overflow-y",
            "position",
            "top",
            "right",
            "bottom",
            "left",
            "z-index",
            "object-fit",
        )
        val declaredProperties = declarations
            .lineSequence()
            .map { it.trim() }
            .filter { it.contains(':') }
            .map { it.substringBefore(':').trim() }
            .toSet()
        (declaredProperties intersect forbidden).shouldBeEmpty()
    }

    private fun findSelectorDeclarations(css: String, selector: String, allowMissing: Boolean = false): List<String> {
        val blocks = Regex("([^{}]+)\\{([^}]*)\\}")
            .findAll(css)
            .mapNotNull { match ->
                val selectors = match.groupValues[1]
                    .split(",")
                    .map { it.trim() }
                if (selector in selectors) {
                    match.groupValues[2]
                } else {
                    null
                }
            }
            .toList()

        if (!allowMissing) {
            blocks.shouldNotBeEmpty()
        }
        return blocks
    }
}
