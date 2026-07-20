package com.wordforge.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand ────────────────────────────────────────────────

val ForgeOrange       = Color(0xFFFF5A1F)
val ForgeOrangeDeep   = Color(0xFFE54616)
val ForgeOrangeSoft   = Color(0xFFFFE2D1)
val ForgeOrangeDark   = Color(0xFFFF9D6E)
val ForgeOrangeDarkDeep = Color(0xFFFFB99A)
val ForgeOrangeDarkSoft = Color(0xFF5C1F0B)

// ── Surfaces ─────────────────────────────────────────────

val Cream             = Color(0xFFFBF1E6)
val CreamTint         = Color(0xFFF6E9D5)
val SurfaceWhite      = Color(0xFFFFFFFF)
val Charcoal          = Color(0xFF17130F)
val CharcoalElevated  = Color(0xFF211B16)
val CharcoalTint      = Color(0xFF2B241E)

// ── Ink (text + outlines) ────────────────────────────────

val Ink               = Color(0xFF1F1A14)
val InkSoft           = Color(0xA31F1A14)   // ~62 %
val Divider           = Color(0x141F1A14)   //  ~8 %
val Parchment         = Color(0xFFF1E4D8)
val ParchmentSoft     = Color(0xB3F1E4D8)   // ~70 %
val DividerDark       = Color(0x33F1E4D8)   // ~20 %

// ── Accents ──────────────────────────────────────────────

val Sage              = Color(0xFF2C5F4F)   // correct / growth
val SageSoft          = Color(0xFFDCE8E2)
val SageDark          = Color(0xFF8BCDB6)
val SageDarkSoft      = Color(0xFF173D32)
val Gold              = Color(0xFFB7882A)   // streak / celebration
val GoldSoft          = Color(0xFFF4E6BD)
val GoldDark          = Color(0xFFE2C066)
val GoldDarkSoft      = Color(0xFF473514)

// ── Semantic + tier colors (kept; used outside the M3 scheme) ────

// Bright "win" green for self-grade Correct, feedback, and stat tiles.
// Per-screen migration may swap these for Sage where the brief asks for it.
val Success = Color(0xFF58CC02)
val SuccessContainer = Color(0xFFD7F4B6)
val OnSuccess = Color(0xFFFFFFFF)
val OnSuccessContainer = Color(0xFF163300)

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
