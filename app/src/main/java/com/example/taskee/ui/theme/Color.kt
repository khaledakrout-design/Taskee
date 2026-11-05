package com.example.taskee.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Raw Color Constants for Dark Theme
val RawDarkBackground = Color(0xFF141412)
val RawDarkSurface = Color(0xFF1F1F1C)
val RawDarkSurfaceVariant = Color(0xFF2B2B27)
val RawCardBackgroundDark = Color(0xFF1E1E1C)
val RawBorderColorDark = Color(0xFF333330)
val RawTabGreyDark = Color(0xFF6C6A69)

// Raw Color Constants for Light Theme
val RawLightBackground = Color(0xFFF6F6F8)
val RawLightSurface = Color(0xFFFFFFFF)
val RawLightSurfaceVariant = Color(0xFFECECEE)
val RawCardBackgroundLight = Color(0xFFFFFFFF)
val RawBorderColorLight = Color(0xFFE2E2E8)
val RawTabGreyLight = Color(0xFF8E8E93)

// Accent Colors
val RawAccentLimeDark = Color(0xFFD4E157)
val RawAccentLimeLight = Color(0xFF689F38)
val AccentLight = Color(0xFFD8E87C)
val NoteCardBg = Color(0xFFE8ECC8)

// Text Colors
val RawTextPrimaryDark = Color(0xFFF0F0EC)
val RawTextSecondaryDark = Color(0xFFAAADA0)
val RawTextMutedDark = Color(0xFF666860)

val RawTextPrimaryLight = Color(0xFF1C1C1E)
val RawTextSecondaryLight = Color(0xFF636366)
val RawTextMutedLight = Color(0xFF8E8E93)

val TextDark = Color(0xFF1A1A18)

// Status Colors
val StatusError = Color(0xFFCF6679)
val StatusSuccess = Color(0xFF81C784)
val StatusWarning = Color(0xFFFFB74D)

data class TaskeeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val cardBackground: Color,
    val borderColor: Color,
    val tabGrey: Color,
    val accentLime: Color,
    val accentLight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isDark: Boolean
)

val DarkTaskeeColors = TaskeeColors(
    background = RawDarkBackground,
    surface = RawDarkSurface,
    surfaceVariant = RawDarkSurfaceVariant,
    cardBackground = RawCardBackgroundDark,
    borderColor = RawBorderColorDark,
    tabGrey = RawTabGreyDark,
    accentLime = RawAccentLimeDark,
    accentLight = AccentLight,
    textPrimary = RawTextPrimaryDark,
    textSecondary = RawTextSecondaryDark,
    textMuted = RawTextMutedDark,
    isDark = true
)

val LightTaskeeColors = TaskeeColors(
    background = RawLightBackground,
    surface = RawLightSurface,
    surfaceVariant = RawLightSurfaceVariant,
    cardBackground = RawCardBackgroundLight,
    borderColor = RawBorderColorLight,
    tabGrey = RawTabGreyLight,
    accentLime = RawAccentLimeLight,
    accentLight = Color(0xFF9CCC65),
    textPrimary = RawTextPrimaryLight,
    textSecondary = RawTextSecondaryLight,
    textMuted = RawTextMutedLight,
    isDark = false
)

val LocalTaskeeColors = staticCompositionLocalOf { DarkTaskeeColors }

val DarkBackground: Color @Composable get() = LocalTaskeeColors.current.background
val DarkSurface: Color @Composable get() = LocalTaskeeColors.current.surface
val DarkSurfaceVariant: Color @Composable get() = LocalTaskeeColors.current.surfaceVariant
val CardBackground: Color @Composable get() = LocalTaskeeColors.current.cardBackground
val BorderColor: Color @Composable get() = LocalTaskeeColors.current.borderColor
val TabGrey: Color @Composable get() = LocalTaskeeColors.current.tabGrey
val AccentLime: Color @Composable get() = LocalTaskeeColors.current.accentLime
val TextPrimary: Color @Composable get() = LocalTaskeeColors.current.textPrimary
val TextSecondary: Color @Composable get() = LocalTaskeeColors.current.textSecondary
val TextMuted: Color @Composable get() = LocalTaskeeColors.current.textMuted
