package com.wordforge.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand ────────────────────────────────────────────────

val ForgeOrange       = Color(0xFFFF5A1F)
val ForgeOrangeDeep   = Color(0xFFE54616)
val ForgeOrangeSoft   = Color(0xFFFFE2D1)

// ── Surfaces ─────────────────────────────────────────────

val Cream             = Color(0xFFFBF1E6)
val CreamTint         = Color(0xFFF6E9D5)
val SurfaceWhite      = Color(0xFFFFFFFF)

// ── Ink (text + outlines) ────────────────────────────────

val Ink               = Color(0xFF1F1A14)
val InkSoft           = Color(0xA31F1A14)   // ~62 %
val InkMuted          = Color(0x6B1F1A14)   // ~42 %
val Divider           = Color(0x141F1A14)   //  ~8 %

// ── Accents ──────────────────────────────────────────────

val Sage              = Color(0xFF2C5F4F)   // correct / growth
val SageSoft          = Color(0xFFDCE8E2)
val Gold              = Color(0xFFB7882A)   // streak / celebration
val GoldSoft          = Color(0xFFF4E6BD)

// ── Semantic + tier colors (kept; used outside the M3 scheme) ────

// Bright "win" green for self-grade Correct, feedback, and stat tiles.
// Per-screen migration may swap these for Sage where the brief asks for it.
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
