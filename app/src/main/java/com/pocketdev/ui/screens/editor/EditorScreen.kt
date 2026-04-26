package com.pocketdev.ui.screens.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pocketdev.ui.screens.editor.components.CodeEditor
import com.pocketdev.ui.screens.editor.components.EditorTabs
import com.pocketdev.ui.screens.editor.components.EditorToolbar
import com.pocketdev.ui.screens.editor.components.FileTree
import com.pocketdev.ui.screens.editor.components.SearchReplaceBar

@Composable
fun EditorScreen(
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { viewModel.onEvent(EditorEvent.OpenFolder(it)) }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(EditorEvent.ClearError)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            EditorToolbar(
                folderName = uiState.folderName,
                hasUnsavedChanges = uiState.hasUnsavedChanges,
                fontSize = uiState.fontSize,
                autoSaveEnabled = uiState.autoSaveEnabled,
                onOpenFolder = { folderPickerLauncher.launch(null) },
                onSave = { viewModel.onEvent(EditorEvent.SaveAllFiles) },
                onUndo = {
                    uiState.activeTab?.let {
                        viewModel.onEvent(EditorEvent.Undo(it.id))
                    }
                },
                onRedo = {
                    uiState.activeTab?.let {
                        viewModel.onEvent(EditorEvent.Redo(it.id))
                    }
                },
                onSearch = { viewModel.onEvent(EditorEvent.ShowSearch()) },
                onZoomIn = { viewModel.onEvent(EditorEvent.ZoomIn()) },
                onZoomOut = { viewModel.onEvent(EditorEvent.ZoomOut()) },
                onToggleFileTree = { viewModel.onEvent(EditorEvent.ToggleFileTree) },
                onToggleAutoSave = { viewModel.onEvent(EditorEvent.ToggleAutoSave(it)) },
                hasActiveTab = uiState.activeTab != null
            )

            AnimatedVisibility(
                visible = uiState.isSearchVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                SearchReplaceBar(
                    searchQuery = uiState.searchQuery,
                    replaceText = uiState.replaceText,
                    onSearchQueryChange = { viewModel.onEvent(EditorEvent.UpdateSearchQuery(it)) },
                    onReplaceTextChange = { viewModel.onEvent(EditorEvent.UpdateReplaceText(it)) },
                    onFindNext = { viewModel.onEvent(EditorEvent.FindNext) },
                    onReplaceOne = { viewModel.onEvent(EditorEvent.ReplaceOne) },
                    onReplaceAll = { viewModel.onEvent(EditorEvent.ReplaceAll) },
                    onClose = { viewModel.onEvent(EditorEvent.HideSearch) }
                )
            }

            if (uiState.openTabs.isNotEmpty()) {
                EditorTabs(
                    tabs = uiState.openTabs,
                    activeTabIndex = uiState.activeTabIndex,
                    onTabSelect = { viewModel.onEvent(EditorEvent.SetActiveTab(it)) },
                    onTabClose = { viewModel.onEvent(EditorEvent.CloseTab(it)) }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                AnimatedVisibility(visible = uiState.isFileTreeVisible) {
                    FileTree(
                        files = uiState.fileTree,
                        folderName = uiState.folderName,
                        onFileClick = { viewModel.onEvent(EditorEvent.SelectFile(it)) },
                        onBackClick = if (uiState.currentFolderUri != null) {
                            { viewModel.onEvent(EditorEvent.NavigateBack) }
                        } else null,
                        isLoading = uiState.isLoading,
                        modifier = Modifier
                            .width(240.dp)
                            .fillMaxHeight()
                    )
                }

                if (uiState.isFileTreeVisible && uiState.openTabs.isNotEmpty()) {
                    VerticalDivider()
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    val activeTab = uiState.activeTab
                    if (activeTab != null) {
                        CodeEditor(
                            content = activeTab.content,
                            language = activeTab.language,
                            fontSize = uiState.fontSize,
                            onContentChange = { viewModel.onEvent(EditorEvent.UpdateContent(activeTab.id, it)) },
                            onCursorChange = { line, col ->
                                viewModel.onEvent(EditorEvent.UpdateCursor(activeTab.id, line, col))
                            }
                        )
                    } else {
                        EmptyEditorState(
                            onOpenFolder = { folderPickerLauncher.launch(null) }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.isAiAssistVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                AiAssistPanel(
                    selectedText = uiState.selectedText,
                    response = uiState.aiAssistResponse,
                    isLoading = uiState.isAiLoading,
                    onSendToAi = { viewModel.onEvent(EditorEvent.SendToAi(uiState.selectedText)) },
                    onInsertResponse = { viewModel.onEvent(EditorEvent.InsertAiResponse) },
                    onClose = { viewModel.onEvent(EditorEvent.HideAiAssist) }
                )
            }
        }
    }
}

@Composable
private fun EmptyEditorState(
    onOpenFolder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "No file open",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Open a folder to start browsing files",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onOpenFolder) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Open Folder")
            }
        }
    }
}

@Composable
private fun AiAssistPanel(
    selectedText: String,
    response: String,
    isLoading: Boolean,
    onSendToAi: () -> Unit,
    onInsertResponse: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Assist",
                    style = MaterialTheme.typography.titleSmall
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Close"
                    )
                }
            }

            if (selectedText.isNotEmpty()) {
                OutlinedTextField(
                    value = selectedText,
                    onValueChange = {},
                    label = { Text("Selected Code") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onSendToAi,
                    enabled = !isLoading && selectedText.isNotEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(16.dp)
                                .width(16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    Text("Ask AI")
                }
            }

            if (response.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = response,
                    onValueChange = {},
                    label = { Text("AI Response") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onInsertResponse) {
                    Text("Insert at Cursor")
                }
            }
        }
    }
}
