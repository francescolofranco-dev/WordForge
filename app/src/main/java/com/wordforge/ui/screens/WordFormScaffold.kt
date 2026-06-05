package com.wordforge.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wordforge.ui.components.WordForgeSnackbarHost
import kotlinx.coroutines.launch

/**
 * Shared form behind both [AddWordScreen] and [EditWordScreen]. The header
 * copy, button label, and post-submit behavior are supplied by the caller so
 * the two screens stay visually identical and differ only in wording.
 */
@Composable
fun WordFormScaffold(
    topBarLabel: String,
    headline: String,
    subtitle: String,
    submitLabel: String,
    onSubmit: (word: String, meaning: String, randomlyFlip: Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    initialWord: String = "",
    initialMeaning: String = "",
    initialRandomlyFlip: Boolean = true,
    clearAfterSubmit: Boolean = false,
    successMessage: String? = null,
) {
    var word by remember { mutableStateOf(initialWord) }
    var meaning by remember { mutableStateOf(initialMeaning) }
    var randomlyFlip by remember { mutableStateOf(initialRandomlyFlip) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isValid = word.isNotBlank() && meaning.isNotBlank()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = topBarLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        snackbarHost = { WordForgeSnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = headline,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(36.dp))

            FieldLabel("WORD OR PHRASE")
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = word,
                onValueChange = { word = it },
                placeholder = { Text("e.g. petrichor") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = filledFieldColors(),
            )

            Spacer(modifier = Modifier.height(28.dp))

            FieldLabel("MEANING")
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = meaning,
                onValueChange = { meaning = it },
                placeholder = { Text("Define it in your own words for stronger recall.") },
                minLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = filledFieldColors(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tip — paraphrasing in your own words builds stronger memory than copy-pasting a definition.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(28.dp))

            RandomlyFlipToggle(
                checked = randomlyFlip,
                onCheckedChange = { randomlyFlip = it },
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    if (!isValid) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please fill in both fields")
                        }
                    } else {
                        onSubmit(word.trim(), meaning.trim(), randomlyFlip)
                        if (clearAfterSubmit) {
                            word = ""
                            meaning = ""
                            randomlyFlip = initialRandomlyFlip
                        }
                        if (successMessage != null) {
                            scope.launch {
                                snackbarHostState.showSnackbar(successMessage)
                            }
                        }
                    }
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
            ) {
                Text(
                    text = submitLabel,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RandomlyFlipToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Randomly flip",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Sometimes quiz in reverse by showing the meaning and asking for the word.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun filledFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    cursorColor = MaterialTheme.colorScheme.primary,
)
