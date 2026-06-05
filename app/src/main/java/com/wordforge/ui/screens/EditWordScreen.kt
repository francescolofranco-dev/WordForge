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

    // Only compose the form once the word has loaded, so WordFormScaffold
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

    WordFormScaffold(
        topBarLabel = "EDIT WORD",
        headline = "Refine this word",
        subtitle = "Update the word, its meaning, or how it shows up in quizzes.",
        submitLabel = "Save changes",
        initialWord = current.word,
        initialMeaning = current.meaning,
        initialRandomlyFlip = current.randomlyFlip,
        onNavigateBack = onNavigateBack,
        onSubmit = { newWord, newMeaning, newFlip ->
            viewModel.updateWord(
                current.copy(
                    word = newWord,
                    meaning = newMeaning,
                    randomlyFlip = newFlip,
                )
            )
            onNavigateBack()
        },
    )
}
