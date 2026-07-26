package com.wordforge.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wordforge.data.Word
import com.wordforge.data.toDraft
import com.wordforge.data.withContent
import com.wordforge.viewmodel.WordViewModel

@Composable
fun EditWordScreen(
    wordId: String,
    viewModel: WordViewModel,
    onNavigateBack: () -> Unit
) {
    var word by remember { mutableStateOf<Word?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(wordId) {
        word = viewModel.getWordById(wordId)
        isLoading = false
    }

    // Only compose the form once the item has loaded, so the form
    // captures the real initial values on its first composition.
    val current = word
    if (current == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        return
    }

    LearningItemFormScaffold(
        topBarLabel = "EDIT ITEM",
        headline = "Refine this item",
        subtitle = "Update its content or how it appears during review.",
        submitLabel = "Save changes",
        initialDraft = current.toDraft(),
        allowTypeSelection = false,
        onNavigateBack = onNavigateBack,
        onSubmit = { draft ->
            viewModel.updateWord(current.withContent(draft))
            onNavigateBack()
        },
    )
}
