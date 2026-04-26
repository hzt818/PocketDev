package com.pocketdev.ui.screens.ollama

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.AVAILABLE_MODELS
import com.pocketdev.domain.model.OllamaModel
import com.pocketdev.domain.model.OllamaModelWithStatus
import com.pocketdev.domain.model.OllamaStatus
import com.pocketdev.domain.repository.OllamaRepository
import com.pocketdev.domain.repository.PullProgress
import com.pocketdev.domain.usecase.DeleteOllamaModelUseCase
import com.pocketdev.domain.usecase.GetOllamaModelsUseCase
import com.pocketdev.domain.usecase.PullOllamaModelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OllamaUiState(
    val installedModels: List<OllamaModel> = emptyList(),
    val availableModels: List<OllamaModelWithStatus> = AVAILABLE_MODELS,
    val isLoading: Boolean = false,
    val isServerRunning: Boolean = false,
    val error: String? = null,
    val downloadProgress: Map<String, Float> = emptyMap(),
    val downloadingModel: String? = null
)

sealed interface OllamaEvent {
    data class PullModel(val modelName: String) : OllamaEvent
    data class DeleteModel(val modelName: String) : OllamaEvent
    data object RefreshModels : OllamaEvent
    data object StartServer : OllamaEvent
    data object StopServer : OllamaEvent
    data object ClearError : OllamaEvent
}

@HiltViewModel
class OllamaViewModel @Inject constructor(
    private val getOllamaModels: GetOllamaModelsUseCase,
    private val pullOllamaModel: PullOllamaModelUseCase,
    private val deleteOllamaModel: DeleteOllamaModelUseCase,
    private val ollamaRepository: OllamaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OllamaUiState())
    val uiState: StateFlow<OllamaUiState> = _uiState.asStateFlow()

    private var pullJob: Job? = null

    init {
        checkServerStatus()
        loadInstalledModels()
    }

    private fun checkServerStatus() {
        _uiState.update { it.copy(isServerRunning = ollamaRepository.isServerRunning()) }
    }

    private fun loadInstalledModels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getOllamaModels().fold(
                onSuccess = { models ->
                    _uiState.update { state ->
                        state.copy(
                            installedModels = models,
                            isLoading = false,
                            availableModels = AVAILABLE_MODELS.map { available ->
                                val isInstalled = models.any {
                                    it.name.startsWith(available.name.split(":").first())
                                }
                                available.copy(
                                    status = if (isInstalled) OllamaStatus.INSTALLED
                                    else OllamaStatus.NOT_INSTALLED
                                )
                            }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                }
            )
        }
    }

    fun onEvent(event: OllamaEvent) {
        when (event) {
            is OllamaEvent.PullModel -> pullModel(event.modelName)
            is OllamaEvent.DeleteModel -> deleteModel(event.modelName)
            is OllamaEvent.RefreshModels -> loadInstalledModels()
            is OllamaEvent.StartServer -> startServer()
            is OllamaEvent.StopServer -> stopServer()
            is OllamaEvent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun pullModel(modelName: String) {
        pullJob?.cancel()
        pullJob = viewModelScope.launch {
            _uiState.update { it.copy(downloadingModel = modelName, downloadProgress = emptyMap()) }

            pullOllamaModel(modelName).collect { progress ->
                when (progress) {
                    is PullProgress.Progress -> {
                        val percent = if (progress.total > 0) {
                            (progress.completed.toFloat() / progress.total.toFloat())
                        } else 0f
                        _uiState.update {
                            it.copy(
                                downloadProgress = it.downloadProgress + (modelName to percent)
                            )
                        }
                    }
                    is PullProgress.Completed -> {
                        _uiState.update {
                            it.copy(
                                downloadingModel = null,
                                downloadProgress = emptyMap()
                            )
                        }
                        loadInstalledModels()
                    }
                    is PullProgress.Error -> {
                        _uiState.update {
                            it.copy(
                                downloadingModel = null,
                                downloadProgress = emptyMap(),
                                error = progress.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun deleteModel(modelName: String) {
        viewModelScope.launch {
            deleteOllamaModel(modelName).fold(
                onSuccess = { loadInstalledModels() },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            )
        }
    }

    private fun startServer() {
        viewModelScope.launch {
            ollamaRepository.startServer().fold(
                onSuccess = {
                    _uiState.update { it.copy(isServerRunning = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            )
        }
    }

    private fun stopServer() {
        viewModelScope.launch {
            ollamaRepository.stopServer().fold(
                onSuccess = {
                    _uiState.update { it.copy(isServerRunning = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            )
        }
    }
}
