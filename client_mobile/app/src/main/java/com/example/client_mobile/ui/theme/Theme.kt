package com.example.client_mobile.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DeepGreen,
    secondary = Gold,
    background = DeepGreen,
    surface = DeepGreen,
    onPrimary = CrispWhite,
    onSecondary = CrispWhite,
    onBackground = CrispWhite,
    onSurface = CrispWhite
)

private val LightColorScheme = lightColorScheme(
    primary = DeepGreen,
    secondary = Gold,
    background = CrispWhite,
    surface = OffWhite,
    onPrimary = CrispWhite,
    onSecondary = CrispWhite,
    onBackground = DeepGreen,
    onSurface = DeepGreen,
    surfaceVariant = SurfaceGray,
    error = ErrorRed
)

@Composable
fun LegalAgroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
