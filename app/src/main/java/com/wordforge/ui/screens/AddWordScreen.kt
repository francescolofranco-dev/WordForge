package com.wordforge.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AddWordScreen(
    onAddWord: (String, String, Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    existingWords: List<String>,
    shouldOfferNotifications: Boolean,
    onNotificationEducationShown: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
    var showNotificationEducation by remember { mutableStateOf(false) }

    WordFormScaffold(
        topBarLabel = "NEW WORD",
        headline = "What do you want to learn?",
        subtitle = "A single word, phrase, or expression in any language.",
        submitLabel = "Add to forge",
        clearAfterSubmit = true,
        successMessage = "Word added!",
        autoFocus = true,
        onNavigateBack = onNavigateBack,
        onSubmit = { word, meaning, randomlyFlip ->
            onAddWord(word, meaning, randomlyFlip)
            if (shouldOfferNotifications) {
                onNotificationEducationShown()
                showNotificationEducation = true
            }
        },
        wordWarning = { candidate ->
            if (existingWords.any { it.equals(candidate.trim(), ignoreCase = true) }) {
                "This word is already in your forge"
            } else {
                null
            }
        },
    )

    if (showNotificationEducation) {
        AlertDialog(
            onDismissRequest = { showNotificationEducation = false },
            title = { Text("Know when a word is ready") },
            text = {
                Text("WordForge can send one quiet summary when reviews are ready. You can change this anytime in Android settings.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNotificationEducation = false
                        onRequestNotificationPermission()
                    }
                ) {
                    Text("Allow reminders")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationEducation = false }) {
                    Text("Not now")
                }
            },
        )
    }
}
