package com.pocketdev.data.remote

import com.pocketdev.domain.model.CollaborationEvent
import com.pocketdev.domain.model.Collaborator
import com.pocketdev.domain.model.CursorPosition
import com.pocketdev.domain.model.DocumentChange
import com.pocketdev.domain.model.TextOperation
import com.pocketdev.domain.model.TextSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Serializable
data class CollaborationMessage(
    val type: String,
    val sessionId: String? = null,
    val userId: String? = null,
    val data: Map<String, String>? = null
)

@Serializable
data class PresenceUpdate(
    val userId: String,
    val userName: String,
    val avatarUrl: String?,
    val color: Int,
    val cursorPosition: CursorPositionData?,
    val selection: TextSelectionData?
)

@Serializable
data class CursorPositionData(
    val line: Int,
    val column: Int
)

@Serializable
data class TextSelectionData(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int
)

@Singleton
class CollaborationWebSocket @Inject constructor(
    @Named("collaboration") private val okHttpClient: OkHttpClient
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var webSocket: WebSocket? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _events = MutableSharedFlow<CollaborationEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<CollaborationEvent> = _events.asSharedFlow()

    private val _collaborators = MutableStateFlow<List<Collaborator>>(emptyList())
    val collaborators: StateFlow<List<Collaborator>> = _collaborators.asStateFlow()

    private var currentSessionId: String? = null
    private var currentUserId: String? = null
    private var currentServerUrl: String? = null
    private val json = Json { ignoreUnknownKeys = true }

    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private val baseReconnectDelay = 1000L
    private val heartbeatInterval = 30_000L // 30 second ping
    private var heartbeatJob: kotlinx.coroutines.Job? = null

    sealed class ConnectionState {
        data object Disconnected : ConnectionState()
        data object Connecting : ConnectionState()
        data object Connected : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    fun connect(serverUrl: String, sessionId: String, userId: String) {
        if (_connectionState.value == ConnectionState.Connecting) return

        currentServerUrl = serverUrl
        currentSessionId = sessionId
        currentUserId = userId
        _connectionState.value = ConnectionState.Connecting
        reconnectAttempts = 0

        val request = Request.Builder()
            .url("$serverUrl/ws/session/$sessionId?userId=$userId")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _connectionState.value = ConnectionState.Connected
                reconnectAttempts = 0
                startHeartbeat()
                scope.launch {
                    _events.emit(CollaborationEvent.SyncCompleted(0))
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                stopHeartbeat()
                _connectionState.value = ConnectionState.Disconnected
                attemptReconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                stopHeartbeat()
                _connectionState.value = ConnectionState.Error(t.message ?: "Connection failed")
                attemptReconnect()
            }
        })
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (true) {
                delay(heartbeatInterval)
                if (_connectionState.value != ConnectionState.Connected) break
                send(CollaborationMessage(type = "ping"))
            }
        }
    }

    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    fun disconnect() {
        reconnectAttempts = maxReconnectAttempts
        stopHeartbeat()
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
        currentSessionId = null
        currentUserId = null
        currentServerUrl = null
        _collaborators.value = emptyList()
    }

    private fun attemptReconnect() {
        if (reconnectAttempts >= maxReconnectAttempts) return
        if (currentSessionId == null || currentUserId == null || currentServerUrl == null) return

        val delayMs = baseReconnectDelay * (1 shl reconnectAttempts)
        reconnectAttempts++

        scope.launch {
            delay(delayMs)
            currentServerUrl?.let { serverUrl ->
                currentSessionId?.let { sessionId ->
                    currentUserId?.let { userId ->
                        connect(serverUrl, sessionId, userId)
                    }
                }
            }
        }
    }

    private fun handleMessage(text: String) {
        scope.launch {
            try {
                val message = json.decodeFromString<CollaborationMessage>(text)
                when (message.type) {
                    "user_joined" -> {
                        message.data?.let { data ->
                            val userId = data["userId"]
                            val userName = data["userName"]
                            if (userId != null && userName != null) {
                                val collaborator = Collaborator(
                                    id = userId,
                                    name = userName,
                                    avatarUrl = data["avatarUrl"],
                                    color = data["color"]?.toIntOrNull() ?: 0xFF5722.toInt(),
                                    cursorPosition = null,
                                    selection = null,
                                    lastActivity = System.currentTimeMillis()
                                )
                                _collaborators.value = _collaborators.value + collaborator
                                _events.emit(CollaborationEvent.UserJoined(collaborator))
                            }
                        }
                    }
                    "user_left" -> {
                        val userId = message.data?.get("userId")
                        if (userId != null) {
                            _collaborators.value = _collaborators.value.filter { it.id != userId }
                            _events.emit(CollaborationEvent.UserLeft(userId))
                        }
                    }
                    "cursor_move" -> {
                        val userId = message.data?.get("userId")
                        val lineStr = message.data?.get("line")
                        val columnStr = message.data?.get("column")
                        if (userId != null && lineStr != null && columnStr != null) {
                            val line = lineStr.toIntOrNull()
                            val column = columnStr.toIntOrNull()
                            if (line != null && column != null) {
                                val position = CursorPosition(line, column)
                                _collaborators.value = _collaborators.value.map {
                                    if (it.id == userId) it.copy(cursorPosition = position, lastActivity = System.currentTimeMillis())
                                    else it
                                }
                                _events.emit(CollaborationEvent.CursorMoved(userId, position))
                            }
                        }
                    }
                    "selection_change" -> {
                        val userId = message.data?.get("userId")
                        if (userId != null) {
                            val startLine = message.data?.get("startLine")?.toIntOrNull()
                            val startColumn = message.data?.get("startColumn")?.toIntOrNull()
                            val endLine = message.data?.get("endLine")?.toIntOrNull()
                            val endColumn = message.data?.get("endColumn")?.toIntOrNull()

                            val selection = if (startLine != null && startColumn != null && endLine != null && endColumn != null) {
                                TextSelection(startLine, startColumn, endLine, endColumn)
                            } else null

                            _collaborators.value = _collaborators.value.map {
                                if (it.id == userId) it.copy(selection = selection, lastActivity = System.currentTimeMillis())
                                else it
                            }
                            _events.emit(CollaborationEvent.SelectionChanged(userId, selection))
                        }
                    }
                    "document_change" -> {
                        val change = parseDocumentChange(message)
                        if (change != null) {
                            _events.emit(CollaborationEvent.DocumentChanged(change))
                        }
                    }
                    "sync_complete" -> {
                        val version = message.data?.get("version")?.toLongOrNull() ?: 0L
                        _events.emit(CollaborationEvent.SyncCompleted(version))
                    }
                    "conflict" -> {
                        _events.emit(CollaborationEvent.Error("Conflict detected"))
                    }
                }
            } catch (e: Exception) {
                _events.emit(CollaborationEvent.Error("Failed to parse message: ${e.message}"))
            }
        }
    }

    private fun parseDocumentChange(message: CollaborationMessage): DocumentChange? {
        val data = message.data ?: return null
        val id = data["id"] ?: return null
        val sessionId = data["sessionId"] ?: return null
        val userId = data["userId"] ?: return null
        val version = data["version"]?.toLongOrNull() ?: return null
        val timestamp = data["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis()

        val ops = parseOperations(data["operations"] ?: return null)

        return DocumentChange(
            id = id,
            sessionId = sessionId,
            userId = userId,
            version = version,
            operations = ops,
            timestamp = timestamp
        )
    }

    private fun parseOperations(opsJson: String): List<TextOperation> {
        return try {
            json.decodeFromString<List<Map<String, Any>>>(opsJson).mapNotNull { op ->
                when (op["type"] as? String) {
                    "insert" -> TextOperation.Insert(
                        position = (op["position"] as? Number)?.toInt() ?: return@mapNotNull null,
                        text = op["text"] as? String ?: return@mapNotNull null
                    )
                    "delete" -> TextOperation.Delete(
                        position = (op["position"] as? Number)?.toInt() ?: return@mapNotNull null,
                        length = (op["length"] as? Number)?.toInt() ?: return@mapNotNull null
                    )
                    "replace" -> TextOperation.Replace(
                        position = (op["position"] as? Number)?.toInt() ?: return@mapNotNull null,
                        length = (op["length"] as? Number)?.toInt() ?: return@mapNotNull null,
                        text = op["text"] as? String ?: return@mapNotNull null
                    )
                    else -> null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun sendCursorPosition(position: CursorPosition) {
        val message = CollaborationMessage(
            type = "cursor_move",
            sessionId = currentSessionId,
            userId = currentUserId,
            data = mapOf(
                "line" to position.line.toString(),
                "column" to position.column.toString()
            )
        )
        send(message)
    }

    fun sendSelection(selection: TextSelection?) {
        val data = if (selection != null) {
            mapOf(
                "startLine" to selection.startLine.toString(),
                "startColumn" to selection.startColumn.toString(),
                "endLine" to selection.endLine.toString(),
                "endColumn" to selection.endColumn.toString()
            )
        } else {
            mapOf("clear" to "true")
        }

        val message = CollaborationMessage(
            type = "selection_change",
            sessionId = currentSessionId,
            userId = currentUserId,
            data = data
        )
        send(message)
    }

    fun sendDocumentChange(operations: List<TextOperation>, version: Long) {
        val opsData = operations.map { op ->
            when (op) {
                is TextOperation.Insert -> mapOf(
                    "type" to "insert",
                    "position" to op.position,
                    "text" to op.text
                )
                is TextOperation.Delete -> mapOf(
                    "type" to "delete",
                    "position" to op.position,
                    "length" to op.length
                )
                is TextOperation.Replace -> mapOf(
                    "type" to "replace",
                    "position" to op.position,
                    "length" to op.length,
                    "text" to op.text
                )
            }
        }

        val message = CollaborationMessage(
            type = "document_change",
            sessionId = currentSessionId,
            userId = currentUserId,
            data = mapOf(
                "operations" to json.encodeToString(opsData),
                "version" to version.toString()
            )
        )
        send(message)
    }

    private fun send(message: CollaborationMessage) {
        if (_connectionState.value != ConnectionState.Connected) return
        try {
            webSocket?.send(json.encodeToString(message))
        } catch (e: Exception) {
            scope.launch {
                _events.emit(CollaborationEvent.Error("Failed to send message: ${e.message}"))
            }
        }
    }
}
