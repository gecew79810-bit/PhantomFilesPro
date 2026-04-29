package com.phantomfiles.pro.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DeepBlack = Color(0xFF050505)
val SurfaceDark = Color(0xFF0F0F0F)
val SurfaceVariant = Color(0xFF1A1A2E)
val ElectricCyan = Color(0xFF00E5FF)
val PhantomPurple = Color(0xFF7C4DFF)
val NeonGreen = Color(0xFF00E676)
val AmberWarning = Color(0xFFFFB300)
val DangerRed = Color(0xFFFF1744)
val TextPrimary = Color(0xFFE0E0E0)
val TextSecondary = Color(0xFF9E9E9E)
val CardBackground = Color(0xFF121218)
val DividerColor = Color(0xFF2A2A3E)

private val PhantomColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = DeepBlack,
    secondary = PhantomPurple,
    onSecondary = Color.White,
    tertiary = NeonGreen,
    onTertiary = DeepBlack,
    background = DeepBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = DangerRed,
    onError = Color.White,
    outline = DividerColor,
    outlineVariant = DividerColor
)

@Composable
fun PhantomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PhantomColorScheme,
        typography = PhantomTypography,
        content = content
    )
}
