package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CricketGreenLight,
    onPrimary = CricketNavy,
    primaryContainer = CricketGreenDark,
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = CricketGold,
    onSecondary = CricketNavy,
    secondaryContainer = CricketNavyElevated,
    onSecondaryContainer = CricketGold,
    tertiary = CricketRedWicket,
    onTertiary = TextWhite,
    background = CricketNavy,
    onBackground = TextWhite,
    surface = CricketNavySurface,
    onSurface = TextWhite,
    surfaceVariant = CricketNavyElevated,
    onSurfaceVariant = TextMuted,
    outline = BorderDark,
    error = CricketRedWicket,
    onError = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = CricketGreenPrimary,
    onPrimary = TextWhite,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = CricketGreenDark,
    secondary = CricketAmber,
    onSecondary = TextWhite,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = CricketRedWicket,
    onTertiary = TextWhite,
    background = BackgroundLight,
    onBackground = TextDark,
    surface = SurfaceLight,
    onSurface = TextDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),
    outline = BorderLight,
    error = CricketRedWicket,
    onError = TextWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false by default to showcase signature cricket palette
    highContrast: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        highContrast && darkTheme -> DarkColorScheme.copy(
            background = Color.Black,
            surface = Color(0xFF121212),
            onBackground = Color.White,
            onSurface = Color.White,
            primary = Color(0xFF00FF88),
            secondary = Color(0xFFFFD700)
        )
        highContrast && !darkTheme -> LightColorScheme.copy(
            background = Color.White,
            surface = Color(0xFFF0F0F0),
            onBackground = Color.Black,
            onSurface = Color.Black,
            primary = Color(0xFF006400),
            secondary = Color(0xFFB8860B)
        )
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
