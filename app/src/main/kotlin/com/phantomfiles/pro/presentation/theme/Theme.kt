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
val CardGlass = Color(0xFF0E0E14)
val DividerColor = Color(0xFF2A2A3E)
val CyanGlow = Color(0x1A00E5FF)
val PurpleGlow = Color(0x1A7C4DFF)

// Futuristic dashboard palette
val PfBg = Color(0xFF050814)
val PfPanel = Color(0xFF060B14)
val PfPanelAlt = Color(0xFF08111F)
val PfStroke = Color(0xFF183764)
val PfBlue = Color(0xFF19D9FF)
val PfBlue2 = Color(0xFF23C7FF)
val PfPurple = Color(0xFF9A4DFF)
val PfGreen = Color(0xFF2BFF88)
val PfPink = Color(0xFFFF4FD8)
val PfRed = Color(0xFFFF5A7A)
val PfAmber = Color(0xFFFFC233)
val PfText = Color(0xFFEAF2FF)
val PfTextDim = Color(0xFF9EB0D6)

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
