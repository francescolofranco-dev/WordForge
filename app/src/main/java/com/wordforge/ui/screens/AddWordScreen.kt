package com.wordforge.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
    var showNotificationEducation by rememberSaveable { mutableStateOf(false) }
    var addedCount by rememberSaveable { mutableIntStateOf(0) }

    fun finishAdding() {
        if (shouldOfferNotifications && addedCount > 0) {
            showNotificationEducation = true
        } else {
            onNavigateBack()
        }
    }

    fun finishNotificationEducation(requestPermission: Boolean) {
        showNotificationEducation = false
        onNotificationEducationShown()
        if (requestPermission) onRequestNotificationPermission()
        onNavigateBack()
    }

    QuickAddItemForm(
        onNavigateBack = ::finishAdding,
        onSubmit = { draft ->
            onAddItem(draft)
            addedCount += 1
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
            onDismissRequest = { finishNotificationEducation(requestPermission = false) },
            title = { Text("Know when an item is ready") },
            text = {
                Text("WordForge can send one grouped reminder for everything ready to review. You can choose how often from the app menu.")
            },
            confirmButton = {
                TextButton(
                    onClick = { finishNotificationEducation(requestPermission = true) }
                ) {
                    Text("Allow reminders")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { finishNotificationEducation(requestPermission = false) }
                ) {
                    Text("Not now")
                }
            },
        )
    }
}
