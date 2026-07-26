package com.wordforge.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordforge.ui.theme.WordForgeTheme

@Composable
fun WordForgeFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Add item",
    expanded: Boolean = true,
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
        expanded = expanded,
        icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
        text = { Text(label, style = MaterialTheme.typography.titleLarge) },
    )
}

@Preview
@Composable
private fun WordForgeFabPreview() {
    WordForgeTheme {
        WordForgeFab(onClick = {})
    }
}
