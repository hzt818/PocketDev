package com.pocketdev.data.repository

import com.pocketdev.data.remote.CollaborationWebSocket
import com.pocketdev.domain.model.CollaborationEvent
import com.pocketdev.domain.model.CollaborationSession
import com.pocketdev.domain.model.Collaborator
import com.pocketdev.domain.model.DocumentChange
import com.pocketdev.domain.repository.CollaborationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollaborationRepositoryImpl @Inject constructor(
    private val collaborationWebSocket: CollaborationWebSocket
) : CollaborationRepository {

    private val _connectionState = MutableStateFlow<CollaborationRepository.ConnectionState>(
        CollaborationRepository.ConnectionState.Disconnected
    )

    override val connectionState: StateFlow<CollaborationRepository.ConnectionState>
        get() = collaborationWebSocket.connectionState.map { wsState ->
            when (wsState) {
                is CollaborationWebSocket.ConnectionState.Disconnected ->
                    CollaborationRepository.ConnectionState.Disconnected
                is CollaborationWebSocket.ConnectionState.Connecting ->
                    CollaborationRepository.ConnectionState.Connecting
                is CollaborationWebSocket.ConnectionState.Connected ->
                    CollaborationRepository.ConnectionState.Connected
                is CollaborationWebSocket.ConnectionState.Error ->
                    CollaborationRepository.ConnectionState.Error(wsState.message)
            }
        }.stateIn(
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default + kotlinx.coroutines.SupervisorJob()),
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = CollaborationRepository.ConnectionState.Disconnected
        )

    override val collaborators: StateFlow<List<Collaborator>>
        get() = collaborationWebSocket.collaborators

    override val events: SharedFlow<CollaborationEvent>
        get() = collaborationWebSocket.events

    private var currentSession: CollaborationSession? = null
    private var currentUserId: String? = null

    override suspend fun createSession(
        repositoryId: String,
        filePath: String,
        branch: String
    ): Result<CollaborationSession> {
        return try {
            val sessionId = generateSessionId(repositoryId, filePath, branch)
            val userId = generateUserId()

            currentSession = CollaborationSession(
                id = sessionId,
                repositoryId = repositoryId,
                filePath = filePath,
                branch = branch,
                ownerId = userId,
                createdAt = System.currentTimeMillis(),
                isActive = true
            )
            currentUserId = userId

            collaborationWebSocket.connect(
                serverUrl = "wss://collab.pocketdev.local",
                sessionId = sessionId,
                userId = userId
            )

            Result.success(currentSession!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinSession(sessionId: String): Result<CollaborationSession> {
        return try {
            val userId = generateUserId()
            currentUserId = userId

            collaborationWebSocket.connect(
                serverUrl = "wss://collab.pocketdev.local",
                sessionId = sessionId,
                userId = userId
            )

            currentSession = CollaborationSession(
                id = sessionId,
                repositoryId = "",
                filePath = "",
                branch = "",
                ownerId = "",
                createdAt = System.currentTimeMillis(),
                isActive = true
            )

            Result.success(currentSession!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun leaveSession() {
        collaborationWebSocket.disconnect()
        currentSession = null
        currentUserId = null
    }

    override suspend fun sendCursorPosition(line: Int, column: Int) {
        collaborationWebSocket.sendCursorPosition(
            com.pocketdev.domain.model.CursorPosition(line, column)
        )
    }

    override suspend fun sendSelection(
        startLine: Int,
        startColumn: Int,
        endLine: Int,
        endColumn: Int
    ) {
        collaborationWebSocket.sendSelection(
            com.pocketdev.domain.model.TextSelection(startLine, startColumn, endLine, endColumn)
        )
    }

    override suspend fun sendDocumentChange(change: DocumentChange) {
        collaborationWebSocket.sendDocumentChange(change.operations, change.version)
    }

    override fun isConnected(): Boolean {
        return collaborationWebSocket.connectionState.value ==
            CollaborationWebSocket.ConnectionState.Connected
    }

    private fun generateSessionId(repositoryId: String, filePath: String, branch: String): String {
        val data = "$repositoryId:$filePath:$branch:${System.currentTimeMillis()}"
        return java.util.Base64.getEncoder().encodeToString(data.toByteArray())
    }

    private fun generateUserId(): String {
        return "user_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}
