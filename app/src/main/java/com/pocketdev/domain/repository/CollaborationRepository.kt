package com.pocketdev.domain.repository

import com.pocketdev.domain.model.CollaborationEvent
import com.pocketdev.domain.model.CollaborationSession
import com.pocketdev.domain.model.Collaborator
import com.pocketdev.domain.model.DocumentChange
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface CollaborationRepository {
    val connectionState: StateFlow<ConnectionState>
    val collaborators: StateFlow<List<Collaborator>>
    val events: SharedFlow<CollaborationEvent>

    suspend fun createSession(repositoryId: String, filePath: String, branch: String): Result<CollaborationSession>
    suspend fun joinSession(sessionId: String): Result<CollaborationSession>
    suspend fun leaveSession()
    suspend fun sendCursorPosition(line: Int, column: Int)
    suspend fun sendSelection(startLine: Int, startColumn: Int, endLine: Int, endColumn: Int)
    suspend fun sendDocumentChange(change: DocumentChange)
    fun isConnected(): Boolean

    sealed class ConnectionState {
        data object Disconnected : ConnectionState()
        data object Connecting : ConnectionState()
        data object Connected : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }
}
