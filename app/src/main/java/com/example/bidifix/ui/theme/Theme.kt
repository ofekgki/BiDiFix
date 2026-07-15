package com.example.bidifix.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = OnBrandLight,
    primaryContainer = LightPrimaryDark,
    onPrimaryContainer = OnBrandLight,
    secondary = LightSecondary,
    onSecondary = OnBrandLight,
    tertiary = LightAccent,
    onTertiary = OnBrandLight,
    background = LightBackground,
    onBackground = LightMainText,
    surface = LightSurface,
    onSurface = LightMainText,
    surfaceVariant = LightInputSurface,
    onSurfaceVariant = LightSecondaryText,
    outline = LightBorder,
    outlineVariant = LightBorder,
    error = LightError,
    onError = OnBrandLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = OnBrandDark,
    primaryContainer = DarkPrimary,
    onPrimaryContainer = OnBrandDark,
    secondary = DarkSecondary,
    onSecondary = OnBrandDark,
    tertiary = DarkAccent,
    onTertiary = OnBrandDark,
    background = DarkBackground,
    onBackground = DarkMainText,
    surface = DarkSurface,
    onSurface = DarkMainText,
    surfaceVariant = DarkInputSurface,
    onSurfaceVariant = DarkSecondaryText,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = DarkError,
    onError = OnBrandDark,
)

/**
 * Convenience accessor for the app's extended semantic colors, e.g.
 * `BiDiTheme.colors.transformedSurface`.
 */
object BiDiTheme {
    val colors: BidiSemanticColors
        @Composable @ReadOnlyComposable get() = LocalBidiColors.current
}

/**
 * The centralized Material 3 theme. Dynamic color is intentionally disabled so the app
 * always uses the defined brand palette; dark mode follows the system setting.
 */
@Composable
fun BiDiFixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val bidiColors = if (darkTheme) DarkBidiColors else LightBidiColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalBidiColors provides bidiColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
