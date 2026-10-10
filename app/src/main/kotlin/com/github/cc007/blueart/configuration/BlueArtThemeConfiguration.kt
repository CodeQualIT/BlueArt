package com.github.cc007.blueart.configuration

import io.github.cc007.kolostyles.theme.ThemeContribution
import io.github.cc007.kolostyles.theme.ThemeEntry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BlueArtThemeConfiguration {
    @Bean
    fun blueArtThemeContribution() = ThemeContribution(
        id = "blueart",
        precedence = 100,
        entries = listOf(
            ThemeEntry("--k-color-blueart-page-background", "#c2dff5"),
            ThemeEntry("--k-color-blueart-surface", "#d4eaf9"),
            ThemeEntry("--k-color-blueart-surface-2", "#dff1fe"),
            ThemeEntry("--k-color-blueart-author-surface", "#eaf6ff"),
            ThemeEntry("--k-color-blueart-border", "#93b9d9"),
            ThemeEntry("--k-color-blueart-primary", "#0f85fe"),
            ThemeEntry("--k-color-blueart-action", "#0f85fe"),
            ThemeEntry("--k-color-blueart-accent", "#0a62a8"),
            ThemeEntry("--k-color-blueart-banner", "#0a5fa8"),
            ThemeEntry("--k-color-blueart-body-text", "#0e2333"),
            ThemeEntry("--k-color-blueart-muted-text", "#3d6680"),
            ThemeEntry("--k-color-blueart-link", "#085898"),
            ThemeEntry("--k-color-blueart-banner-foreground", "#f2f8ff"),
            ThemeEntry("--k-color-blueart-action-foreground", "#eef6ff"),
            ThemeEntry("--k-font-sans", "Inter, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif"),
        ),
    )
}
