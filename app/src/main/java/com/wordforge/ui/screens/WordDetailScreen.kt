package com.wordforge.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.wordforge.data.Word
import com.wordforge.ui.components.StatTile
import com.wordforge.ui.components.StreakCard
import com.wordforge.ui.components.TierIndicator
import com.wordforge.ui.components.WordForgeSnackbarHost
import com.wordforge.ui.theme.LocalWordForgeColors
import com.wordforge.viewmodel.WordViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import java.util.Date
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordforge.data.LearningItemType
import com.wordforge.data.VerbConjugation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    wordId: String,
    viewModel: WordViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    var word by remember { mutableStateOf<Word?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var meaningRevealed by rememberSaveable { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val wordForgeColors = LocalWordForgeColors.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(word?.nextPromptAt) {
        while (true) {
            val untilDue = (word?.nextPromptAt ?: Long.MAX_VALUE) - System.currentTimeMillis()
            delay(if (untilDue <= 60 * 60 * 1000L) 1000L else 30_000L)
            now = System.currentTimeMillis()
        }
    }

    LaunchedEffect(wordId) {
        word = viewModel.getWordById(wordId)
        isLoading = false
    }

    // Keep the displayed word in sync with edits made on the edit screen:
    // when the live list changes, refresh our copy. find returns null for a
    // just-deleted word, so the ?.let leaves the screen untouched mid-delete.
    val liveWords by viewModel.allWords.collectAsStateWithLifecycle()
    LaunchedEffect(liveWords) {
        liveWords.find { it.id == wordId }?.let { word = it }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { WordForgeSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    if (word != null) {
                        Box {
                            IconButton(onClick = { menuOpen = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = "More"
                                )
                            }
                            DropdownMenu(
                                expanded = menuOpen,
                                onDismissRequest = { menuOpen = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit item") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Rounded.Edit,
                                            contentDescription = null,
                                        )
                                    },
                                    onClick = {
                                        menuOpen = false
                                        onNavigateToEdit(wordId)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Delete item",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Rounded.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {
                                        menuOpen = false
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        },
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
                        text = "Item not found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    val currentWord = word!!
                    val dateFormat = DateFormat.getMediumDateFormat(context)
                    val timeFormat = DateFormat.getTimeFormat(context)
                    val isOverdue = currentWord.nextPromptAt <= now

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                    ) {
                        TopLabelsRow(
                            tier = currentWord.currentTier,
                            nextPromptAt = currentWord.nextPromptAt,
                            now = now,
                            isOverdue = isOverdue,
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = currentWord.word,
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        if (currentWord.itemType == LearningItemType.VERB_CONJUGATION) {
                            Spacer(modifier = Modifier.height(10.dp))
                            VerbTypeBadge(
                                tense = currentWord.verbConjugation?.tense.orEmpty()
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        TierIndicator(tier = currentWord.currentTier)

                        Spacer(modifier = Modifier.height(28.dp))

                        when (currentWord.itemType) {
                            LearningItemType.SIMPLE_WORD -> {
                                RevealCard(
                                    meaning = currentWord.meaning,
                                    revealed = meaningRevealed,
                                    onToggle = { meaningRevealed = !meaningRevealed },
                                )
                            }
                            LearningItemType.VERB_CONJUGATION -> {
                                currentWord.verbConjugation?.let { conjugation ->
                                    VerbConjugationRevealCard(
                                        conjugation = conjugation,
                                        revealed = meaningRevealed,
                                        onToggle = { meaningRevealed = !meaningRevealed },
                                    )
                                }
                            }
                        }

                        if (isOverdue) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ReviewNowButton(onClick = { onNavigateToQuiz(currentWord.id) })
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            StatTile(
                                label = "Correct",
                                value = currentWord.totalCorrect.toString(),
                                icon = Icons.Rounded.Check,
                                iconTint = wordForgeColors.correct,
                                container = wordForgeColors.correctContainer,
                                modifier = Modifier.weight(1f),
                            )
                            StatTile(
                                label = "Incorrect",
                                value = currentWord.totalIncorrect.toString(),
                                icon = Icons.Rounded.Close,
                                iconTint = wordForgeColors.incorrect,
                                container = wordForgeColors.incorrectContainer,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        if (currentWord.currentStreak > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            StreakCard(streak = currentWord.currentStreak)
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Added",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "${dateFormat.format(Date(currentWord.createdAt))} · " +
                                    timeFormat.format(Date(currentWord.createdAt)),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        val current = word
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete item") },
            text = {
                Text("Delete \"${current?.word ?: ""}\"?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    current?.let {
                        viewModel.deleteWord(it)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "\"${it.word}\" deleted",
                                actionLabel = "Undo",
                                withDismissAction = true,
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.restoreWord(it)
                            } else {
                                onNavigateBack()
                            }
                        }
                    }
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun TopLabelsRow(
    tier: Int,
    nextPromptAt: Long,
    now: Long,
    isOverdue: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "TIER $tier OF 8",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.W600,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = if (isOverdue) "READY" else "NEXT · ${formatCompactDue(nextPromptAt, now)}",
            style = MaterialTheme.typography.labelMedium,
            color = if (isOverdue) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RevealCard(
    meaning: String,
    revealed: Boolean,
    onToggle: () -> Unit,
) {
    Surface(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (revealed) Icons.Rounded.VisibilityOff
                                  else Icons.Rounded.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                if (revealed) {
                    Text(
                        text = meaning,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to hide.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                } else {
                    Text(
                        text = "Tap to reveal meaning",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Recall first, then check.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@Composable
private fun VerbTypeBadge(tense: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Text(
            text = tense.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        )
    }
}

@Composable
private fun VerbConjugationRevealCard(
    conjugation: VerbConjugation,
    revealed: Boolean,
    onToggle: () -> Unit,
) {
    Surface(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (revealed) {
                            Icons.Rounded.VisibilityOff
                        } else {
                            Icons.Rounded.Visibility
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (revealed) {
                            "Conjugation"
                        } else {
                            "Tap to reveal conjugation"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (revealed) "Tap to hide." else conjugation.tense,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
            }

            if (revealed) {
                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                )
                conjugation.rows().forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = row.person,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = row.form,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
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
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Review now",
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
