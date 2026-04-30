package com.pocketdev.ui.screens.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.EditorTab
import com.pocketdev.domain.model.FileType
import com.pocketdev.domain.model.RemoteFileContent
import com.pocketdev.domain.model.RemoteRepository
import com.pocketdev.domain.model.RemoteRepositoryResult
import com.pocketdev.domain.model.RepositoryProvider
import com.pocketdev.domain.repository.RemoteRepositoryGateway
import com.pocketdev.ui.i18n.UiMessage
import com.pocketdev.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RemoteEditorUiState(
    val activeTab: EditorTab? = null,
    val openTabs: List<EditorTab> = emptyList(),
    val activeTabIndex: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isModified: Boolean = false,
    val error: UiMessage? = null,
    val showCommitDialog: Boolean = false,
    val commitMessage: String = "",
    val branch: String = "",
    val fileSha: String = "",
    val cursorLine: Int = 1,
    val cursorColumn: Int = 1,
    val fontSize: Float = 14f,
    val isSearchVisible: Boolean = false,
    val searchQuery: String = "",
    val replaceText: String = "",
    val showSaveSuccess: Boolean = false
)

sealed interface RemoteEditorEvent {
    data class UpdateContent(val content: String) : RemoteEditorEvent
    data class UpdateCursor(val line: Int, val column: Int) : RemoteEditorEvent
    data object SaveFile : RemoteEditorEvent
    data object ShowCommitDialog : RemoteEditorEvent
    data object HideCommitDialog : RemoteEditorEvent
    data class UpdateCommitMessage(val message: String) : RemoteEditorEvent
    data object CommitFile : RemoteEditorEvent
    data object ClearError : RemoteEditorEvent
    data class ZoomIn(val step: Float) : RemoteEditorEvent
    data class ZoomOut(val step: Float) : RemoteEditorEvent
    data object ShowSearch : RemoteEditorEvent
    data object HideSearch : RemoteEditorEvent
    data class UpdateSearchQuery(val query: String) : RemoteEditorEvent
    data class UpdateReplaceText(val text: String) : RemoteEditorEvent
    data object FindNext : RemoteEditorEvent
    data object ReplaceOne : RemoteEditorEvent
    data object ReplaceAll : RemoteEditorEvent
}

@HiltViewModel
class RemoteEditorViewModel @Inject constructor(
    private val remoteRepository: RemoteRepositoryGateway,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemoteEditorUiState())
    val uiState: StateFlow<RemoteEditorUiState> = _uiState.asStateFlow()

    private val repoFullName: String = savedStateHandle.get<String>("repoFullName") ?: ""
    private val branch: String = savedStateHandle.get<String>("branch") ?: "main"
    private val filePath: String = savedStateHandle.get<String>("filePath") ?: ""
    private val fileSha: String = savedStateHandle.get<String>("sha") ?: ""

    private var autoSaveJob: Job? = null
    private val autoSaveDelay = 2000L

    init {
        loadFile()
    }

    fun onEvent(event: RemoteEditorEvent) {
        when (event) {
            is RemoteEditorEvent.UpdateContent -> updateContent(event.content)
            is RemoteEditorEvent.UpdateCursor -> updateCursor(event.line, event.column)
            RemoteEditorEvent.SaveFile -> showCommitDialog()
            RemoteEditorEvent.ShowCommitDialog -> showCommitDialog()
            RemoteEditorEvent.HideCommitDialog -> hideCommitDialog()
            is RemoteEditorEvent.UpdateCommitMessage -> updateCommitMessage(event.message)
            RemoteEditorEvent.CommitFile -> commitFile()
            RemoteEditorEvent.ClearError -> _uiState.update { it.copy(error = null) }
            is RemoteEditorEvent.ZoomIn -> zoomIn(event.step)
            is RemoteEditorEvent.ZoomOut -> zoomOut(event.step)
            RemoteEditorEvent.ShowSearch -> showSearch()
            RemoteEditorEvent.HideSearch -> hideSearch()
            is RemoteEditorEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            is RemoteEditorEvent.UpdateReplaceText -> updateReplaceText(event.text)
            RemoteEditorEvent.FindNext -> findNext()
            RemoteEditorEvent.ReplaceOne -> replaceOne()
            RemoteEditorEvent.ReplaceAll -> replaceAll()
        }
    }

    private fun loadFile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, branch = branch, fileSha = fileSha) }

            val repository = RemoteRepository(
                id = 0,
                name = repoFullName.substringAfterLast("/"),
                fullName = repoFullName,
                description = null,
                htmlUrl = "",
                defaultBranch = branch,
                provider = RepositoryProvider.GITHUB,
                owner = repoFullName.substringBeforeLast("/")
            )

            when (val result = remoteRepository.getFileContent(repository, filePath, branch)) {
                is RemoteRepositoryResult.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val content = result.data as RemoteFileContent
                    val language = FileType.fromFileName(content.name)
                    val newTab = EditorTab.create(
                        fileName = content.name,
                        filePath = content.path,
                        content = content.content,
                        language = language
                    )

                    _uiState.update {
                        it.copy(
                            activeTab = newTab,
                            openTabs = listOf(newTab),
                            activeTabIndex = 0,
                            fileSha = content.sha,
                            isLoading = false
                        )
                    }
                }
                is RemoteRepositoryResult.Error -> {
                    _uiState.update {
                        it.copy(error = UiMessage.Generic(result.message), isLoading = false)
                    }
                }
                else -> {
                    _uiState.update {
                        it.copy(error = UiMessage.StrRes(R.string.error_remote_load_failed), isLoading = false)
                    }
                }
            }
        }
    }

    private fun updateContent(content: String) {
        _uiState.update { state ->
            val updatedTabs = state.openTabs.map { tab ->
                tab.copy(
                    content = content,
                    isModified = content != tab.originalContent
                )
            }
            val activeTab = updatedTabs.getOrNull(state.activeTabIndex)
            state.copy(
                openTabs = updatedTabs,
                activeTab = activeTab?.copy(
                    content = content,
                    isModified = content != (activeTab.originalContent)
                ),
                isModified = activeTab?.let { content != it.originalContent } ?: false
            )
        }
    }

    private fun updateCursor(line: Int, column: Int) {
        _uiState.update { it.copy(cursorLine = line, cursorColumn = column) }
    }

    private fun showCommitDialog() {
        _uiState.update { it.copy(showCommitDialog = true) }
    }

    private fun hideCommitDialog() {
        _uiState.update { it.copy(showCommitDialog = false, commitMessage = "") }
    }

    private fun updateCommitMessage(message: String) {
        _uiState.update { it.copy(commitMessage = message) }
    }

    private fun commitFile() {
        val tab = _uiState.value.activeTab ?: return
        val message = _uiState.value.commitMessage.ifBlank { "Update ${tab.fileName}" }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, showCommitDialog = false) }

            val repository = RemoteRepository(
                id = 0,
                name = repoFullName.substringAfterLast("/"),
                fullName = repoFullName,
                description = null,
                htmlUrl = "",
                defaultBranch = branch,
                provider = RepositoryProvider.GITHUB,
                owner = repoFullName.substringBeforeLast("/")
            )

            when (val result = remoteRepository.commitFile(
                repository = repository,
                path = filePath,
                content = tab.content,
                message = message,
                sha = _uiState.value.fileSha,
                branch = branch
            )) {
                is RemoteRepositoryResult.Success<*> -> {
                    _uiState.update { state ->
                        val updatedTabs = state.openTabs.map { t ->
                            if (t.id == tab.id) {
                                t.copy(
                                    originalContent = t.content,
                                    isModified = false
                                )
                            } else t
                        }
                        state.copy(
                            openTabs = updatedTabs,
                            activeTab = updatedTabs.getOrNull(state.activeTabIndex),
                            isModified = false,
                            isSaving = false,
                            showSaveSuccess = true,
                            fileSha = result.data.toString()
                        )
                    }
                    delay(2000)
                    _uiState.update { it.copy(showSaveSuccess = false) }
                }
                is RemoteRepositoryResult.Error -> {
                    _uiState.update {
                        it.copy(error = UiMessage.Generic(result.message), isSaving = false)
                    }
                }
                else -> {
                    _uiState.update {
                        it.copy(error = UiMessage.StrRes(R.string.error_remote_save_failed), isSaving = false)
                    }
                }
            }
        }
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

    private fun showSearch() {
        _uiState.update { it.copy(isSearchVisible = true) }
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
        // Search implementation would go here
    }

    private fun replaceOne() {
        val activeTab = _uiState.value.activeTab ?: return
        val query = _uiState.value.searchQuery
        val replacement = _uiState.value.replaceText
        if (query.isEmpty()) return

        val newContent = activeTab.content.replaceFirst(query, replacement)
        updateContent(newContent)
    }

    private fun replaceAll() {
        val activeTab = _uiState.value.activeTab ?: return
        val query = _uiState.value.searchQuery
        val replacement = _uiState.value.replaceText
        if (query.isEmpty()) return

        val newContent = activeTab.content.replace(query, replacement)
        updateContent(newContent)
    }
}
