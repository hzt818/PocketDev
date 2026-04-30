package com.pocketdev.ui.screens.editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pocketdev.R

@Composable
fun EditorToolbar(
    folderName: String,
    hasUnsavedChanges: Boolean,
    fontSize: Float,
    autoSaveEnabled: Boolean,
    onOpenFolder: () -> Unit,
    onSave: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSearch: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onToggleFileTree: () -> Unit,
    onToggleAutoSave: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hasActiveTab: Boolean = false
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(onClick = onToggleFileTree) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.editor_toggle_filetree),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = onOpenFolder) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = stringResource(R.string.editor_open_folder_tooltip),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            if (folderName.isNotEmpty()) {
                Text(
                    text = folderName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            if (hasUnsavedChanges) {
                Text(
                    text = "*",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasActiveTab) {
                IconButton(onClick = onUndo) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = stringResource(R.string.editor_undo),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = onRedo) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = stringResource(R.string.editor_redo),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = onSearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.editor_search),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = onSave) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(R.string.editor_save),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            IconButton(onClick = onZoomOut) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = stringResource(R.string.editor_zoom_out),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "${fontSize.toInt()}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            IconButton(onClick = onZoomIn) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = stringResource(R.string.editor_zoom_in),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.editor_more_options)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.editor_auto_save))
                            Text(
                                text = if (autoSaveEnabled) stringResource(R.string.editor_on) else stringResource(R.string.editor_off),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    onClick = { onToggleAutoSave(!autoSaveEnabled) }
                )
            }
        }
    }
}
