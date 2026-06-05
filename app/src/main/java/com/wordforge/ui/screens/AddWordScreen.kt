package com.wordforge.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun AddWordScreen(
    onAddWord: (String, String, Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    WordFormScaffold(
        topBarLabel = "NEW WORD",
        headline = "What do you want to learn?",
        subtitle = "A single word, phrase, or expression in any language.",
        submitLabel = "Add to forge",
        clearAfterSubmit = true,
        successMessage = "Word added!",
        onNavigateBack = onNavigateBack,
        onSubmit = { word, meaning, randomlyFlip ->
            onAddWord(word, meaning, randomlyFlip)
        },
    )
}
