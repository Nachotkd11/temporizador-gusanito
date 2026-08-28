package com.gusanito.temporizador.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun GusanitoTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = Warm.worm,
        background = Warm.paper,
        surface = Warm.card,
        onBackground = Warm.ink,
        onSurface = Warm.ink,
    )
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
