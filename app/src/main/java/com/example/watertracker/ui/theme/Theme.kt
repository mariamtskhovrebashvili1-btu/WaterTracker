package com.example.watertracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = WaterBlue40,
    onPrimary = Foam,
    primaryContainer = WaterBlue90,
    onPrimaryContainer = WaterBlue10,
    secondary = AquaGreen40,
    onSecondary = Foam,
    secondaryContainer = AquaGreen80,
    background = SkyMist,
    surface = Foam,
    onBackground = DeepNavy,
    onSurface = DeepNavy,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = WaterBlue80,
    onPrimary = WaterBlue10,
    primaryContainer = WaterBlue20,
    onPrimaryContainer = WaterBlue90,
    secondary = AquaGreen80,
    onSecondary = WaterBlue10,
    background = DeepNavy,
    surface = WaterBlue20,
    onBackground = SkyMist,
    onSurface = SkyMist,
    error = ErrorRed
)

@Composable
fun WaterTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WaterTrackerTypography,
        content = content
    )
}
