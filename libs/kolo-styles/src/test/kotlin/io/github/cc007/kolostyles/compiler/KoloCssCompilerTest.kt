package io.github.cc007.kolostyles.compiler

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class KoloCssCompilerTest {

    @Test
    fun `compile parses and generates css through hooks`() {
        val parserHook = StyleParserHook { token ->
            TestToken(token)
        }
        val generatorHook = StyleGeneratorHook { token, builder ->
            val parsedToken = token as? TestToken ?: return@StyleGeneratorHook false
            builder.apply {
                ".${parsedToken.raw.replace(":", "\\:")}" {
                    put("display", "block")
                }
            }
            true
        }
        val compiler = KoloCssCompiler(
            parserHooks = listOf(parserHook),
            generatorHooks = listOf(generatorHook)
        )

        val result = compiler.compile("flex;mt-2;hover:mt-2;md:mt-2")

        result shouldBe
            """
            .flex {
            display: block;
            }
            .mt-2 {
            display: block;
            }
            .hover\:mt-2 {
            display: block;
            }
            .md\:mt-2 {
            display: block;
            }
            
            """.trimIndent()
    }

    @Test
    fun `compile preserves token order from input`() {
        val tokens = mutableListOf<String>()
        val recordingParser = StyleParserHook { token ->
            tokens.add(token)
            null
        }
        val noopGenerator = StyleGeneratorHook { _, _ -> false }
        val compiler = KoloCssCompiler(
            parserHooks = listOf(recordingParser),
            generatorHooks = listOf(noopGenerator)
        )

        compiler.compile("md:mt-2;mt-2;flex;mt-2;hover:mt-2")

        // Should preserve order and include duplicates (deduping is client-side)
        tokens shouldContainExactly listOf("md:mt-2", "mt-2", "flex", "mt-2", "hover:mt-2")
    }

    @Test
    fun `compile notes arbitrary value tokens as unparsed comments`() {
        val compiler = KoloCssCompiler()

        val result = compiler.compile("mt-[4px]")

        result shouldBe
            """
            :root {
            --kolo-unparsed-0: "mt-[4px]";
            }
            
            """.trimIndent()
    }

    @Test
    fun `compile notes malformed tokens with whitespace as comments`() {
        val compiler = KoloCssCompiler()

        val result = compiler.compile("mt- 2")

        result shouldBe
            """
            :root {
            --kolo-unparsed-0: "mt- 2";
            }
            
            """.trimIndent()
    }

    @Test
    fun `compile produces empty css for empty input`() {
        val compiler = KoloCssCompiler()

        val result = compiler.compile(" ; ; ")

        result shouldBe ""
    }

    @Test
    fun `compile with no supporting hooks marks tokens unsupported`() {
        val compiler = KoloCssCompiler()

        val result = compiler.compile("flex;mt-2")

        result shouldBe
            """
            :root {
            --kolo-unsupported-0: "flex";
            --kolo-unsupported-1: "mt-2";
            }
            
            """.trimIndent()
    }

    @Test
    fun `compile uses parser and generator hooks when token is supported via hooks`() {
        val parserHook = StyleParserHook { token ->
            if (token == "x-hook") {
                TestToken(token)
            } else {
                null
            }
        }
        
        val generatorHook = StyleGeneratorHook { token, builder ->
            val parsedToken = token as? TestToken ?: return@StyleGeneratorHook false
            if (parsedToken.raw != "x-hook") {
                return@StyleGeneratorHook false
            }
            builder.apply {
                ".x-hook" {
                    put("display", "block")
                }
            }
            true
        }

        val compiler = KoloCssCompiler(
            parserHooks = listOf(parserHook),
            generatorHooks = listOf(generatorHook)
        )

        val result = compiler.compile("x-hook")

        result shouldBe
            """
            .x-hook {
            display: block;
            }
            
            """.trimIndent()
    }

    @Test
    fun `compile marks token unsupported when hooks do not support token`() {
        val parserHook = StyleParserHook { null }
        val generatorHook = StyleGeneratorHook { _, _ -> false }
        val compiler = KoloCssCompiler(
            parserHooks = listOf(parserHook),
            generatorHooks = listOf(generatorHook)
        )

        val result = compiler.compile("mt-2")

        result shouldBe
            """
            :root {
            --kolo-unsupported-0: "mt-2";
            }
            
            """.trimIndent()
    }

    @Test
    fun `compile escapes comment terminators inside unsupported tokens`() {
        val compiler = KoloCssCompiler()

        val result = compiler.compile("broken*/token")

        result shouldBe
            """
            :root {
            --kolo-unsupported-0: "broken*/token";
            }
            
            """.trimIndent()
    }

    private data class TestToken(
        override val raw: String
    ) : Token
}
