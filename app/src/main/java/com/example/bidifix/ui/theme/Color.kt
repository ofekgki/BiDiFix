package com.example.bidifix.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Raw palette. These are the ONLY places a hex literal may appear — screens and
// components must read colors from MaterialTheme.colorScheme or LocalBidiColors.
// ---------------------------------------------------------------------------

// Light theme
val LightPrimary = Color(0xFF3F51B5)
val LightPrimaryDark = Color(0xFF283593)
val LightSecondary = Color(0xFF00ACC1)
val LightAccent = Color(0xFF00BFA5)
val LightBackground = Color(0xFFF6F7FB)
val LightSurface = Color(0xFFFFFFFF)
val LightInputSurface = Color(0xFFEEF0FF)
val LightTransformedSurface = Color(0xFFE8F8FA)
val LightMainText = Color(0xFF18202E)
val LightSecondaryText = Color(0xFF64748B)
val LightBorder = Color(0xFFD9DEEA)
val LightSuccess = Color(0xFF2EAD68)
val LightWarning = Color(0xFFF4A623)
val LightError = Color(0xFFD94343)

// Dark theme
val DarkPrimary = Color(0xFF9FA8FF)
val DarkSecondary = Color(0xFF5DD9E8)
val DarkAccent = Color(0xFF4DDDC5)
val DarkBackground = Color(0xFF10131A)
val DarkSurface = Color(0xFF191E29)
val DarkInputSurface = Color(0xFF232A46)
val DarkTransformedSurface = Color(0xFF16383E)
val DarkMainText = Color(0xFFF4F6FC)
val DarkSecondaryText = Color(0xFFAAB2C2)
val DarkBorder = Color(0xFF343C4D)
val DarkSuccess = Color(0xFF55C987)
val DarkWarning = Color(0xFFFFC35C)
val DarkError = Color(0xFFFF7474)

// Shared "on" colors. In the light theme the brand colors are deep, so content on
// them is white; in the dark theme the brand colors are light, so content is near-black.
val OnBrandLight = Color(0xFFFFFFFF)
val OnBrandDark = Color(0xFF10131A)

/**
 * Semantic colors that Material 3's [androidx.compose.material3.ColorScheme] has no slot
 * for — the two distinct text-panel surfaces plus status/highlight colors. Provided through
 * [LocalBidiColors] so components can read them without hard-coding hex values.
 */
@Immutable
data class BidiSemanticColors(
    val inputSurface: Color,
    val transformedSurface: Color,
    val border: Color,
    val secondaryText: Color,
    val accent: Color,
    val onAccent: Color,
    val success: Color,
    val warning: Color,
)

val LightBidiColors = BidiSemanticColors(
    inputSurface = LightInputSurface,
    transformedSurface = LightTransformedSurface,
    border = LightBorder,
    secondaryText = LightSecondaryText,
    accent = LightAccent,
    onAccent = OnBrandLight,
    success = LightSuccess,
    warning = LightWarning,
)

val DarkBidiColors = BidiSemanticColors(
    inputSurface = DarkInputSurface,
    transformedSurface = DarkTransformedSurface,
    border = DarkBorder,
    secondaryText = DarkSecondaryText,
    accent = DarkAccent,
    onAccent = OnBrandDark,
    success = DarkSuccess,
    warning = DarkWarning,
)

val LocalBidiColors = staticCompositionLocalOf { LightBidiColors }
