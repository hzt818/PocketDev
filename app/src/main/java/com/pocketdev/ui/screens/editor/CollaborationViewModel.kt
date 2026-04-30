package com.pocketdev.ui.screens.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.CollaborationEvent
import com.pocketdev.domain.model.Collaborator
import com.pocketdev.domain.model.CollaborationState
import com.pocketdev.domain.model.CursorPosition
import com.pocketdev.domain.model.DocumentChange
import com.pocketdev.domain.model.TextOperation
import com.pocketdev.domain.repository.CollaborationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CollaborationViewModel @Inject constructor(
    private val collaborationRepository: CollaborationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollaborationState(null))
    val uiState: StateFlow<CollaborationState> = _uiState.asStateFlow()

    private var localVersion = 0L

    init {
        observeCollaborationEvents()
        observeConnectionState()
        observeCollaborators()
    }

    private fun observeCollaborationEvents() {
        viewModelScope.launch {
            collaborationRepository.events.collect { event ->
                when (event) {
                    is CollaborationEvent.UserJoined -> {
                        _uiState.update { state ->
                            state.copy(
                                collaborators = state.collaborators + event.collaborator
                            )
                        }
                    }
                    is CollaborationEvent.UserLeft -> {
                        _uiState.update { state ->
                            state.copy(
                                collaborators = state.collaborators.filter { it.id != event.userId }
                            )
                        }
                    }
                    is CollaborationEvent.CursorMoved -> {
                        _uiState.update { state ->
                            state.copy(
                                collaborators = state.collaborators.map {
                                    if (it.id == event.userId) {
                                        it.copy(cursorPosition = event.position)
                                    } else it
                                }
                            )
                        }
                    }
                    is CollaborationEvent.SelectionChanged -> {
                        _uiState.update { state ->
                            state.copy(
                                collaborators = state.collaborators.map {
                                    if (it.id == event.userId) {
                                        it.copy(selection = event.selection)
                                    } else it
                                }
                            )
                        }
                    }
                    is CollaborationEvent.DocumentChanged -> {
                        handleRemoteChange(event.change)
                    }
                    is CollaborationEvent.ConflictDetected -> {
                        _uiState.update {
                            it.copy(hasConflict = true, conflict = event.conflict)
                        }
                    }
                    is CollaborationEvent.SyncCompleted -> {
                        localVersion = event.version
                        _uiState.update { it.copy(isSyncing = false) }
                    }
                    is CollaborationEvent.Error -> {
                        _uiState.update { it.copy(isSyncing = false) }
                    }
                }
            }
        }
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            collaborationRepository.connectionState.collect { state ->
                _uiState.update {
                    it.copy(
                        isConnected = state is CollaborationRepository.ConnectionState.Connected,
                        isSyncing = state is CollaborationRepository.ConnectionState.Connecting
                    )
                }
            }
        }
    }

    private fun observeCollaborators() {
        viewModelScope.launch {
            collaborationRepository.collaborators.collect { collaborators ->
                _uiState.update { it.copy(collaborators = collaborators) }
            }
        }
    }

    private fun handleRemoteChange(change: DocumentChange) {
        _uiState.update { state ->
            state.copy(
                pendingChanges = state.pendingChanges + change,
                currentVersion = change.version
            )
        }
    }

    fun createSession(repositoryId: String, filePath: String, branch: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            collaborationRepository.createSession(repositoryId, filePath, branch)
        }
    }

    fun joinSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            collaborationRepository.joinSession(sessionId)
        }
    }

    fun leaveSession() {
        viewModelScope.launch {
            collaborationRepository.leaveSession()
            _uiState.update {
                CollaborationState(null)
            }
        }
    }

    fun sendCursorPosition(line: Int, column: Int) {
        viewModelScope.launch {
            collaborationRepository.sendCursorPosition(line, column)
        }
    }

    fun sendSelection(startLine: Int, startColumn: Int, endLine: Int, endColumn: Int) {
        viewModelScope.launch {
            collaborationRepository.sendSelection(startLine, startColumn, endLine, endColumn)
        }
    }

    fun sendLocalChange(operations: List<TextOperation>) {
        localVersion++

        val change = DocumentChange(
            id = UUID.randomUUID().toString(),
            sessionId = _uiState.value.session?.id ?: "",
            userId = "",
            version = localVersion,
            operations = operations,
            timestamp = System.currentTimeMillis()
        )

        _uiState.update { state ->
            state.copy(
                pendingChanges = state.pendingChanges + change,
                isSyncing = true
            )
        }

        viewModelScope.launch {
            collaborationRepository.sendDocumentChange(change)
        }
    }

    fun resolveConflict(resolution: com.pocketdev.domain.model.ConflictResolutionType) {
        _uiState.update { state ->
            state.copy(
                hasConflict = false,
                conflict = state.conflict?.copy(
                    resolution = resolution,
                    resolvedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
