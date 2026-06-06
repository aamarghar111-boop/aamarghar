package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = M3Primary,
    secondary = M3SecondaryContainer,
    tertiary = BrightGold,
    background = M3Background,
    surface = M3Surface,
    onPrimary = M3OnPrimary,
    onSecondary = M3OnSecondaryContainer,
    onTertiary = Color.White,
    onBackground = M3OnBackground,
    onSurface = M3OnBackground,
    surfaceVariant = M3SurfaceVariant,
    onSurfaceVariant = M3OnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = M3Primary,
    secondary = M3SecondaryContainer,
    tertiary = BrightGold,
    background = M3Background,
    surface = M3Surface,
    onPrimary = M3OnPrimary,
    onSecondary = M3OnSecondaryContainer,
    onTertiary = Color.White,
    onBackground = M3OnBackground,
    onSurface = M3OnBackground,
    surfaceVariant = M3SurfaceVariant,
    onSurfaceVariant = M3OnSurfaceVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to false (light theme) for the Professional Polish theme!
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve our tailored visual look
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
