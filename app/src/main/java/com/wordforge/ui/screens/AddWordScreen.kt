package com.wordforge.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.wordforge.data.LearningItemDraft
import com.wordforge.data.LearningItemType
import com.wordforge.data.Word

@Composable
fun AddWordScreen(
    onAddItem: (LearningItemDraft) -> Unit,
    onNavigateBack: () -> Unit,
    existingItems: List<Word>,
    shouldOfferNotifications: Boolean,
    onNotificationEducationShown: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
    var showNotificationEducation by remember { mutableStateOf(false) }

    LearningItemFormScaffold(
        topBarLabel = "NEW ITEM",
        headline = "What do you want to learn?",
        subtitle = "Add a word or practise one complete Spanish verb tense.",
        submitLabel = "Add to forge",
        clearAfterSubmit = true,
        successMessage = "Item added!",
        autoFocus = true,
        onNavigateBack = onNavigateBack,
        onSubmit = { draft ->
            onAddItem(draft)
            if (shouldOfferNotifications) {
                onNotificationEducationShown()
                showNotificationEducation = true
            }
        },
        itemWarning = { candidate ->
            val duplicate = existingItems.any { existing ->
                existing.itemType == candidate.type &&
                    existing.word.equals(candidate.term.trim(), ignoreCase = true) &&
                    (
                        candidate.type != LearningItemType.VERB_CONJUGATION ||
                            existing.verbConjugation?.tense.equals(
                                candidate.verbConjugation?.tense?.trim(),
                                ignoreCase = true,
                            )
                        )
            }
            if (duplicate) {
                if (candidate.type == LearningItemType.VERB_CONJUGATION) {
                    "This verb and tense are already in your forge"
                } else {
                    "This word is already in your forge"
                }
            } else {
                null
            }
        },
    )

    if (showNotificationEducation) {
        AlertDialog(
            onDismissRequest = { showNotificationEducation = false },
            title = { Text("Know when an item is ready") },
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
