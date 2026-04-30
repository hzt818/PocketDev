package com.pocketdev.ui.screens.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pocketdev.R
import com.pocketdev.domain.model.EditorPreferences

@Composable
fun EditorSettingsCard(
    preferences: EditorPreferences,
    onFontSizeChange: (Int) -> Unit,
    onTabSizeChange: (Int) -> Unit,
    onShowLineNumbersChange: (Boolean) -> Unit,
    onWordWrapChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Font Size
            Text(
                text = stringResource(R.string.editor_font_size, preferences.fontSize),
                style = MaterialTheme.typography.bodyLarge
            )
            Slider(
                value = preferences.fontSize.toFloat(),
                onValueChange = { onFontSizeChange(it.toInt()) },
                valueRange = 10f..24f,
                steps = 13,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Size
            Text(
                text = stringResource(R.string.editor_tab_size, preferences.tabSize),
                style = MaterialTheme.typography.bodyLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(2, 4, 8).forEach { size ->
                    FilterChip(
                        selected = preferences.tabSize == size,
                        onClick = { onTabSizeChange(size) },
                        label = { Text("$size") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show Line Numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.editor_show_line_numbers),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = preferences.showLineNumbers,
                    onCheckedChange = onShowLineNumbersChange
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Word Wrap
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.editor_word_wrap),
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = preferences.wordWrap,
                    onCheckedChange = onWordWrapChange
                )
            }
        }
    }
}
