package com.pocketdev.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.AiProviderConfig
import com.pocketdev.domain.model.AiProviderType
import com.pocketdev.domain.model.DEFAULT_PROVIDERS
import com.pocketdev.domain.repository.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val providers: List<AiProviderConfig> = DEFAULT_PROVIDERS,
    val activeProviderType: AiProviderType = AiProviderType.DEEPSEEK,
    val editingProvider: AiProviderConfig? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

sealed interface SettingsEvent {
    data class SelectProvider(val type: AiProviderType) : SettingsEvent
    data class EditProvider(val provider: AiProviderConfig) : SettingsEvent
    data class UpdateBaseUrl(val url: String) : SettingsEvent
    data class UpdateApiKey(val key: String) : SettingsEvent
    data class UpdateModelName(val name: String) : SettingsEvent
    data object SaveProvider : SettingsEvent
    data object CancelEdit : SettingsEvent
    data object ClearError : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadProviders()
    }

    private fun loadProviders() {
        viewModelScope.launch {
            try {
                val providers = userSettingsRepository.getProviders()
                val activeProvider = userSettingsRepository.getActiveProvider()
                _uiState.update {
                    it.copy(
                        providers = providers,
                        activeProviderType = activeProvider?.type ?: AiProviderType.DEEPSEEK
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SelectProvider -> selectProvider(event.type)
            is SettingsEvent.EditProvider -> startEditing(event.provider)
            is SettingsEvent.UpdateBaseUrl -> updateEditingProvider { it.copy(baseUrl = event.url) }
            is SettingsEvent.UpdateApiKey -> updateEditingProvider { it.copy(apiKey = event.key) }
            is SettingsEvent.UpdateModelName -> updateEditingProvider { it.copy(modelName = event.name) }
            is SettingsEvent.SaveProvider -> saveProvider()
            is SettingsEvent.CancelEdit -> _uiState.update { it.copy(editingProvider = null) }
            is SettingsEvent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun selectProvider(type: AiProviderType) {
        viewModelScope.launch {
            try {
                userSettingsRepository.setActiveProvider(type)
                _uiState.update { it.copy(activeProviderType = type, editingProvider = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun startEditing(provider: AiProviderConfig) {
        _uiState.update { it.copy(editingProvider = provider) }
    }

    private fun updateEditingProvider(transform: (AiProviderConfig) -> AiProviderConfig) {
        _uiState.update { state ->
            state.editingProvider?.let { editing ->
                state.copy(editingProvider = transform(editing))
            } ?: state
        }
    }

    private fun saveProvider() {
        val provider = _uiState.value.editingProvider ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveSuccess = false) }
            try {
                userSettingsRepository.updateProvider(provider)
                val updatedProviders = userSettingsRepository.getProviders()
                _uiState.update {
                    it.copy(
                        providers = updatedProviders,
                        editingProvider = null,
                        isSaving = false,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}