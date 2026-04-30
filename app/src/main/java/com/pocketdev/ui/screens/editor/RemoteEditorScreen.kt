package com.pocketdev.ui.screens.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pocketdev.ui.components.CommitDialog
import com.pocketdev.ui.screens.editor.components.CodeEditor
import com.pocketdev.ui.screens.editor.components.SearchReplaceBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteEditorScreen(
    repoFullName: String,
    branch: String,
    filePath: String,
    fileSha: String,
    onNavigateBack: () -> Unit,
    viewModel: RemoteEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(RemoteEditorEvent.ClearError)
        }
    }

    LaunchedEffect(uiState.showSaveSuccess) {
        if (uiState.showSaveSuccess) {
            snackbarHostState.showSnackbar("File saved successfully")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.activeTab?.fileName ?: filePath.substringAfterLast("/"),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$repoFullName ($branch)",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.isModified) {
                        IconButton(onClick = { viewModel.onEvent(RemoteEditorEvent.SaveFile) }) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    }

                    IconButton(onClick = { viewModel.onEvent(RemoteEditorEvent.ShowSearch) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }

                    IconButton(onClick = { viewModel.onEvent(RemoteEditorEvent.ZoomIn(1f)) }) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in")
                    }

                    IconButton(onClick = { viewModel.onEvent(RemoteEditorEvent.ZoomOut(1f)) }) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.activeTab != null -> {
                    val tab = uiState.activeTab!!
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (uiState.isSearchVisible) {
                            SearchReplaceBar(
                                searchQuery = uiState.searchQuery,
                                replaceText = uiState.replaceText,
                                onSearchQueryChange = { viewModel.onEvent(RemoteEditorEvent.UpdateSearchQuery(it)) },
                                onReplaceTextChange = { viewModel.onEvent(RemoteEditorEvent.UpdateReplaceText(it)) },
                                onFindNext = { viewModel.onEvent(RemoteEditorEvent.FindNext) },
                                onReplaceOne = { viewModel.onEvent(RemoteEditorEvent.ReplaceOne) },
                                onReplaceAll = { viewModel.onEvent(RemoteEditorEvent.ReplaceAll) },
                                onClose = { viewModel.onEvent(RemoteEditorEvent.HideSearch) }
                            )
                        }

                        CodeEditor(
                            content = tab.content,
                            language = tab.language,
                            fontSize = uiState.fontSize,
                            onContentChange = { viewModel.onEvent(RemoteEditorEvent.UpdateContent(it)) },
                            onCursorChange = { line, col ->
                                viewModel.onEvent(RemoteEditorEvent.UpdateCursor(line, col))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }

                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                else -> {
                    Text(
                        text = "Failed to load file",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (uiState.showCommitDialog) {
            CommitDialog(
                commitMessage = uiState.commitMessage,
                onCommitMessageChange = { viewModel.onEvent(RemoteEditorEvent.UpdateCommitMessage(it)) },
                onConfirm = { viewModel.onEvent(RemoteEditorEvent.CommitFile) },
                onDismiss = { viewModel.onEvent(RemoteEditorEvent.HideCommitDialog) }
            )
        }
    }
}