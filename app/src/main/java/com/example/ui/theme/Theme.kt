package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Premium Silver & Black Scheme (Dark Mode is Primary Luxury Aesthetic)
private val DarkColorScheme = darkColorScheme(
    primary = SilverHighlight,
    onPrimary = BlackObsidian,
    primaryContainer = BlackElevated,
    onPrimaryContainer = SilverPure,
    secondary = SilverMetallic,
    onSecondary = BlackObsidian,
    secondaryContainer = BlackSurfaceVariant,
    onSecondaryContainer = SilverHighlight,
    tertiary = TealSubtle,
    onTertiary = BlackObsidian,
    background = BlackObsidian,
    onBackground = SilverPure,
    surface = BlackCarbon,
    onSurface = SilverPure,
    surfaceVariant = BlackSurface,
    onSurfaceVariant = SilverChrome,
    outline = SilverBorder,
    outlineVariant = SilverDark,
    error = RiskHighRed,
    onError = SilverPure
)

private val LightColorScheme = lightColorScheme(
    primary = BlackCarbon,
    onPrimary = SilverPure,
    primaryContainer = SilverPlatinum,
    onPrimaryContainer = BlackObsidian,
    secondary = SilverDark,
    onSecondary = SilverPure,
    secondaryContainer = SilverHighlight,
    onSecondaryContainer = BlackCarbon,
    tertiary = TealSubtle,
    onTertiary = SilverPure,
    background = Color(0xFFF1F5F9),
    onBackground = BlackObsidian,
    surface = SilverPure,
    onSurface = BlackObsidian,
    surfaceVariant = SilverPlatinum,
    onSurfaceVariant = SilverDark,
    outline = SilverMetallic,
    outlineVariant = SilverHighlight,
    error = RiskHighRed,
    onError = SilverPure
)

// Precision 3D Bevel Shapes: 8dp to 12dp rounded corners for clean tactile depth
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(10.dp),
    extraLarge = RoundedCornerShape(12.dp)
)

@Composable
fun ScamShieldTheme(
    darkTheme: Boolean = true, // Default to true for the requested Premium Silver & Black look
    content: @Composable () -> Unit
) {
    // Both dark and light themes uphold the Premium Silver & Black philosophy
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
