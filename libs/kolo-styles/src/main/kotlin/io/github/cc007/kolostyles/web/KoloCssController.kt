package io.github.cc007.kolostyles.web

import io.github.cc007.kolostyles.compiler.KoloCssCompiler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody


val logger = KotlinLogging.logger {}
@Controller
class KoloCssController(
    private val koloCssCompiler: KoloCssCompiler
) {

    @GetMapping("/css/generated/kolo.css", produces = ["text/css"])
    @ResponseBody
    fun koloStylesheet(
        @RequestParam(required = false) version: String?,
        @RequestParam(required = false) kolo: String?
    ): ResponseEntity<String> {
        version?.length // kept for endpoint contract compatibility (`version` participates in URL cache keys)
        val css = koloCssCompiler.compile(kolo)
        logger.info { "KoloStylesheet: $css" }
        return ResponseEntity
            .ok()
            .contentType(MediaType.valueOf("text/css"))
            .body(css)
    }
}

