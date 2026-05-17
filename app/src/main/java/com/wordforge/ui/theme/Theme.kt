package com.wordforge.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val WordForgeLightColors = lightColorScheme(
    primary              = ForgeOrange,
    onPrimary            = SurfaceWhite,
    primaryContainer     = ForgeOrangeSoft,
    onPrimaryContainer   = ForgeOrangeDeep,

    secondary            = Sage,
    onSecondary          = SurfaceWhite,
    secondaryContainer   = SageSoft,
    onSecondaryContainer = Sage,

    tertiary             = Gold,
    onTertiary           = SurfaceWhite,
    tertiaryContainer    = GoldSoft,
    onTertiaryContainer  = Ink,

    background           = Cream,
    onBackground         = Ink,
    surface              = SurfaceWhite,
    onSurface            = Ink,
    surfaceVariant       = CreamTint,
    onSurfaceVariant     = InkSoft,

    outline              = Divider,
    outlineVariant       = Divider,
)

// Rounder than M3 defaults — playful direction for any component that
// doesn't override its shape inline.
private val PlayfulShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun WordForgeTheme(
    content: @Composable () -> Unit
) {
    // Dark variant is intentionally not defined yet — the refreshed
    // palette is light-mode-only. Apply the light scheme unconditionally
    // so the app stays consistent across system settings.
    MaterialTheme(
        colorScheme = WordForgeLightColors,
        typography = Typography,
        shapes = PlayfulShapes,
        content = content
    )
}
