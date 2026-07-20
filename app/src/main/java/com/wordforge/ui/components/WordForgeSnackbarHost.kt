package com.wordforge.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wordforge.ui.theme.LocalWordForgeColors

// Snackbar host styled to sit on the cream palette: dark warm Ink
// container with cream-white text, primary ember for the action label,
// and a rounded shape that matches the rest of the cards.
@Composable
fun WordForgeSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val wordForgeColors = LocalWordForgeColors.current

    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = wordForgeColors.snackbarContainer,
            contentColor = wordForgeColors.snackbarContent,
            actionColor = MaterialTheme.colorScheme.primary,
            actionContentColor = MaterialTheme.colorScheme.primary,
            dismissActionContentColor = wordForgeColors.snackbarContent,
            shape = RoundedCornerShape(16.dp),
        )
    }
}
