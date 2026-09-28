package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dedicated Healthcare Theme Palette (White / Light Blue with Dark Navy text)
private val HealthcareColorScheme = lightColorScheme(
    primary = MedicalBluePrimary,
    onPrimary = Color.White,
    primaryContainer = MedicalBlueLight,
    onPrimaryContainer = MedicalBlueDark,
    secondary = MedicalNavy,
    onSecondary = Color.White,
    secondaryContainer = MedicalBlueSubtle,
    onSecondaryContainer = MedicalNavy,
    tertiary = LiveGreen,
    onTertiary = Color.White,
    tertiaryContainer = LiveGreenLight,
    onTertiaryContainer = Color(0xFF065F46),
    background = MedicalBackground,
    onBackground = MedicalNavy,
    surface = Color.White,
    onSurface = MedicalNavy,
    surfaceVariant = Color(0xFFEBF2FE),
    onSurfaceVariant = MedicalNavy,
    outline = MedicalBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = DangerRed,
    onError = Color.White,
    errorContainer = DangerRedLight,
    onErrorContainer = Color(0xFF991B1B)
)

@Composable
fun QlinicsTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = HealthcareColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    QlinicsTheme(darkTheme = false, content = content)
}
