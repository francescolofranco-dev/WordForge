package com.wordforge.ui.theme

import androidx.compose.ui.graphics.Color

// ── Light theme ──────────────────────────────────────────

// Primary — bold burnt orange (the forge flame)
val md_theme_light_primary = Color(0xFFBF4A13)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFDBCC)
val md_theme_light_onPrimaryContainer = Color(0xFF3A0B00)

// Secondary — warm brown (the anvil)
val md_theme_light_secondary = Color(0xFF77574B)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFDBCC)
val md_theme_light_onSecondaryContainer = Color(0xFF2C160D)

// Tertiary — golden amber (sparks)
val md_theme_light_tertiary = Color(0xFF6C5D2F)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFF6E1A7)
val md_theme_light_onTertiaryContainer = Color(0xFF231B00)

// Error — deep red
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)

// Surfaces — warm off-white
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF201A17)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = Color(0xFF201A17)
val md_theme_light_surfaceVariant = Color(0xFFF5DED3)
val md_theme_light_onSurfaceVariant = Color(0xFF53443C)
val md_theme_light_outline = Color(0xFF85746B)
val md_theme_light_outlineVariant = Color(0xFFD8C2B8)
val md_theme_light_inverseSurface = Color(0xFF362F2B)
val md_theme_light_inverseOnSurface = Color(0xFFFBEEE8)
val md_theme_light_inversePrimary = Color(0xFFFFB692)
val md_theme_light_surfaceTint = Color(0xFFBF4A13)

// ── Dark theme ───────────────────────────────────────────

val md_theme_dark_primary = Color(0xFFFFB692)
val md_theme_dark_onPrimary = Color(0xFF5F1600)
val md_theme_dark_primaryContainer = Color(0xFF872300)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFDBCC)

val md_theme_dark_secondary = Color(0xFFE7BEAE)
val md_theme_dark_onSecondary = Color(0xFF442A20)
val md_theme_dark_secondaryContainer = Color(0xFF5D4035)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFDBCC)

val md_theme_dark_tertiary = Color(0xFFD9C58D)
val md_theme_dark_onTertiary = Color(0xFF3B2F05)
val md_theme_dark_tertiaryContainer = Color(0xFF534619)
val md_theme_dark_onTertiaryContainer = Color(0xFFF6E1A7)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = Color(0xFF201A17)
val md_theme_dark_onBackground = Color(0xFFEDE0D9)
val md_theme_dark_surface = Color(0xFF201A17)
val md_theme_dark_onSurface = Color(0xFFEDE0D9)
val md_theme_dark_surfaceVariant = Color(0xFF53443C)
val md_theme_dark_onSurfaceVariant = Color(0xFFD8C2B8)
val md_theme_dark_outline = Color(0xFFA08D83)
val md_theme_dark_outlineVariant = Color(0xFF53443C)
val md_theme_dark_inverseSurface = Color(0xFFEDE0D9)
val md_theme_dark_inverseOnSurface = Color(0xFF201A17)
val md_theme_dark_inversePrimary = Color(0xFFBF4A13)
val md_theme_dark_surfaceTint = Color(0xFFFFB692)

// ── Semantic colors (used across both themes) ────────────

val Success = Color(0xFF2E7D32)
val SuccessContainer = Color(0xFFC8E6C9)
val OnSuccess = Color(0xFFFFFFFF)

// Tier badge colors — warm gradient from red (new) to green (mastered)
val TierColors = listOf(
    Color(0xFFE53935),  // Tier 0 — just added
    Color(0xFFF4511E),  // Tier 1
    Color(0xFFFF9800),  // Tier 2
    Color(0xFFFFC107),  // Tier 3
    Color(0xFFCDDC39),  // Tier 4
    Color(0xFF8BC34A),  // Tier 5
    Color(0xFF4CAF50),  // Tier 6
    Color(0xFF2E7D32),  // Tier 7 — mastered
)