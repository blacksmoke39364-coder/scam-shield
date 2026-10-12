package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium Silver & Black Luxury Theme
// Obsidian & Deep Carbon Blacks
val BlackPure = Color(0xFF000000)
val BlackObsidian = Color(0xFF05070B)
val BlackCarbon = Color(0xFF0A0E17)
val BlackSurface = Color(0xFF101522)
val BlackSurfaceVariant = Color(0xFF161E2E)
val BlackElevated = Color(0xFF1B2436)

// Polished Metallic Silvers & Platinums
val SilverPure = Color(0xFFFFFFFF)
val SilverPlatinum = Color(0xFFF8FAFC)
val SilverHighlight = Color(0xFFE2E8F0)
val SilverMetallic = Color(0xFFCBD5E1)
val SilverChrome = Color(0xFFA6B4C9)
val SilverSteel = Color(0xFF718096)
val SilverDark = Color(0xFF475569)
val SilverBorder = Color(0xFF2D3748)
val SilverBorderGleam = Color(0xFF526077)

// Backward compatibility tokens mapped to Premium Silver & Chrome
val Navy950 = BlackObsidian
val Navy900 = BlackCarbon
val Navy800 = BlackSurface
val Navy700 = SilverBorder
val Navy600 = SilverDark
val Navy400 = SilverChrome
val Navy200 = SilverHighlight
val Navy100 = SilverPlatinum
val White = SilverPure

// Premium Luminous Silver Accent (Replaces standard blue with sleek chrome platinum)
val BlueAccent = Color(0xFFE2E8F0) // Silver Platinum Accent
val BlueAccentLight = Color(0xFFFFFFFF)
val BlueAccentDark = Color(0xFFCBD5E1)

// Polished Subtle Teal (Platinum Teal for verified safe states)
val TealSubtle = Color(0xFF2DD4BF)
val TealSubtleLight = Color(0xFF5EEAD4)
val TealSubtleDark = Color(0xFF14B8A6)

// Risk Semantic Colors with 3D Metallic depth
val RiskHighRed = Color(0xFFEF4444)
val RiskHighBgDark = Color(0xFF240A0D)
val RiskHighBgLight = Color(0xFFFEE2E2)

val RiskMediumAmber = Color(0xFFF59E0B)
val RiskMediumBgDark = Color(0xFF261906)
val RiskMediumBgLight = Color(0xFFFEF3C7)

val RiskLowTeal = Color(0xFF10B981)
val RiskLowBgDark = Color(0xFF072118)
val RiskLowBgLight = Color(0xFFD1FAE5)

val RiskUncertainSlate = Color(0xFF94A3B8)
val RiskUncertainBgDark = Color(0xFF141A24)
val RiskUncertainBgLight = Color(0xFFF1F5F9)

// 3D Metallic Brushes
val SilverMetallicGradient = Brush.linearGradient(
    listOf(
        Color(0xFFFFFFFF),
        Color(0xFFE2E8F0),
        Color(0xFF94A3B8),
        Color(0xFFCBD5E1),
        Color(0xFFFFFFFF)
    )
)

val BlackMetallicGradient = Brush.verticalGradient(
    listOf(
        Color(0xFF1A2233),
        Color(0xFF0F1522),
        Color(0xFF06090F)
    )
)

val Card3DGleamGradient = Brush.verticalGradient(
    listOf(
        Color(0x40FFFFFF),
        Color(0x08FFFFFF),
        Color(0x30000000)
    )
)
