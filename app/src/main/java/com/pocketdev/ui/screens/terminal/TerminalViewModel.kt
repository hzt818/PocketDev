package com.pocketdev.ui.screens.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.TerminalOutput
import com.pocketdev.domain.model.TerminalSession
import com.pocketdev.domain.model.TerminalSize
import com.pocketdev.domain.model.ShellType
import com.pocketdev.domain.repository.TerminalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TerminalUiState(
    val sessions: List<TerminalSession> = emptyList(),
    val activeSessionId: String? = null,
    val outputs: Map<String, List<TerminalOutput>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedShellType: ShellType = ShellType.LOCAL,
    val commandHistory: List<String> = emptyList(),
    val historyIndex: Int = -1
)

sealed class TerminalEvent {
    data object CreateSession : TerminalEvent()
    data class SendInput(val input: String) : TerminalEvent()
    data class CloseSession(val sessionId: String) : TerminalEvent()
    data class Resize(val rows: Int, val cols: Int) : TerminalEvent()
    data class SelectSession(val sessionId: String) : TerminalEvent()
    data object ClearError : TerminalEvent()
    data class SetShellType(val shellType: ShellType) : TerminalEvent()
    data object HistoryPrevious : TerminalEvent()
    data object HistoryNext : TerminalEvent()
}

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val terminalRepository: TerminalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TerminalUiState())
    val uiState: StateFlow<TerminalUiState> = _uiState.asStateFlow()

    init {
        observeActiveSessions()
    }

    private fun observeActiveSessions() {
        viewModelScope.launch {
            terminalRepository.getActiveSessions().collect { sessions ->
                _uiState.update { it.copy(sessions = sessions) }
            }
        }
    }

    fun onEvent(event: TerminalEvent) {
        when (event) {
            is TerminalEvent.CreateSession -> createSession()
            is TerminalEvent.SendInput -> sendInput(event.input)
            is TerminalEvent.CloseSession -> closeSession(event.sessionId)
            is TerminalEvent.Resize -> resize(event.rows, event.cols)
            is TerminalEvent.SelectSession -> selectSession(event.sessionId)
            is TerminalEvent.ClearError -> clearError()
            is TerminalEvent.SetShellType -> setShellType(event.shellType)
            is TerminalEvent.HistoryPrevious -> historyPrevious()
            is TerminalEvent.HistoryNext -> historyNext()
        }
    }

    private fun setShellType(shellType: ShellType) {
        _uiState.update { it.copy(selectedShellType = shellType) }
    }

    private fun createSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val shellType = _uiState.value.selectedShellType
            val result = terminalRepository.createSession(shellType = shellType)
            result.fold(
                onSuccess = { session ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            activeSessionId = session.id,
                            outputs = it.outputs + (session.id to emptyList())
                        )
                    }
                    startOutputCollection(session.id)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to create session"
                        )
                    }
                }
            )
        }
    }

    private fun startOutputCollection(sessionId: String) {
        viewModelScope.launch {
            terminalRepository.readOutput(sessionId).collect { output ->
                _uiState.update { state ->
                    val sessionOutputs = state.outputs[sessionId] ?: emptyList()
                    state.copy(outputs = state.outputs + (sessionId to sessionOutputs + output))
                }
            }
        }
    }

    private fun sendInput(input: String) {
        val sessionId = _uiState.value.activeSessionId ?: return

        viewModelScope.launch {
            try {
                // Add to command history if not empty and different from last command
                if (input.isNotBlank()) {
                    val currentHistory = _uiState.value.commandHistory
                    if (currentHistory.isEmpty() || currentHistory.last() != input) {
                        _uiState.update {
                            it.copy(
                                commandHistory = it.commandHistory + input,
                                historyIndex = -1
                            )
                        }
                    }
                }
                terminalRepository.writeToSession(sessionId, input)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Failed to send input")
                }
            }
        }
    }

    private fun historyPrevious() {
        val history = _uiState.value.commandHistory
        if (history.isEmpty()) return

        val newIndex = if (_uiState.value.historyIndex == -1) {
            history.lastIndex
        } else {
            (_uiState.value.historyIndex - 1).coerceAtLeast(0)
        }

        _uiState.update { it.copy(historyIndex = newIndex) }
    }

    private fun historyNext() {
        val history = _uiState.value.commandHistory
        if (history.isEmpty()) return

        val newIndex = if (_uiState.value.historyIndex == -1) {
            return
        } else if (_uiState.value.historyIndex >= history.lastIndex) {
            -1
        } else {
            _uiState.value.historyIndex + 1
        }

        _uiState.update { it.copy(historyIndex = newIndex) }
    }

    private fun closeSession(sessionId: String) {
        viewModelScope.launch {
            try {
                terminalRepository.closeSession(sessionId)
                _uiState.update { state ->
                    val newOutputs = state.outputs.toMutableMap()
                    newOutputs.remove(sessionId)
                    val newActiveId = if (state.activeSessionId == sessionId) {
                        newOutputs.keys.firstOrNull()
                    } else {
                        state.activeSessionId
                    }
                    state.copy(
                        outputs = newOutputs,
                        activeSessionId = newActiveId
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Failed to close session")
                }
            }
        }
    }

    private fun resize(rows: Int, cols: Int) {
        val sessionId = _uiState.value.activeSessionId ?: return

        viewModelScope.launch {
            try {
                terminalRepository.resize(sessionId, TerminalSize(rows, cols))
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Failed to resize terminal")
                }
            }
        }
    }

    private fun selectSession(sessionId: String) {
        _uiState.update { it.copy(activeSessionId = sessionId) }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}