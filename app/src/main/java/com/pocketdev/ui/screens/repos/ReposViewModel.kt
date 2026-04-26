package com.pocketdev.ui.screens.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.GitHubRepo
import com.pocketdev.domain.repository.GitHubRepository
import com.pocketdev.domain.usecase.AuthenticateGitHubUseCase
import com.pocketdev.domain.usecase.GetReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReposUiState(
    val repos: List<GitHubRepo> = emptyList(),
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val authUrl: String? = null
)

sealed interface ReposEvent {
    data object LoadRepos : ReposEvent
    data object Login : ReposEvent
    data class SelectRepo(val repo: GitHubRepo) : ReposEvent
    data object ClearError : ReposEvent
}

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val getReposUseCase: GetReposUseCase,
    private val authenticateGitHubUseCase: AuthenticateGitHubUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReposUiState())
    val uiState: StateFlow<ReposUiState> = _uiState.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            val isAuthenticated = authenticateGitHubUseCase.isAuthenticated()
            _uiState.update { it.copy(isAuthenticated = isAuthenticated) }
            if (isAuthenticated) {
                loadRepos()
            }
        }
    }

    fun onEvent(event: ReposEvent) {
        when (event) {
            is ReposEvent.LoadRepos -> loadRepos()
            is ReposEvent.Login -> generateAuthUrl()
            is ReposEvent.SelectRepo -> { /* Handle repo selection */ }
            is ReposEvent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun loadRepos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getReposUseCase().fold(
                onSuccess = { repos ->
                    _uiState.update { it.copy(repos = repos, isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun generateAuthUrl() {
        val url = authenticateGitHubUseCase.getAuthorizationUrl()
        _uiState.update { it.copy(authUrl = url) }
    }
}
