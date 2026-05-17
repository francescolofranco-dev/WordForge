package com.wordforge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordforge.R
import com.wordforge.ui.theme.WordForgeTheme

@Composable
fun SparksLogo(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    background: Color = MaterialTheme.colorScheme.primary,
    foreground: Color = MaterialTheme.colorScheme.onPrimary,
    cornerRadius: Dp = size * 0.26f,
) {
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_sparks),
            contentDescription = null,
            tint = foreground,
            modifier = Modifier.size(size * 0.62f),
        )
    }
}

@Preview
@Composable
private fun SparksLogoPreview() {
    WordForgeTheme {
        SparksLogo()
    }
}
