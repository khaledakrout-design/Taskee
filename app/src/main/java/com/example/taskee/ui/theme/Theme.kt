package com.example.taskee.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentLime,
    onPrimary = TextDark,
    primaryContainer = AccentLight,
    onPrimaryContainer = TextDark,
    secondary = AccentLight,
    onSecondary = TextDark,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = StatusError,
    onError = TextPrimary,
    outline = BorderColor
)

@Composable
fun TaskeeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
