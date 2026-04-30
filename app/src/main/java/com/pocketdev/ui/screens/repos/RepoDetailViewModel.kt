package com.pocketdev.ui.screens.repos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.RemoteBranch
import com.pocketdev.domain.model.RemoteFile
import com.pocketdev.domain.model.RemoteRepository
import com.pocketdev.domain.model.RemoteRepositoryResult
import com.pocketdev.domain.model.RemoteCommit
import com.pocketdev.domain.model.RemoteFileContent
import com.pocketdev.domain.repository.RemoteRepositoryGateway
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepoDetailUiState(
    val repository: RemoteRepository? = null,
    val branches: List<RemoteBranch> = emptyList(),
    val selectedBranch: String = "main",
    val currentPath: String = "",
    val breadcrumb: List<String> = emptyList(),
    val files: List<RemoteFile> = emptyList(),
    val commits: List<RemoteCommit> = emptyList(),
    val selectedFile: RemoteFile? = null,
    val fileContent: RemoteFileContent? = null,
    val isLoading: Boolean = false,
    val isLoadingContent: Boolean = false,
    val error: String? = null
)

sealed interface RepoDetailEvent {
    data object LoadBranches : RepoDetailEvent
    data class SelectBranch(val branchName: String) : RepoDetailEvent
    data class NavigateToPath(val path: String) : RepoDetailEvent
    data class NavigateToFile(val file: RemoteFile) : RepoDetailEvent
    data object NavigateUp : RepoDetailEvent
    data object LoadFileContent : RepoDetailEvent
    data object ClearFileSelection : RepoDetailEvent
    data object ClearError : RepoDetailEvent
}

@HiltViewModel
class RepoDetailViewModel @Inject constructor(
    private val remoteRepository: RemoteRepositoryGateway,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepoDetailUiState())
    val uiState: StateFlow<RepoDetailUiState> = _uiState.asStateFlow()

    private val _repoFullName: String = savedStateHandle.get<String>("repoFullName") ?: ""
    private val _repoId: Long = savedStateHandle.get<Long>("repoId") ?: 0L
    private val _repoOwner: String = savedStateHandle.get<String>("repoOwner") ?: ""
    private val _repoDefaultBranch: String = savedStateHandle.get<String>("repoDefaultBranch") ?: "main"

    init {
        val repo = RemoteRepository(
            id = _repoId,
            name = _repoFullName.substringAfterLast("/"),
            fullName = _repoFullName,
            description = null,
            htmlUrl = "",
            defaultBranch = _repoDefaultBranch,
            provider = com.pocketdev.domain.model.RepositoryProvider.GITHUB,
            owner = _repoOwner
        )
        _uiState.update { it.copy(repository = repo, selectedBranch = _repoDefaultBranch) }
        loadBranches()
        loadFileTree()
    }

    fun onEvent(event: RepoDetailEvent) {
        when (event) {
            is RepoDetailEvent.LoadBranches -> loadBranches()
            is RepoDetailEvent.SelectBranch -> selectBranch(event.branchName)
            is RepoDetailEvent.NavigateToPath -> navigateToPath(event.path)
            is RepoDetailEvent.NavigateToFile -> handleFileClick(event.file)
            is RepoDetailEvent.NavigateUp -> navigateUp()
            is RepoDetailEvent.LoadFileContent -> loadFileContent()
            is RepoDetailEvent.ClearFileSelection -> clearFileSelection()
            is RepoDetailEvent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun loadBranches() {
        val repo = _uiState.value.repository ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = remoteRepository.getBranches(repo)) {
                is RemoteRepositoryResult.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val branches = result.data as List<RemoteBranch>
                    _uiState.update {
                        it.copy(
                            branches = branches,
                            isLoading = false,
                            selectedBranch = branches.find { b -> b.isDefault }?.name
                                ?: branches.firstOrNull()?.name
                                ?: it.selectedBranch
                        )
                    }
                    loadFileTree()
                }
                is RemoteRepositoryResult.Error -> {
                    _uiState.update { it.copy(error = result.message, isLoading = false) }
                }
                RemoteRepositoryResult.NotAuthenticated -> {
                    _uiState.update { it.copy(error = "Not authenticated", isLoading = false) }
                }
                RemoteRepositoryResult.RateLimited -> {
                    _uiState.update { it.copy(error = "Rate limited, please try again later", isLoading = false) }
                }
            }
        }
    }

    private fun selectBranch(branchName: String) {
        _uiState.update {
            it.copy(
                selectedBranch = branchName,
                currentPath = "",
                breadcrumb = emptyList(),
                files = emptyList(),
                selectedFile = null,
                fileContent = null
            )
        }
        loadFileTree()
    }

    private fun loadFileTree() {
        val repo = _uiState.value.repository ?: return
        val branch = _uiState.value.selectedBranch
        val path = _uiState.value.currentPath

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = remoteRepository.getFileTree(repo, branch, path)) {
                is RemoteRepositoryResult.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val files = (result.data as List<RemoteFile>).sortedWith(
                        compareBy({ it.type != com.pocketdev.domain.model.RemoteFileType.DIRECTORY }, { it.name.lowercase() })
                    )
                    _uiState.update {
                        it.copy(
                            files = files,
                            isLoading = false
                        )
                    }
                    loadCommits()
                }
                is RemoteRepositoryResult.Error -> {
                    _uiState.update { it.copy(error = result.message, isLoading = false) }
                }
                RemoteRepositoryResult.NotAuthenticated -> {
                    _uiState.update { it.copy(error = "Not authenticated", isLoading = false) }
                }
                RemoteRepositoryResult.RateLimited -> {
                    _uiState.update { it.copy(error = "Rate limited", isLoading = false) }
                }
            }
        }
    }

    private fun loadCommits() {
        val repo = _uiState.value.repository ?: return
        val branch = _uiState.value.selectedBranch
        val path = _uiState.value.currentPath

        viewModelScope.launch {
            when (val result = remoteRepository.getCommits(repo, branch, path.ifEmpty { null })) {
                is RemoteRepositoryResult.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    _uiState.update { it.copy(commits = (result.data as List<RemoteCommit>).take(10)) }
                }
                else -> { /* Silently fail for commits */ }
            }
        }
    }

    private fun navigateToPath(path: String) {
        _uiState.update {
            it.copy(
                currentPath = path,
                breadcrumb = if (path.isEmpty()) emptyList() else path.split("/"),
                selectedFile = null,
                fileContent = null
            )
        }
        loadFileTree()
    }

    private fun handleFileClick(file: RemoteFile) {
        when (file.type) {
            com.pocketdev.domain.model.RemoteFileType.DIRECTORY -> {
                navigateToPath(file.path)
            }
            else -> {
                _uiState.update { it.copy(selectedFile = file) }
                loadFileContent()
            }
        }
    }

    private fun loadFileContent() {
        val repo = _uiState.value.repository ?: return
        val file = _uiState.value.selectedFile ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingContent = true) }
            when (val result = remoteRepository.getFileContent(repo, file.path, _uiState.value.selectedBranch)) {
                is RemoteRepositoryResult.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    _uiState.update { it.copy(fileContent = result.data as RemoteFileContent, isLoadingContent = false) }
                }
                is RemoteRepositoryResult.Error -> {
                    _uiState.update { it.copy(error = result.message, isLoadingContent = false) }
                }
                else -> _uiState.update { it.copy(isLoadingContent = false) }
            }
        }
    }

    private fun navigateUp() {
        val currentPath = _uiState.value.currentPath
        if (currentPath.isEmpty()) return

        val parentPath = currentPath.substringBeforeLast("/", "")
        navigateToPath(parentPath)
    }

    private fun clearFileSelection() {
        _uiState.update { it.copy(selectedFile = null, fileContent = null) }
    }
}
