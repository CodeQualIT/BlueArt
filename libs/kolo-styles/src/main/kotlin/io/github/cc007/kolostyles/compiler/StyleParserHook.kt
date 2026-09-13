package io.github.cc007.kolostyles.compiler

/**
 * Parses one style token into a typed token when supported.
 */
fun interface StyleParserHook {
    fun parse(token: String): Token?
}
