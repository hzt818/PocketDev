package com.pocketdev.ui.screens.editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchReplaceBar(
    searchQuery: String,
    replaceText: String,
    onSearchQueryChange: (String) -> Unit,
    onReplaceTextChange: (String) -> Unit,
    onFindNext: () -> Unit,
    onReplaceOne: () -> Unit,
    onReplaceAll: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    matchCount: Int = 0
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Find",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search...") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall
                )

                IconButton(onClick = onFindNext) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Find next"
                    )
                }

                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            if (matchCount > 0) {
                Text(
                    text = "$matchCount matches",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 40.dp, top = 2.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Replace",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )

                OutlinedTextField(
                    value = replaceText,
                    onValueChange = onReplaceTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Replace with...") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall
                )

                TextButton(
                    onClick = onReplaceOne,
                    enabled = searchQuery.isNotEmpty(),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text("One")
                }

                TextButton(
                    onClick = onReplaceAll,
                    enabled = searchQuery.isNotEmpty()
                ) {
                    Text("All")
                }
            }
        }
    }
}
