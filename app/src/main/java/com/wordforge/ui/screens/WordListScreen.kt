package com.wordforge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wordforge.data.Word
import com.wordforge.ui.theme.TierColors
import com.wordforge.viewmodel.WordViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListScreen(
    viewModel: WordViewModel,
    onNavigateToAddWord: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHowItWorks: () -> Unit
) {
    val words by viewModel.allWords.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showDeleteAllDialog1 by remember { mutableStateOf(false) }
    var showDeleteAllDialog2 by remember { mutableStateOf(false) }
    var wordToDelete by remember { mutableStateOf<Word?>(null) }

    // Tick every second for live countdowns
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = System.currentTimeMillis()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "WordForge",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToHowItWorks) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                            contentDescription = "How it works"
                        )
                    }
                    if (words.isNotEmpty()) {
                        IconButton(onClick = { showDeleteAllDialog1 = true }) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteSweep,
                                contentDescription = "Delete all words"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddWord,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = "Add word",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        if (words.isEmpty()) {
            EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                items(words, key = { it.id }) { word ->
                    WordCard(
                        word = word,
                        now = now,
                        onClick = { onNavigateToDetail(word.id) },
                        onDelete = { wordToDelete = word }
                    )
                }

                item { Spacer(modifier = Modifier.height(96.dp)) }
            }
        }
    }

    // Single word delete confirmation
    wordToDelete?.let { word ->
        AlertDialog(
            onDismissRequest = { wordToDelete = null },
            title = { Text("Delete word") },
            text = { Text("Delete \"${word.word}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteWord(word)
                    wordToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { wordToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete all — first confirmation
    if (showDeleteAllDialog1) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog1 = false },
            title = { Text("Delete all words") },
            text = { Text("Are you sure you want to delete all ${words.size} words?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAllDialog1 = false
                    showDeleteAllDialog2 = true
                }) {
                    Text("Yes, delete all", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog1 = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete all — second confirmation
    if (showDeleteAllDialog2) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog2 = false },
            title = { Text("Are you really sure?") },
            text = { Text("This will permanently delete all your words and progress. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAllWords()
                    showDeleteAllDialog2 = false
                }) {
                    Text("Delete everything", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog2 = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Decorative forge scene: ember halo + flame, with floating sparks
            Box(modifier = Modifier.size(180.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(76.dp)
                )
                // Spark — large, top right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 8.dp, top = 12.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary)
                )
                // Spark — medium, top left
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 18.dp, top = 32.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.75f))
                )
                // Spark — small, bottom right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 22.dp, bottom = 28.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your forge is empty",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add a word to start hammering it into long-term memory.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WordCard(
    word: Word,
    now: Long,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val tierColor = TierColors.getOrElse(word.currentTier) { TierColors.last() }
    val isOverdue = word.nextPromptAt <= now

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tier-color edge stripe — instant visual signal of progress
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(tierColor)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tier badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(tierColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${word.currentTier}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tierColor
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Word text and countdown
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCountdown(word.nextPromptAt, now),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isOverdue) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isOverdue)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Delete button
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete word",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

/**
 * Formats the time remaining until the next prompt.
 * Shows "Overdue by Xh Ym" if past due, or "Xh Ym Zs left" if upcoming.
 */
fun formatCountdown(nextPromptAt: Long, now: Long): String {
    val diff = nextPromptAt - now

    if (diff <= 0) {
        val overdue = -diff
        return "Overdue by ${formatDuration(overdue)}"
    }
    return "${formatDuration(diff)} left"
}

fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val days = totalSeconds / 86400
    val hours = (totalSeconds % 86400) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        days > 0 -> "${days}d ${hours}h ${minutes}m"
        hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
        minutes > 0 -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}
