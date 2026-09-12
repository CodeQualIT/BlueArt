package com.github.cc007.blueart.kolostyles.dsl

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val KOLO_HREF_PLACEHOLDER = "__blueart_kolo_href__"

private data class KoloRenderContext(
    val version: String,
    val classNameMapper: (String) -> String?,
    val tokens: MutableList<String> = mutableListOf(),
)

private object KoloRenderContextHolder {
    private val current = ThreadLocal<KoloRenderContext?>()

    fun <T> withContext(context: KoloRenderContext, block: () -> T): T {
        val previous = current.get()
        current.set(context)
        return try {
            block()
        } finally {
            if (previous == null) {
                current.remove()
            } else {
                current.set(previous)
            }
        }
    }

    fun current(): KoloRenderContext? = current.get()
}

fun renderKoloHtml(
    version: String = defaultKoloVersion(),
    classNameMapper: (String) -> String? = { token -> "k-${token}" },
    block: HTML.() -> Unit,
): String {
    val context = KoloRenderContext(
        version = version,
        classNameMapper = classNameMapper,
    )
    return KoloRenderContextHolder.withContext(context) {
        val html = createHTML().html(block = block)
        html.replace(KOLO_HREF_PLACEHOLDER, buildKoloHref(context))
    }
}

fun HEAD.koloStylesheetLink() {
    val context = KoloRenderContextHolder.current()
    val href = context?.let { KOLO_HREF_PLACEHOLDER } ?: buildKoloHref(
        KoloRenderContext(
            version = defaultKoloVersion(),
            classNameMapper = { null },
        )
    )

    link(rel = "stylesheet", href = href)
}

fun HTMLTag.kolo(block: KoloScope.() -> Unit) {
    val context = KoloRenderContextHolder.current() ?: return
    val emitted = mutableListOf<String>()
    KoloScope(
        sink = { token ->
            emitted += token
            context.tokens += token
        }
    ).block()

    emitted
        .mapNotNull(context.classNameMapper)
        .distinct()
        .forEach { appendClass(it) }
}

class KoloScope internal constructor(
    private val sink: (String) -> Unit,
) {
    internal fun variant(name: String): KoloVariantScope = KoloVariantScope(sink, listOf(name))

    internal fun recordBase(token: String) {
        sink(token)
    }
}

class KoloVariantScope internal constructor(
    private val sink: (String) -> Unit,
    private val variants: List<String>,
) {
    internal fun variant(name: String): KoloVariantScope = KoloVariantScope(sink, variants + name)

    internal fun recordBase(token: String) {
        sink(variants.joinToString(separator = ":", postfix = ":") + token)
    }
}

fun canonicalizeKoloTokens(tokens: Iterable<String>): String {
    val canonical = tokens
        .flatMap { raw -> raw.split(';') }
        .map { part -> part.trim() }
        .filter { part -> part.isNotEmpty() }
        .filter { part -> isCanonicalizable(part) }
        .distinct()
        .sortedWith(
            compareBy(
                { tokenGroup(it) },
                { tokenVariantCount(it) },
                { tokenVariantChain(it) },
                { tokenBaseUtility(it) },
                { it },
            )
        )

    return canonical.joinToString(separator = ";")
}

private fun buildKoloHref(context: KoloRenderContext): String {
    val canonical = canonicalizeKoloTokens(context.tokens)
    return "/css/generated/kolo.css?version=${encodeQueryParam(context.version)}&kolo=${encodeQueryParam(canonical)}"
}

private fun defaultKoloVersion(): String {
    return System.getProperty("blueart.build.sha")
        ?: System.getenv("BLUEART_BUILD_SHA")
        ?: "dev"
}

private fun encodeQueryParam(value: String): String {
    return URLEncoder.encode(value, StandardCharsets.UTF_8)
}

private fun isCanonicalizable(token: String): Boolean {
    if (token.contains(';') || token.contains('[') || token.contains(']')) {
        return false
    }
    if (token.any { it.isWhitespace() }) {
        return false
    }
    return token.split(':').none { it.isBlank() }
}

private fun tokenVariantCount(token: String): Int = token.split(':').size - 1

private fun tokenVariantChain(token: String): String {
    val parts = token.split(':')
    return if (parts.size <= 1) "" else parts.dropLast(1).joinToString(":")
}

private fun tokenBaseUtility(token: String): String = token.substringAfterLast(':')

private fun tokenGroup(token: String): String {
    val base = tokenBaseUtility(token)
    return base.substringBefore('-', base)
}

private fun HTMLTag.appendClass(className: String) {
    val existing = attributes["class"].orEmpty().trim()
    attributes["class"] = if (existing.isEmpty()) {
        className
    } else {
        "$existing $className"
    }
}



