package com.pocketdev.ui.screens.build

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.BuildConfig
import com.pocketdev.domain.model.BuildPhase
import com.pocketdev.domain.model.BuildProgress
import com.pocketdev.domain.model.BuildResult
import com.pocketdev.domain.model.GradleInfo
import com.pocketdev.domain.repository.BuildRepository
import com.pocketdev.ui.i18n.UiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class BuildUiState(
    val projectPath: String = "",
    val buildType: String = "debug",
    val isBuilding: Boolean = false,
    val currentProgress: BuildProgress? = null,
    val gradleInfo: GradleInfo? = null,
    val buildHistory: List<BuildResult> = emptyList(),
    val error: UiMessage? = null
)

sealed class BuildEvent {
    data class SetProjectPath(val path: String) : BuildEvent()
    data class SetBuildType(val type: String) : BuildEvent()
    data object ExecuteBuild : BuildEvent()
    data object CancelBuild : BuildEvent()
    data object RefreshGradleInfo : BuildEvent()
    data object ClearError : BuildEvent()
}

@HiltViewModel
class BuildViewModel @Inject constructor(
    private val buildRepository: BuildRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuildUiState())
    val uiState: StateFlow<BuildUiState> = _uiState.asStateFlow()

    private var buildJob: Job? = null

    init {
        refreshGradleInfo()
        loadBuildHistory()
    }

    fun onEvent(event: BuildEvent) {
        when (event) {
            is BuildEvent.SetProjectPath -> {
                _uiState.update { it.copy(projectPath = event.path) }
                refreshGradleInfo()
            }
            is BuildEvent.SetBuildType -> {
                _uiState.update { it.copy(buildType = event.type) }
            }
            is BuildEvent.ExecuteBuild -> executeBuild()
            is BuildEvent.CancelBuild -> cancelBuild()
            is BuildEvent.RefreshGradleInfo -> refreshGradleInfo()
            is BuildEvent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun executeBuild() {
        val projectPath = _uiState.value.projectPath
        if (projectPath.isBlank()) {
            _uiState.update { it.copy(error = UiMessage.StrRes(com.pocketdev.R.string.error_build_path_required)) }
            return
        }

        buildJob?.cancel()
        buildJob = viewModelScope.launch {
            _uiState.update { it.copy(isBuilding = true, currentProgress = null) }

            val config = BuildConfig(
                projectPath = projectPath,
                tasks = listOf("assemble${_uiState.value.buildType.replaceFirstChar { it.uppercase() }}"),
                buildType = _uiState.value.buildType,
                parallel = true,
                daemon = true
            )

            buildRepository.executeBuild(config).collect { progress ->
                _uiState.update { it.copy(currentProgress = progress) }
            }

            // Add to history
            val history = _uiState.value.buildHistory.toMutableList()
            val finalProgress = _uiState.value.currentProgress

            if (finalProgress != null) {
                val result = BuildResult(
                    id = finalProgress.buildId,
                    success = finalProgress.phase == BuildPhase.COMPLETED,
                    exitCode = if (finalProgress.phase == BuildPhase.COMPLETED) 0 else 1,
                    output = finalProgress.message,
                    durationMs = 0,
                    timestamp = System.currentTimeMillis(),
                    phase = finalProgress.phase
                )
                history.add(0, result)
                _uiState.update { it.copy(buildHistory = history.take(20)) }
            }

            _uiState.update { it.copy(isBuilding = false) }
        }
    }

    private fun cancelBuild() {
        buildJob?.cancel()
        _uiState.value.currentProgress?.let { progress ->
            viewModelScope.launch {
                buildRepository.cancelBuild(progress.buildId)
            }
        }
        _uiState.update { it.copy(isBuilding = false) }
    }

    private fun refreshGradleInfo() {
        viewModelScope.launch {
            val projectPath = _uiState.value.projectPath
            if (projectPath.isBlank()) {
                _uiState.update { it.copy(gradleInfo = null) }
                return@launch
            }

            buildRepository.getGradleInfo(projectPath)
                .onSuccess { info ->
                    _uiState.update { it.copy(gradleInfo = info) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            gradleInfo = GradleInfo(
                                available = false,
                                version = null,
                                homeDir = null,
                                daemonRunning = false
                            )
                        )
                    }
                }
        }
    }

    private fun loadBuildHistory() {
        viewModelScope.launch {
            buildRepository.getBuildHistory().collect { history ->
                _uiState.update { it.copy(buildHistory = history) }
            }
        }
    }
}