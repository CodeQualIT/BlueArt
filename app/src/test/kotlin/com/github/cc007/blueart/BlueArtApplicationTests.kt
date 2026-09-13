package com.github.cc007.blueart

import io.github.cc007.kolostyles.compiler.KoloCssCompiler
import io.github.cc007.kolostyles.web.KoloCssController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import kotlin.test.assertNotNull

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class BlueArtApplicationTests(
    private val applicationContext: ApplicationContext,
) {

    @Test
    fun contextLoads() {
    }

    @Test
    fun `loads Kolo styles through auto-configuration`() {
        assertNotNull(applicationContext.getBean<KoloCssCompiler>())
        assertNotNull(applicationContext.getBean<KoloCssController>())
    }
}
