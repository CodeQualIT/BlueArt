package com.github.cc007.blueart.endpoints.styling

import kotlinx.css.*
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class CssController {

    @GetMapping("/css/generated/browse.css", produces = ["text/css"])
    @ResponseBody
    fun browseStylesheet(): String = CssBuilder().apply { buildBrowseStyles() }.toString()

    @GetMapping("/css/generated/art.css", produces = ["text/css"])
    @ResponseBody
    fun artStylesheet(): String = CssBuilder().apply { buildArtStyles() }.toString()


    private fun CssBuilder.buildBrowseStyles() {
        root {
            varDef("bg", "#c2dff5")
            varDef("surface", "#d4eaf9")
            varDef("surface-2", "#dff1fe")
            varDef("author-surface", "#eaf6ff")
            varDef("border", "#93b9d9")
            varDef("text", "#0e2333")
            varDef("muted", "#3d6680")
            varDef("banner", "#0a5fa8")
            varDef("primary", "#0f85fe")
            varDef("primary-2", "#3ca0ff")
            varDef("accent", "#0a62a8")
            varDef("hashtag", "#085898")
            defineFontVariables()
        }

        universal {
            // kolo-exception: global preflight-style selector cannot be represented as element-local utilities
            boxSizing = BoxSizing.borderBox
        }

        "body.browse-body" {
            flexDirection = FlexDirection.column
            background = "radial-gradient(circle at top, #d8effe 0%, ${cssVar("bg")} 48%)"
            color = cssColorVar("text")
        }

        a {
            color = Color.inherit
            raw("text-decoration", "none")
        }

        ".top-banner" {
            justifyContent = JustifyContent.spaceBetween
            alignItems = Align.center
            raw("border-bottom", "1px solid #1a6eb5")
            background = "linear-gradient(90deg, #0a5fa8 0%, #1478cc 50%, #0e6ec0 100%)"
            backdropFilter = "blur(10px)"
        }
        ".top-banner, .top-banner .brand h1" {
            color = Color("#f2f8ff")
        }
        ".brand h1" {
            fontSize = 1.15.rem
            letterSpacing = 0.04.em
        }
        ".logout-button" {
            raw("border", "1px solid #9bcfff")
            borderRadius = 999.px
            background = "linear-gradient(135deg, #0f73e0, ${cssVar("primary")})"
            color = Color("#eef6ff")
            cursor = Cursor.pointer
        }
        ".browse-layout" {
            raw("grid-template-columns", "245px minmax(0, 1fr)")
            gap = 1.rem
            raw("flex", "1")
        }
        ".browse-sidebar" {
            raw("align-self", "start")
            raw("border", "1px solid ${cssVar("border")}")
            borderRadius = 12.px
            background = "linear-gradient(180deg, #dbe8f4, #d3e2f0)"
        }
        ".sidebar-title" {
            fontSize = 0.9.rem
            color = cssColorVar("accent")
            textTransform = TextTransform.uppercase
            letterSpacing = 0.08.em
        }
        ".sidebar-nav" {
            gap = 0.35.rem
        }
        ".sidebar-nav a" {
            borderRadius = 8.px
            color = cssColorVar("muted")
        }
        ".sidebar-nav a:hover, .sidebar-nav a:focus-visible" {
            raw("outline", "none")
            color = cssColorVar("text")
            background = "rgba(17, 133, 254, 0.14)"
        }
        ".content-top" {
            justifyContent = JustifyContent.spaceBetween
            gap = 1.rem
            alignItems = Align.center
        }
        ".content-top h1" {
            fontSize = 1.35.rem
        }
        ".filter-row" {
            gap = 0.4.rem
            flexWrap = FlexWrap.wrap
        }
        ".filter-chip" {
            raw("border", "1px solid ${cssVar("border")}")
            background = "#e5eef6"
            color = cssColorVar("muted")
            borderRadius = 999.px
            cursor = Cursor.pointer
        }
        ".filter-chip-active, .filter-chip:hover, .filter-chip:focus-visible" {
            raw("outline", "none")
            color = Color("#f4f9ff")
            raw("border-color", cssVar("primary-2"))
            background = "rgba(17, 133, 254, 0.16)"
        }
        ".feed-grid" {
            raw("grid-template-columns", "repeat(auto-fill, minmax(300px, 1fr))")
            gap = 0.8.rem
            raw("align-items", "stretch")
        }
        ".post-card" {
            raw("border", "1px solid ${cssVar("border")}")
            borderRadius = 12.px
            background = "linear-gradient(180deg, ${cssVar("surface-2")}, ${cssVar("surface")})"
            raw("box-shadow", "0 8px 20px rgba(32, 58, 83, 0.12)")
            flexDirection = FlexDirection.column
        }
        ".post-author" {
            alignItems = Align.center
            gap = 0.55.rem
            background = cssVar("author-surface")
            raw("border", "1px solid #d2e0eb")
            borderRadius = 10.px
        }
        ".author-avatar" {
            raw("border-radius", "50%")
            raw("border", "1px solid #89a9c4")
        }
        ".author-meta" {
            gap = 0.1.rem
        }
        ".author-name" {
            fontSize = 0.93.rem
        }
        ".author-handle" {
            color = cssColorVar("muted")
            fontSize = 0.8.rem
        }
        ".post-content" {
            raw("flex", "1")
        }
        ".post-text" {
            raw("line-height", "1.4")
            raw("overflow-wrap", "anywhere")
            raw("word-break", "break-word")
        }
        ".post-card-text-only .post-text" {
            // kolo-exception: browser-specific line-clamp pattern not represented by current utility model
            raw("display", "-webkit-box")
            raw("-webkit-box-orient", "vertical")
            raw("-webkit-line-clamp", "11")
            overflow = Overflow.hidden
        }
        ".richtext-link, .richtext-mention, .richtext-tag" {
            raw("text-decoration", "underline")
            raw("text-underline-offset", "2px")
        }
        ".richtext-link, .richtext-mention" {
            color = cssColorVar("accent")
        }
        ".richtext-tag" {
            color = cssColorVar("hashtag")
        }
        ".richtext-link:hover, .richtext-link:focus-visible, .richtext-mention:hover, .richtext-mention:focus-visible, .richtext-tag:hover, .richtext-tag:focus-visible" {
            opacity = 0.85
        }
        ".embed-media" {
            borderRadius = 8.px
            raw("border", "1px solid #a6bfd2")
        }
        ".embed-media-grid" {
            raw("grid-template-columns", "2fr 1fr")
            gap = 0.35.rem
        }
        ".embed-media-grid-side" {
            gap = 0.35.rem
            raw("grid-template-rows", "repeat(3, minmax(0, 1fr))")
        }
        ".embed-blur-clip" {
            borderRadius = 8.px
        }
        ".embed-media-blur" {
            filter = "blur(18px)"
            raw("transition", "filter 0.25s ease")
        }
        ".embed-blur-clip:hover .embed-media-blur" {
            filter = "blur(0)"
        }
        ".parent-post .post-card" {
            background = "#eaf3fa"
        }
        ".post-stats" {
            alignItems = Align.center
            gap = 0.85.rem
            flexWrap = FlexWrap.wrap
            color = cssColorVar("muted")
            fontSize = 0.78.rem
            raw("border-top", "1px solid rgba(90, 115, 137, 0.26)")
            raw("flex-shrink", "0")
        }
        ".post-open-link" {
            fontSize = 0.8.rem
            color = cssColorVar("accent")
        }
        ".post-open-link a" {
            raw("text-decoration", "underline")
            raw("text-underline-offset", "2px")
        }
        ".post-stat-item" {
            alignItems = Align.center
            gap = 0.28.rem
            raw("line-height", "1")
        }
        ".post-stat-icon" {
            // kolo-exception: arbitrary value
            height = 0.875.rem
        }
        ".post-stat-icon path" {
            raw("fill", "none")
            raw("stroke", "currentColor")
            raw("stroke-width", "2")
            raw("stroke-linecap", "round")
            raw("stroke-linejoin", "round")
        }
        ".post-stat-icon-like svg" {
            raw("transform", "translateY(0.5px)")
        }
        ".post-stat-icon-quote svg" {
            raw("transform", "translateY(1px)")
        }
        ".post-stat-icon-quote path" {
            raw("fill", "currentColor")
            raw("stroke", "none")
        }
        ".post-stat-icon-repost svg" {
            raw("transform", "translateY(1px)")
        }
        ".post-stat-icon-reply svg" {
            raw("transform", "translateY(1px)")
        }
        ".post-stat-icon-bookmark svg" {
            raw("transform", "translateY(1px)")
        }

        media("(max-width: 960px)") {
            ".browse-layout" {
                raw("grid-template-columns", "1fr")
            }
            ".browse-sidebar" {
                // kolo-exception: max-width variant not representable in current min-width-only utility variants
                position = Position.static
            }
        }
        media("(max-width: 640px)") {
            ".content-top" {
                flexDirection = FlexDirection.column
                raw("align-items", "flex-start")
            }
        }
    }

    private fun CssBuilder.buildArtStyles() {
        root {
            varDef("bg", "#c2dff5")
            varDef("surface", "#d4eaf9")
            varDef("surface-2", "#dff1fe")
            varDef("border", "#93b9d9")
            varDef("text", "#0e2333")
            varDef("muted", "#3d6680")
            varDef("accent", "#0a62a8")
            varDef("hashtag", "#085898")
            defineFontVariables()
        }

        universal {
            // kolo-exception: global preflight-style selector cannot be represented as element-local utilities
            boxSizing = BoxSizing.borderBox
        }

        ".top-banner" {
            justifyContent = JustifyContent.spaceBetween
            alignItems = Align.center
            raw("border-bottom", "1px solid #1a6eb5")
            background = "linear-gradient(90deg, #0a5fa8 0%, #1478cc 50%, #0e6ec0 100%)"
            backdropFilter = "blur(10px)"
        }
        ".top-banner, .top-banner .brand h1" {
            color = Color("#f2f8ff")
        }

        "body.art-body" {
            background = "radial-gradient(circle at top, #d8effe 0%, ${cssVar("bg")} 48%)"
            color = cssColorVar("text")
        }

        ".art-content" {
            gap = 1.rem
        }
        ".content-top" {
            gap = 0.2.rem
        }
        ".art-byline" {
            color = cssColorVar("muted")
            fontSize = 0.9.rem
        }
        ".art-card, .comments" {
            raw("border", "1px solid ${cssVar("border")}")
            borderRadius = 12.px
            background = "linear-gradient(180deg, ${cssVar("surface-2")}, ${cssVar("surface")})"
            raw("box-shadow", "0 8px 20px rgba(32, 58, 83, 0.12)")
        }
        ".art-embed" {
            borderRadius = 10.px
            background = "#cfe4f4"
            raw("border", "1px solid #b9d3e7")
        }
        ".art-image-grid" {
            raw("grid-template-columns", "repeat(auto-fit, minmax(250px, 1fr))")
            gap = 0.5.rem
        }
        ".art-image" {
            borderRadius = 8.px
            background = "#d7e8f6"
        }
        ".art-description h2, .comments h2" {
            color = cssColorVar("accent")
        }
        ".art-text, .comment-text, .art-empty, .art-external" {
            raw("line-height", "1.45")
            raw("overflow-wrap", "anywhere")
            raw("word-break", "break-word")
        }
        ".richtext-link, .richtext-mention, .richtext-tag" {
            raw("text-decoration", "underline")
            raw("text-underline-offset", "2px")
        }
        ".richtext-link, .richtext-mention" {
            color = cssColorVar("accent")
        }
        ".richtext-tag" {
            color = cssColorVar("hashtag")
        }
        ".richtext-link:hover, .richtext-link:focus-visible, .richtext-mention:hover, .richtext-mention:focus-visible, .richtext-tag:hover, .richtext-tag:focus-visible" {
            opacity = 0.85
        }
        ".comments" {
            gap = 0.7.rem
        }
        ".comment" {
            raw("border", "1px solid #bcd4e5")
            borderRadius = 10.px
            background = "#e8f3fc"
            gap = 0.45.rem
        }
        ".comment-header" {
            alignItems = Align.center
            gap = 0.45.rem
        }
        ".comment-avatar" {
            raw("border-radius", "50%")
            raw("border", "1px solid #89a9c4")
        }
        ".comment-meta" {
            raw("line-height", "1.15")
        }
        ".comment-author" {
            fontSize = 0.88.rem
        }
        ".comment-handle" {
            color = cssColorVar("muted")
            fontSize = 0.76.rem
        }

        media("(max-width: 700px)") {
            ".art-layout" {
                padding = Padding(0.75.rem)
            }
            ".art-card, .comments" {
                padding = Padding(0.8.rem)
            }
        }
    }

    private fun CssBuilder.varDef(name: String, value: String) {
        put("--$name", value)
    }

    private fun CssBuilder.defineFontVariables() {
        /* Todo: set default values to TailwindCSS defaults, but set font-serif to "Inter, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif" for BlueArt specifically */
        varDef("font-sans", "Inter, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif")
        varDef("font-serif", "ui-serif, Georgia, Cambria, \"Times New Roman\", Times, serif")
        varDef("font-mono", "ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, \"Liberation Mono\", \"Courier New\", monospace")
    }

    private fun CssBuilder.raw(name: String, value: String) {
        put(name, value)
    }

    private fun cssVar(name: String): String = "var(--$name)"

    private fun cssColorVar(name: String): Color = Color(cssVar(name))
}
