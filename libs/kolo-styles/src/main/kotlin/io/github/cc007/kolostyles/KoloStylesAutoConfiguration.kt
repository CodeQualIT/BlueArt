package io.github.cc007.kolostyles

import io.github.cc007.kolostyles.theme.EffectiveKoloTheme
import io.github.cc007.kolostyles.theme.KoloThemeComposer
import io.github.cc007.kolostyles.theme.ThemeContribution
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@AutoConfiguration
@ComponentScan(basePackageClasses = [KoloStylesAutoConfiguration::class])
class KoloStylesAutoConfiguration {
    @Bean
    fun effectiveKoloTheme(contributions: List<ThemeContribution>): EffectiveKoloTheme =
        KoloThemeComposer().compose(contributions)
}
