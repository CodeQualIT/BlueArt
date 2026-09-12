package com.github.cc007.blueart.kolostyles.compiler

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.css.CssBuilder
import org.springframework.stereotype.Service

val logger = KotlinLogging.logger {}
/**
 * Compiles canonicalized kolo token lists into deterministic CSS output.
 *
 * Expects tokens to be pre-validated and passed as-is (in the order provided by client).
 * Deduping and sorting happens client-side (BA-022) for link URL caching.
 * Server-side generation just converts the token list into CSS rules.
 *
 * Utility-to-CSS mapping is pluggable via parser/generator hooks.
 */
@Service
class KoloCssCompiler(
    private val parserHooks: List<StyleParserHook> = emptyList(),
    private val generatorHooks: List<StyleGeneratorHook> = emptyList()
) {
    init {
        logger.info { "Parser hooks loaded: ${parserHooks.map { it::class.simpleName }}" }
        logger.info { "Generator hooks loaded: ${generatorHooks.map { it::class.simpleName }}" }
    }

    fun compile(rawKolo: String?): String {
        val tokens = splitTokens(rawKolo)
        val builder = CssBuilder()
        tokens.forEachIndexed { index, token ->
            if (isMalformedToken(token)) {
                builder.appendDiagnostic("unparsed", index, token)
            } else if (!generateViaHooks(token, builder)) {
                builder.appendDiagnostic("unsupported", index, token)
            }
        }
        return builder.toString()
    }

    private fun generateViaHooks(token: String, builder: CssBuilder): Boolean {
        val parsed = parserHooks.firstNotNullOfOrNull { hook -> hook.parse(token) } ?: return false
        return generatorHooks.any { hook -> hook.generate(parsed, builder) }
    }

    private fun splitTokens(rawKolo: String?): List<String> {
        return rawKolo
            .orEmpty()
            .split(';')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun isMalformedToken(token: String): Boolean {
        if (token.contains('[') || token.contains(']') || token.contains(';') || token.any { it.isWhitespace() }) {
            return true
        }

        val parts = token.split(':')
        return parts.any { it.isBlank() }
    }

    private fun CssBuilder.appendDiagnostic(kind: String, index: Int, token: String) {
        root {
            put("--kolo-$kind-$index", token.toCssQuotedString())
        }
    }

    private fun String.toCssQuotedString(): String {
        val escaped = buildString(length + 2) {
            for (char in this@toCssQuotedString) {
                when (char) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\A ")
                    '\r' -> append("\\D ")
                    else -> append(char)
                }
            }
        }
        return "\"$escaped\""
    }
}
