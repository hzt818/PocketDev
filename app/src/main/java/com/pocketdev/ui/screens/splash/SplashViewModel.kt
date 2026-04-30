package com.pocketdev.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.repository.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashUiState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val animationProgress: Float = 0f
)

sealed interface SplashEvent {
    data object AnimationTick : SplashEvent
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val gitHubToken = userSettingsRepository.getGitHubToken()
                val hasToken = !gitHubToken.isNullOrBlank()
                _uiState.update { it.copy(isAuthenticated = hasToken, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun onEvent(event: SplashEvent) {
        when (event) {
            is SplashEvent.AnimationTick -> {
                _uiState.update { state ->
                    val newProgress = (state.animationProgress + 0.02f).coerceAtMost(1f)
                    state.copy(animationProgress = newProgress)
                }
            }
        }
    }
}