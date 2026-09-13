package io.github.cc007.kolostyles

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.ComponentScan

@AutoConfiguration
@ComponentScan(basePackageClasses = [KoloStylesAutoConfiguration::class])
class KoloStylesAutoConfiguration
