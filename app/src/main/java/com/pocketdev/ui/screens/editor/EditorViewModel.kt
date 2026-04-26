package com.pocketdev.ui.screens.editor

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.EditorTab
import com.pocketdev.domain.model.FileItem
import com.pocketdev.domain.model.FileType
import com.pocketdev.domain.repository.FileRepository
import com.pocketdev.domain.usecase.ListFilesUseCase
import com.pocketdev.domain.usecase.OpenFolderUseCase
import com.pocketdev.domain.usecase.ReadFileUseCase
import com.pocketdev.domain.usecase.SaveFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val openFolderUseCase: OpenFolderUseCase,
    private val listFilesUseCase: ListFilesUseCase,
    private val readFileUseCase: ReadFileUseCase,
    private val saveFileUseCase: SaveFileUseCase,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val undoStacks = mutableMapOf<String, List<EditorTab>>()
    private val redoStacks = mutableMapOf<String, List<EditorTab>>()
    private val fileUris = mutableMapOf<String, Uri>()

    private var autoSaveJob: Job? = null
    private val autoSaveDelay = 2000L

    fun onEvent(event: EditorEvent) {
        when (event) {
            is EditorEvent.OpenFolder -> openFolder(event.uri)
            EditorEvent.OpenFolderPicker -> _uiState.update { it.copy(showFolderPicker = true) }
            EditorEvent.CloseFolderPicker -> _uiState.update { it.copy(showFolderPicker = false) }
            is EditorEvent.SelectFile -> selectFile(event.file)
            is EditorEvent.NavigateToFolder -> navigateToFolder(event.folder)
            EditorEvent.NavigateBack -> navigateBack()
            is EditorEvent.CloseTab -> closeTab(event.tabId)
            is EditorEvent.SetActiveTab -> setActiveTab(event.index)
            is EditorEvent.UpdateContent -> updateContent(event.tabId, event.content)
            is EditorEvent.SaveFile -> saveFile(event.tabId)
            EditorEvent.SaveAllFiles -> saveAllFiles()
            is EditorEvent.UpdateCursor -> updateCursor(event.tabId, event.line, event.column)
            is EditorEvent.ShowSearch -> showSearch(event.query)
            EditorEvent.HideSearch -> hideSearch()
            is EditorEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            is EditorEvent.UpdateReplaceText -> updateReplaceText(event.text)
            EditorEvent.FindNext -> findNext()
            EditorEvent.ReplaceOne -> replaceOne()
            EditorEvent.ReplaceAll -> replaceAll()
            is EditorEvent.Undo -> undo(event.tabId)
            is EditorEvent.Redo -> redo(event.tabId)
            EditorEvent.ClearError -> _uiState.update { it.copy(error = null) }
            is EditorEvent.ZoomIn -> zoomIn(event.step)
            is EditorEvent.ZoomOut -> zoomOut(event.step)
            EditorEvent.ToggleFileTree -> toggleFileTree()
            is EditorEvent.SelectText -> selectText(event.text)
            EditorEvent.ShowAiAssist -> showAiAssist()
            EditorEvent.HideAiAssist -> hideAiAssist()
            is EditorEvent.SendToAi -> sendToAi(event.code)
            EditorEvent.InsertAiResponse -> insertAiResponse()
            is EditorEvent.ToggleAutoSave -> toggleAutoSave(event.enabled)
        }
    }

    private fun openFolder(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showFolderPicker = false) }
            openFolderUseCase(uri).fold(
                onSuccess = { files ->
                    _uiState.update {
                        it.copy(
                            currentFolderUri = uri,
                            folderName = getFolderName(uri),
                            fileTree = files,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message, isLoading = false)
                    }
                }
            )
        }
    }

    private fun selectFile(file: FileItem) {
        if (file.isDirectory) {
            navigateToFolder(file)
        } else {
            val existingTab = _uiState.value.openTabs.find {
                it.filePath == file.path
            }
            if (existingTab != null) {
                val index = _uiState.value.openTabs.indexOf(existingTab)
                setActiveTab(index)
            } else {
                openFile(file)
            }
        }
    }

    private fun openFile(file: FileItem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            readFileUseCase(file.uri).fold(
                onSuccess = { content ->
                    val language = FileType.fromFileName(file.name)
                    val newTab = EditorTab.create(
                        fileName = file.name,
                        filePath = file.path,
                        content = content,
                        language = language
                    )
                    fileUris[newTab.id] = file.uri

                    _uiState.update { state ->
                        state.copy(
                            openTabs = state.openTabs + newTab,
                            activeTabIndex = state.openTabs.size,
                            isLoading = false,
                            currentFileUri = file.uri
                        )
                    }
                    undoStacks[newTab.id] = listOf(newTab)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = "Failed to open file: ${error.message}", isLoading = false)
                    }
                }
            )
        }
    }

    private fun navigateToFolder(folder: FileItem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            listFilesUseCase(folder.uri).fold(
                onSuccess = { files ->
                    _uiState.update {
                        it.copy(
                            currentFolderUri = folder.uri,
                            folderName = folder.name,
                            fileTree = files,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message, isLoading = false)
                    }
                }
            )
        }
    }

    private fun navigateBack() {
        val currentUri = _uiState.value.currentFolderUri ?: return
        val parentUri = getParentUri(currentUri) ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            listFilesUseCase(parentUri).fold(
                onSuccess = { files ->
                    _uiState.update {
                        it.copy(
                            currentFolderUri = parentUri,
                            folderName = getFolderName(parentUri),
                            fileTree = files,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message, isLoading = false)
                    }
                }
            )
        }
    }

    private fun getParentUri(uri: Uri): Uri? {
        val uriString = uri.toString()
        val lastSlash = uriString.lastIndexOf('/')
        return if (lastSlash > 0) {
            Uri.parse(uriString.substring(0, lastSlash))
        } else null
    }

    private fun closeTab(tabId: String) {
        val currentTabs = _uiState.value.openTabs
        val tabIndex = currentTabs.indexOfFirst { it.id == tabId }
        if (tabIndex == -1) return

        val newTabs = currentTabs.toMutableList().apply { removeAt(tabIndex) }
        val newActiveIndex = when {
            newTabs.isEmpty() -> 0
            tabIndex >= newTabs.size -> newTabs.size - 1
            else -> tabIndex
        }

        _uiState.update {
            it.copy(
                openTabs = newTabs,
                activeTabIndex = newActiveIndex.coerceAtLeast(0)
            )
        }
        undoStacks.remove(tabId)
        redoStacks.remove(tabId)
        fileUris.remove(tabId)
    }

    private fun setActiveTab(index: Int) {
        if (index in _uiState.value.openTabs.indices) {
            val tab = _uiState.value.openTabs[index]
            _uiState.update {
                it.copy(
                    activeTabIndex = index,
                    currentFileUri = fileUris[tab.id]
                )
            }
        }
    }

    private fun updateContent(tabId: String, content: String) {
        _uiState.update { state ->
            state.copy(
                openTabs = state.openTabs.map { tab ->
                    if (tab.id == tabId) {
                        tab.copy(
                            content = content,
                            isModified = content != tab.originalContent
                        )
                    } else tab
                }
            )
        }

        val currentTab = _uiState.value.openTabs.find { it.id == tabId }
        currentTab?.let { tab ->
            val stack = undoStacks[tabId] ?: emptyList()
            undoStacks[tabId] = stack + tab

            if (_uiState.value.autoSaveEnabled) {
                scheduleAutoSave(tabId)
            }
        }
    }

    private fun scheduleAutoSave(tabId: String) {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(autoSaveDelay)
            val tab = _uiState.value.openTabs.find { it.id == tabId }
            if (tab?.isModified == true) {
                saveFile(tabId)
            }
        }
    }

    private fun saveFile(tabId: String) {
        val tab = _uiState.value.openTabs.find { it.id == tabId } ?: return
        val uri = fileUris[tabId] ?: return

        viewModelScope.launch {
            saveFileUseCase(uri, tab.content).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            openTabs = state.openTabs.map { t ->
                                if (t.id == tabId) {
                                    t.copy(
                                        originalContent = t.content,
                                        isModified = false
                                    )
                                } else t
                            }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = "Save failed: ${error.message}")
                    }
                }
            )
        }
    }

    private fun saveAllFiles() {
        _uiState.value.openTabs
            .filter { it.isModified }
            .forEach { tab -> saveFile(tab.id) }
    }

    private fun updateCursor(tabId: String, line: Int, column: Int) {
        _uiState.update { state ->
            state.copy(
                openTabs = state.openTabs.map { tab ->
                    if (tab.id == tabId) {
                        tab.copy(cursorLine = line, cursorColumn = column)
                    } else tab
                }
            )
        }
    }

    private fun showSearch(query: String) {
        _uiState.update { it.copy(isSearchVisible = true, searchQuery = query) }
    }

    private fun hideSearch() {
        _uiState.update {
            it.copy(isSearchVisible = false, searchQuery = "", replaceText = "")
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun updateReplaceText(text: String) {
        _uiState.update { it.copy(replaceText = text) }
    }

    private fun findNext() {
        val activeTab = _uiState.value.activeTab ?: return
        val query = _uiState.value.searchQuery
        if (query.isEmpty()) return

        val content = activeTab.content
        val currentIndex = content.indexOf(query, ignoreCase = true)
        if (currentIndex != -1) {
        }
    }

    private fun replaceOne() {
        val activeTab = _uiState.value.activeTab ?: return
        val query = _uiState.value.searchQuery
        val replacement = _uiState.value.replaceText
        if (query.isEmpty()) return

        val newContent = activeTab.content.replaceFirst(query, replacement)
        _uiState.update { state ->
            state.copy(
                openTabs = state.openTabs.map { tab ->
                    if (tab.id == activeTab.id) {
                        tab.copy(content = newContent, isModified = true)
                    } else tab
                }
            )
        }
    }

    private fun replaceAll() {
        val activeTab = _uiState.value.activeTab ?: return
        val query = _uiState.value.searchQuery
        val replacement = _uiState.value.replaceText
        if (query.isEmpty()) return

        val newContent = activeTab.content.replace(query, replacement)
        _uiState.update { state ->
            state.copy(
                openTabs = state.openTabs.map { tab ->
                    if (tab.id == activeTab.id) {
                        tab.copy(content = newContent, isModified = true)
                    } else tab
                }
            )
        }
    }

    private fun undo(tabId: String) {
        val stack = undoStacks[tabId] ?: return
        if (stack.isEmpty()) return

        val currentTab = _uiState.value.openTabs.find { it.id == tabId } ?: return
        val previousState = stack.last()

        val redoStack = redoStacks[tabId] ?: emptyList()
        redoStacks[tabId] = redoStack + currentTab

        _uiState.update { state ->
            state.copy(
                openTabs = state.openTabs.map { tab ->
                    if (tab.id == tabId) previousState else tab
                }
            )
        }

        undoStacks[tabId] = stack.dropLast(1)
    }

    private fun redo(tabId: String) {
        val stack = redoStacks[tabId] ?: return
        if (stack.isEmpty()) return

        val currentTab = _uiState.value.openTabs.find { it.id == tabId } ?: return
        val nextState = stack.last()

        val undoStack = undoStacks[tabId] ?: emptyList()
        undoStacks[tabId] = undoStack + currentTab

        _uiState.update { state ->
            state.copy(
                openTabs = state.openTabs.map { tab ->
                    if (tab.id == tabId) nextState else tab
                }
            )
        }

        redoStacks[tabId] = stack.dropLast(1)
    }

    private fun zoomIn(step: Float) {
        _uiState.update {
            it.copy(fontSize = (it.fontSize + step).coerceAtMost(32f))
        }
    }

    private fun zoomOut(step: Float) {
        _uiState.update {
            it.copy(fontSize = (it.fontSize - step).coerceAtLeast(8f))
        }
    }

    private fun toggleFileTree() {
        _uiState.update { it.copy(isFileTreeVisible = !it.isFileTreeVisible) }
    }

    private fun selectText(text: String) {
        _uiState.update { it.copy(selectedText = text) }
    }

    private fun showAiAssist() {
        _uiState.update { it.copy(isAiAssistVisible = true) }
    }

    private fun hideAiAssist() {
        _uiState.update {
            it.copy(isAiAssistVisible = false, aiAssistResponse = "", selectedText = "")
        }
    }

    private fun sendToAi(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true) }
            delay(1000)
            _uiState.update {
                it.copy(
                    isAiLoading = false,
                    aiAssistResponse = "AI analysis: This code snippet selected. (AI integration available via chat)"
                )
            }
        }
    }

    private fun insertAiResponse() {
        val response = _uiState.value.aiAssistResponse
        if (response.isEmpty()) return

        val activeTab = _uiState.value.activeTab ?: return
        val newContent = activeTab.content + "\n" + response

        _uiState.update { state ->
            state.copy(
                openTabs = state.openTabs.map { tab ->
                    if (tab.id == activeTab.id) {
                        tab.copy(content = newContent, isModified = true)
                    } else tab
                },
                isAiAssistVisible = false,
                aiAssistResponse = ""
            )
        }
    }

    private fun toggleAutoSave(enabled: Boolean) {
        _uiState.update { it.copy(autoSaveEnabled = enabled) }
        if (!enabled) {
            autoSaveJob?.cancel()
        }
    }

    private fun getFolderName(uri: Uri): String {
        return uri.lastPathSegment?.substringAfterLast('/') ?: "Project"
    }
}
