package com.wordforge.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordforge.data.Word
import com.wordforge.domain.SpacedRepetition
import com.wordforge.ui.theme.Success
import com.wordforge.ui.theme.TierColors
import com.wordforge.viewmodel.WordViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    wordId: String,
    viewModel: WordViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: (String) -> Unit
) {
    var word by remember { mutableStateOf<Word?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var meaningRevealed by remember { mutableStateOf(false) }

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = System.currentTimeMillis()
        }
    }

    LaunchedEffect(wordId) {
        word = viewModel.getWordById(wordId)
        isLoading = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                word == null -> {
                    Text(
                        text = "Word not found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    val currentWord = word!!
                    val tierColor = TierColors.getOrElse(currentWord.currentTier) { TierColors.last() }
                    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                    val isOverdue = currentWord.nextPromptAt <= now

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Hero region — edge-to-edge tier-color gradient backdrop
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            tierColor.copy(alpha = 0.18f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                HeroHeader(word = currentWord, tierColor = tierColor)
                                Spacer(modifier = Modifier.height(24.dp))
                                TierProgressTrack(currentTier = currentWord.currentTier)
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }

                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Spacer(modifier = Modifier.height(8.dp))

                            MeaningCard(
                                meaning = currentWord.meaning,
                                revealed = meaningRevealed,
                                onToggle = { meaningRevealed = !meaningRevealed }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            NextPromptCard(
                                nextPromptAt = currentWord.nextPromptAt,
                                now = now,
                                isOverdue = isOverdue
                            )

                            if (isOverdue) {
                                Spacer(modifier = Modifier.height(12.dp))
                                ReviewNowButton(onClick = { onNavigateToQuiz(currentWord.id) })
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            StatsRow(
                                correct = currentWord.totalCorrect,
                                incorrect = currentWord.totalIncorrect
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            DatesCard(
                                createdAt = currentWord.createdAt,
                                lastAnsweredAt = currentWord.lastAnsweredAt,
                                dateFormat = dateFormat
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroHeader(word: Word, tierColor: androidx.compose.ui.graphics.Color) {
    Column {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(tierColor)
                .border(
                    width = 4.dp,
                    color = tierColor.copy(alpha = 0.18f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${word.currentTier}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = word.word,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 40.sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Tier ${word.currentTier} of ${SpacedRepetition.MAX_TIER}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = tierColor
        )
    }
}

@Composable
private fun TierProgressTrack(currentTier: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (i in 0..SpacedRepetition.MAX_TIER) {
            val isReached = i <= currentTier
            val isCurrent = i == currentTier
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(if (isCurrent) 10.dp else 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isReached)
                            TierColors.getOrElse(i) { TierColors.last() }
                        else
                            MaterialTheme.colorScheme.surfaceContainerHighest
                    )
            )
        }
    }
}

@Composable
private fun MeaningCard(
    meaning: String,
    revealed: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        if (revealed) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = meaning,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Hide",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tap to reveal meaning",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun NextPromptCard(
    nextPromptAt: Long,
    now: Long,
    isOverdue: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.AccessTime,
                contentDescription = null,
                tint = if (isOverdue)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Next prompt",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatCountdown(nextPromptAt, now),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isOverdue)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ReviewNowButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Review now",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun StatsRow(correct: Int, incorrect: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatTile(
            value = correct,
            label = "Correct",
            color = Success,
            icon = Icons.Rounded.Check,
            modifier = Modifier.weight(1f)
        )
        StatTile(
            value = incorrect,
            label = "Incorrect",
            color = MaterialTheme.colorScheme.error,
            icon = Icons.Rounded.Close,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatTile(
    value: Int,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "$value",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DatesCard(
    createdAt: Long,
    lastAnsweredAt: Long?,
    dateFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            DetailRow(
                label = "Added on",
                value = dateFormat.format(Date(createdAt))
            )

            if (lastAnsweredAt != null) {
                Spacer(modifier = Modifier.height(10.dp))
                DetailRow(
                    label = "Last answered",
                    value = dateFormat.format(Date(lastAnsweredAt))
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
