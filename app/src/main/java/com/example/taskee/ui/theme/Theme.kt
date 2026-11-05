package com.example.taskee.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = RawAccentLimeDark,
    onPrimary = TextDark,
    primaryContainer = AccentLight,
    onPrimaryContainer = TextDark,
    secondary = AccentLight,
    onSecondary = TextDark,
    background = RawDarkBackground,
    onBackground = RawTextPrimaryDark,
    surface = RawDarkSurface,
    onSurface = RawTextPrimaryDark,
    surfaceVariant = RawDarkSurfaceVariant,
    onSurfaceVariant = RawTextSecondaryDark,
    error = StatusError,
    onError = RawTextPrimaryDark,
    outline = RawBorderColorDark
)

private val LightColorScheme = lightColorScheme(
    primary = RawAccentLimeLight,
    onPrimary = RawLightSurface,
    primaryContainer = RawLightSurfaceVariant,
    onPrimaryContainer = RawTextPrimaryLight,
    secondary = RawAccentLimeLight,
    onSecondary = RawLightSurface,
    background = RawLightBackground,
    onBackground = RawTextPrimaryLight,
    surface = RawLightSurface,
    onSurface = RawTextPrimaryLight,
    surfaceVariant = RawLightSurfaceVariant,
    onSurfaceVariant = RawTextSecondaryLight,
    error = StatusError,
    onError = RawLightSurface,
    outline = RawBorderColorLight
)

@Composable
fun TaskeeTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val taskeeColors = if (darkTheme) DarkTaskeeColors else LightTaskeeColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalTaskeeColors provides taskeeColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
