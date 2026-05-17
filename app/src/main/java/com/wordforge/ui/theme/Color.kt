package com.wordforge.ui.theme

import androidx.compose.ui.graphics.Color

// ── Light theme ──────────────────────────────────────────

// Primary — ember (the forge flame), pushed brighter for playful punch
val md_theme_light_primary = Color(0xFFFF5722)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFDBC8)
val md_theme_light_onPrimaryContainer = Color(0xFF3D0F00)

// Secondary — warm caramel (the anvil), warmer and a touch more saturated
val md_theme_light_secondary = Color(0xFF8B6049)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFD0B3)
val md_theme_light_onSecondaryContainer = Color(0xFF2C160D)

// Tertiary — bright spark gold, was muted olive
val md_theme_light_tertiary = Color(0xFFF2A41E)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFE1A0)
val md_theme_light_onTertiaryContainer = Color(0xFF2A1900)

// Error — punchier red
val md_theme_light_error = Color(0xFFD32F2F)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)

// Surfaces — warm neutral, slightly creamier
val md_theme_light_background = Color(0xFFFFF8F2)
val md_theme_light_onBackground = Color(0xFF1F1A17)
val md_theme_light_surface = Color(0xFFFFF8F2)
val md_theme_light_onSurface = Color(0xFF1F1A17)
val md_theme_light_surfaceVariant = Color(0xFFEFE7E1)
val md_theme_light_onSurfaceVariant = Color(0xFF54453C)
val md_theme_light_outline = Color(0xFF85746B)
val md_theme_light_outlineVariant = Color(0xFFDFD2CA)
val md_theme_light_inverseSurface = Color(0xFF362F2B)
val md_theme_light_inverseOnSurface = Color(0xFFFBEEE8)
val md_theme_light_inversePrimary = Color(0xFFFFB996)
val md_theme_light_surfaceTint = Color(0xFFFF5722)

// M3 surface containers — explicit tonal hierarchy
val md_theme_light_surfaceContainerLowest = Color(0xFFFFFEFC)
val md_theme_light_surfaceContainerLow = Color(0xFFF8F2EC)
val md_theme_light_surfaceContainer = Color(0xFFF2EAE2)
val md_theme_light_surfaceContainerHigh = Color(0xFFECE3D9)
val md_theme_light_surfaceContainerHighest = Color(0xFFE5DBCF)

// ── Dark theme ───────────────────────────────────────────

val md_theme_dark_primary = Color(0xFFFFB996)
val md_theme_dark_onPrimary = Color(0xFF5F1600)
val md_theme_dark_primaryContainer = Color(0xFFA02700)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFDBC8)

val md_theme_dark_secondary = Color(0xFFEFC4B1)
val md_theme_dark_onSecondary = Color(0xFF442A20)
val md_theme_dark_secondaryContainer = Color(0xFF6E4A3A)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFD0B3)

val md_theme_dark_tertiary = Color(0xFFF7CA68)
val md_theme_dark_onTertiary = Color(0xFF3B2F05)
val md_theme_dark_tertiaryContainer = Color(0xFF6E5419)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFE1A0)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = Color(0xFF1A1613)
val md_theme_dark_onBackground = Color(0xFFEDE0D9)
val md_theme_dark_surface = Color(0xFF1A1613)
val md_theme_dark_onSurface = Color(0xFFEDE0D9)
val md_theme_dark_surfaceVariant = Color(0xFF53443C)
val md_theme_dark_onSurfaceVariant = Color(0xFFD8C2B8)
val md_theme_dark_outline = Color(0xFFA08D83)
val md_theme_dark_outlineVariant = Color(0xFF53443C)
val md_theme_dark_inverseSurface = Color(0xFFEDE0D9)
val md_theme_dark_inverseOnSurface = Color(0xFF1A1613)
val md_theme_dark_inversePrimary = Color(0xFFFF5722)
val md_theme_dark_surfaceTint = Color(0xFFFFB996)

val md_theme_dark_surfaceContainerLowest = Color(0xFF110D0B)
val md_theme_dark_surfaceContainerLow = Color(0xFF211C18)
val md_theme_dark_surfaceContainer = Color(0xFF25201C)
val md_theme_dark_surfaceContainerHigh = Color(0xFF2F2A26)
val md_theme_dark_surfaceContainerHighest = Color(0xFF3A3530)

// ── Semantic colors (used across both themes) ────────────

// Bright "win" green for self-grade Correct, feedback, and stat tiles
val Success = Color(0xFF58CC02)
val SuccessContainer = Color(0xFFD7F4B6)
val OnSuccess = Color(0xFFFFFFFF)

// Tier badge colors — warm gradient from red (new) to green (mastered).
// 9 entries to match SpacedRepetition.MAX_TIER (0..8).
val TierColors = listOf(
    Color(0xFFE53935),  // Tier 0 — just added
    Color(0xFFF4511E),  // Tier 1
    Color(0xFFFF9800),  // Tier 2
    Color(0xFFFFC107),  // Tier 3
    Color(0xFFCDDC39),  // Tier 4
    Color(0xFF8BC34A),  // Tier 5
    Color(0xFF66BB6A),  // Tier 6
    Color(0xFF4CAF50),  // Tier 7
    Color(0xFF2E7D32),  // Tier 8 — mastered
)
