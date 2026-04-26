package com.pocketdev.ui.screens.editor

import android.net.Uri
import com.pocketdev.domain.model.EditorTab
import com.pocketdev.domain.model.FileItem

data class EditorUiState(
    val currentFolderUri: Uri? = null,
    val folderName: String = "",
    val fileTree: List<FileItem> = emptyList(),
    val openTabs: List<EditorTab> = emptyList(),
    val activeTabIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val replaceText: String = "",
    val isSearchVisible: Boolean = false,
    val fontSize: Float = 14f,
    val showFolderPicker: Boolean = false,
    val isFileTreeVisible: Boolean = true,
    val autoSaveEnabled: Boolean = true,
    val selectedText: String = "",
    val isAiAssistVisible: Boolean = false,
    val aiAssistResponse: String = "",
    val isAiLoading: Boolean = false,
    val currentFileUri: Uri? = null
) {
    val activeTab: EditorTab?
        get() = openTabs.getOrNull(activeTabIndex)

    val hasUnsavedChanges: Boolean
        get() = openTabs.any { it.isModified }

    val canGoBack: Boolean
        get() = currentFolderUri != null
}

sealed interface EditorEvent {
    data class OpenFolder(val uri: Uri) : EditorEvent
    data object OpenFolderPicker : EditorEvent
    data object CloseFolderPicker : EditorEvent
    data class SelectFile(val file: FileItem) : EditorEvent
    data class NavigateToFolder(val folder: FileItem) : EditorEvent
    data object NavigateBack : EditorEvent
    data class CloseTab(val tabId: String) : EditorEvent
    data class SetActiveTab(val index: Int) : EditorEvent
    data class UpdateContent(val tabId: String, val content: String) : EditorEvent
    data class SaveFile(val tabId: String) : EditorEvent
    data object SaveAllFiles : EditorEvent
    data class UpdateCursor(val tabId: String, val line: Int, val column: Int) : EditorEvent
    data class ShowSearch(val query: String = "") : EditorEvent
    data object HideSearch : EditorEvent
    data class UpdateSearchQuery(val query: String) : EditorEvent
    data class UpdateReplaceText(val text: String) : EditorEvent
    data object FindNext : EditorEvent
    data object ReplaceOne : EditorEvent
    data object ReplaceAll : EditorEvent
    data class Undo(val tabId: String) : EditorEvent
    data class Redo(val tabId: String) : EditorEvent
    data object ClearError : EditorEvent
    data class ZoomIn(val step: Float = 2f) : EditorEvent
    data class ZoomOut(val step: Float = 2f) : EditorEvent
    data object ToggleFileTree : EditorEvent
    data class SelectText(val text: String) : EditorEvent
    data object ShowAiAssist : EditorEvent
    data object HideAiAssist : EditorEvent
    data class SendToAi(val code: String) : EditorEvent
    data object InsertAiResponse : EditorEvent
    data class ToggleAutoSave(val enabled: Boolean) : EditorEvent
}
