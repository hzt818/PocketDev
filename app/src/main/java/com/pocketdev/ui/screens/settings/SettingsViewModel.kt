package com.pocketdev.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.AiActionMode
import com.pocketdev.domain.model.AiProviderConfig
import com.pocketdev.domain.model.AiProviderType
import com.pocketdev.domain.model.AppSettings
import com.pocketdev.domain.model.DEFAULT_PROVIDERS
import com.pocketdev.domain.model.EditorPreferences
import com.pocketdev.domain.model.ThemeMode
import com.pocketdev.domain.model.UserProfile
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
    val actionMode: AiActionMode = AiActionMode.PLAN,
    val editingProvider: AiProviderConfig? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    // User Management
    val userProfile: UserProfile? = null,
    // App Settings
    val appSettings: AppSettings = AppSettings(),
    // UI State
    val isClearingCache: Boolean = false,
    val cacheSize: String = "0 B"
)

sealed interface SettingsEvent {
    // Provider events
    data class SelectProvider(val type: AiProviderType) : SettingsEvent
    data class EditProvider(val provider: AiProviderConfig) : SettingsEvent
    data class UpdateBaseUrl(val url: String) : SettingsEvent
    data class UpdateApiKey(val key: String) : SettingsEvent
    data class UpdateModelName(val name: String) : SettingsEvent
    data object SaveProvider : SettingsEvent
    data object CancelEdit : SettingsEvent
    data object ClearError : SettingsEvent
    data class SetActionMode(val mode: AiActionMode) : SettingsEvent
    // Theme events
    data class UpdateThemeMode(val mode: ThemeMode) : SettingsEvent
    data class UpdateDynamicColor(val enabled: Boolean) : SettingsEvent
    // Editor preference events
    data class UpdateFontSize(val size: Int) : SettingsEvent
    data class UpdateTabSize(val size: Int) : SettingsEvent
    data class UpdateShowLineNumbers(val show: Boolean) : SettingsEvent
    data class UpdateWordWrap(val wrap: Boolean) : SettingsEvent
    // User management events
    data object SignOut : SettingsEvent
    // Cache events
    data object ClearCache : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadProviders()
        loadActionMode()
        loadAppSettings()
        loadUserProfile()
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

    private fun loadActionMode() {
        viewModelScope.launch {
            try {
                val mode = userSettingsRepository.getActionMode()
                _uiState.update { it.copy(actionMode = mode) }
            } catch (e: Exception) {
                // Use default
            }
        }
    }

    private fun loadAppSettings() {
        viewModelScope.launch {
            try {
                val settings = userSettingsRepository.getAppSettings()
                _uiState.update { it.copy(appSettings = settings) }
            } catch (e: Exception) {
                // Use default
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val profile = userSettingsRepository.getUserProfile()
                _uiState.update { it.copy(userProfile = profile) }
            } catch (e: Exception) {
                // Use default
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
            is SettingsEvent.SetActionMode -> setActionMode(event.mode)
            is SettingsEvent.UpdateThemeMode -> updateThemeMode(event.mode)
            is SettingsEvent.UpdateDynamicColor -> updateDynamicColor(event.enabled)
            is SettingsEvent.UpdateFontSize -> updateEditorPreferences { it.copy(fontSize = event.size) }
            is SettingsEvent.UpdateTabSize -> updateEditorPreferences { it.copy(tabSize = event.size) }
            is SettingsEvent.UpdateShowLineNumbers -> updateEditorPreferences { it.copy(showLineNumbers = event.show) }
            is SettingsEvent.UpdateWordWrap -> updateEditorPreferences { it.copy(wordWrap = event.wrap) }
            is SettingsEvent.SignOut -> signOut()
            is SettingsEvent.ClearCache -> clearCache()
        }
    }

    private fun setActionMode(mode: AiActionMode) {
        viewModelScope.launch {
            try {
                userSettingsRepository.setActionMode(mode)
                _uiState.update { it.copy(actionMode = mode) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
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

    private fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            try {
                val newSettings = _uiState.value.appSettings.copy(themeMode = mode)
                userSettingsRepository.updateAppSettings(newSettings)
                _uiState.update { it.copy(appSettings = newSettings) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun updateDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val newSettings = _uiState.value.appSettings.copy(dynamicColor = enabled)
                userSettingsRepository.updateAppSettings(newSettings)
                _uiState.update { it.copy(appSettings = newSettings) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun updateEditorPreferences(transform: (EditorPreferences) -> EditorPreferences) {
        viewModelScope.launch {
            try {
                val currentSettings = _uiState.value.appSettings
                val newEditorPrefs = transform(currentSettings.editorPreferences)
                val newSettings = currentSettings.copy(editorPreferences = newEditorPrefs)
                userSettingsRepository.updateAppSettings(newSettings)
                _uiState.update { it.copy(appSettings = newSettings) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            try {
                userSettingsRepository.clearUserSession()
                _uiState.update { it.copy(userProfile = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun clearCache() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClearingCache = true) }
            try {
                // TODO: Implement actual cache clearing
                _uiState.update { it.copy(isClearingCache = false, cacheSize = "0 B") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isClearingCache = false, error = e.message) }
            }
        }
    }
}
