package com.wordforge.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wordforge.R
import com.wordforge.data.Word
import com.wordforge.ui.components.SparksLogo
import com.wordforge.ui.components.WordCard
import com.wordforge.ui.components.WordForgeFab
import com.wordforge.viewmodel.WordViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListScreen(
    viewModel: WordViewModel,
    onNavigateToAddWord: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHowItWorks: () -> Unit
) {
    val words by viewModel.allWords.collectAsState()

    var showDeleteAllDialog1 by remember { mutableStateOf(false) }
    var showDeleteAllDialog2 by remember { mutableStateOf(false) }
    var wordToDelete by remember { mutableStateOf<Word?>(null) }
    var menuOpen by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            try {
                val json = viewModel.exportToJson()
                context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                snackbarHostState.showSnackbar("Exported ${words.size} words")
            } catch (t: Throwable) {
                snackbarHostState.showSnackbar("Export failed: ${t.message ?: "unknown error"}")
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            try {
                val json = context.contentResolver.openInputStream(uri)
                    ?.bufferedReader()?.use { it.readText() }
                    ?: return@launch
                val count = viewModel.importFromJson(json)
                snackbarHostState.showSnackbar("Imported $count words")
            } catch (t: Throwable) {
                snackbarHostState.showSnackbar("Import failed: ${t.message ?: "unknown error"}")
            }
        }
    }

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = System.currentTimeMillis()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "WordForge",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    SparksLogo(
                        size = 36.dp,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToHowItWorks) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                            contentDescription = "How it works"
                        )
                    }
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
                                text = { Text("Export words") },
                                leadingIcon = {
                                    Icon(Icons.Rounded.SaveAlt, contentDescription = null)
                                },
                                onClick = {
                                    menuOpen = false
                                    exportLauncher.launch("wordforge-export.json")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Import words") },
                                leadingIcon = {
                                    Icon(Icons.Rounded.FolderOpen, contentDescription = null)
                                },
                                onClick = {
                                    menuOpen = false
                                    importLauncher.launch(arrayOf("application/json"))
                                }
                            )
                            if (words.isNotEmpty()) {
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Delete all words",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Rounded.DeleteSweep,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {
                                        menuOpen = false
                                        showDeleteAllDialog1 = true
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            WordForgeFab(onClick = onNavigateToAddWord)
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
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        text = "Due soon",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                    )
                }

                items(words, key = { it.id }) { word ->
                    WordCard(
                        word = word.word,
                        meaning = word.meaning,
                        tier = word.currentTier,
                        dueLabel = formatCompactDue(word.nextPromptAt, now),
                        onClick = { onNavigateToDetail(word.id) },
                        onLongClick = { wordToDelete = word },
                    )
                }

                item { Spacer(modifier = Modifier.height(96.dp)) }
            }
        }
    }

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
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sparks),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(92.dp),
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Your forge is ready",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Add your first word to start hammering it into long-term memory.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 280.dp),
            )
        }
    }
}

// Compact mono-style due label: "OVERDUE", "2D 14H", "5H 23M",
// "12M 04S", "37S". Two units max for legibility at small sizes.
fun formatCompactDue(nextPromptAt: Long, now: Long): String {
    val diff = nextPromptAt - now
    if (diff <= 0L) return "OVERDUE"

    val totalSeconds = diff / 1000
    val days = totalSeconds / 86400
    val hours = (totalSeconds % 86400) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        days > 0 -> "${days}D ${hours}H"
        hours > 0 -> "${hours}H ${minutes}M"
        minutes > 0 -> "${minutes}M ${seconds.toString().padStart(2, '0')}S"
        else -> "${seconds}S"
    }
}
