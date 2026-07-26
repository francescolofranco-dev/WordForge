package com.wordforge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordforge.ui.theme.LocalWordForgeColors
import com.wordforge.ui.theme.WordForgeTheme

@Composable
fun StreakCard(
    streak: Int,
    modifier: Modifier = Modifier,
) {
    val subtitle = streakSubtitle(streak)
    val wordForgeColors = LocalWordForgeColors.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = wordForgeColors.celebrationContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(wordForgeColors.celebration),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = streak.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = wordForgeColors.onCelebration,
                )
            }
            Column(
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = if (streak == 1) "1 correct in a row" else "$streak correct in a row",
                    style = MaterialTheme.typography.titleLarge,
                    color = wordForgeColors.onCelebrationContainer,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun streakSubtitle(streak: Int): String = when {
    streak <= 0 -> "Just getting started."
    streak < STEADY_FORGE_THRESHOLD -> {
        val remaining = STEADY_FORGE_THRESHOLD - streak
        if (remaining == 1) "One more to unlock a steady forge."
        else "$remaining more to unlock a steady forge."
    }
    streak == STEADY_FORGE_THRESHOLD -> "Steady forge unlocked."
    else -> "This item is holding strong."
}

private const val STEADY_FORGE_THRESHOLD = 5

@Preview
@Composable
private fun StreakCardPreview() {
    WordForgeTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StreakCard(streak = 1)
            StreakCard(streak = 4)
            StreakCard(streak = 5)
            StreakCard(streak = 9)
        }
    }
}
