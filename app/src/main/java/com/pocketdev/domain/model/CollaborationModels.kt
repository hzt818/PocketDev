package com.pocketdev.domain.model

data class CollaborationSession(
    val id: String,
    val repositoryId: String,
    val filePath: String,
    val branch: String,
    val ownerId: String,
    val createdAt: Long,
    val isActive: Boolean
)

data class Collaborator(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val color: Int,
    val cursorPosition: CursorPosition?,
    val selection: TextSelection?,
    val lastActivity: Long
)

data class CursorPosition(
    val line: Int,
    val column: Int
)

data class TextSelection(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int
)

data class DocumentChange(
    val id: String,
    val sessionId: String,
    val userId: String,
    val version: Long,
    val operations: List<TextOperation>,
    val timestamp: Long
)

sealed class TextOperation {
    data class Insert(
        val position: Int,
        val text: String
    ) : TextOperation()

    data class Delete(
        val position: Int,
        val length: Int
    ) : TextOperation()

    data class Replace(
        val position: Int,
        val length: Int,
        val text: String
    ) : TextOperation()
}

data class ChangeVector(
    val version: Long,
    val userId: String,
    val timestamp: Long
)

data class ConflictResolution(
    val type: ConflictType,
    val filePath: String,
    val localChange: DocumentChange?,
    val remoteChange: DocumentChange?,
    val resolution: ConflictResolutionType?,
    val resolvedAt: Long?
)

enum class ConflictType {
    CONCURRENT_EDIT,
    BRANCH_DIVERGENCE,
    FILE_DELETED,
    FILE_MODIFIED
}

enum class ConflictResolutionType {
    ACCEPT_LOCAL,
    ACCEPT_REMOTE,
    ACCEPT_MERGE,
    MANUAL_RESOLUTION
}

sealed class CollaborationEvent {
    data class UserJoined(val collaborator: Collaborator) : CollaborationEvent()
    data class UserLeft(val userId: String) : CollaborationEvent()
    data class CursorMoved(val userId: String, val position: CursorPosition) : CollaborationEvent()
    data class SelectionChanged(val userId: String, val selection: TextSelection?) : CollaborationEvent()
    data class DocumentChanged(val change: DocumentChange) : CollaborationEvent()
    data class ConflictDetected(val conflict: ConflictResolution) : CollaborationEvent()
    data class SyncCompleted(val version: Long) : CollaborationEvent()
    data class Error(val message: String) : CollaborationEvent()
}

data class CollaborationState(
    val session: CollaborationSession?,
    val collaborators: List<Collaborator> = emptyList(),
    val pendingChanges: List<DocumentChange> = emptyList(),
    val currentVersion: Long = 0,
    val hasConflict: Boolean = false,
    val conflict: ConflictResolution? = null,
    val isConnected: Boolean = false,
    val isSyncing: Boolean = false
)
