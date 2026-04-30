package com.pocketdev.domain.repository

import com.pocketdev.domain.model.TerminalSession
import com.pocketdev.domain.model.TerminalSize
import com.pocketdev.domain.model.TerminalOutput
import com.pocketdev.domain.model.ShellType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for terminal session management
 */
interface TerminalRepository {
    /**
     * Creates a new terminal session
     * @param cwd Working directory for the session, null uses default
     * @param shellType The type of shell (LOCAL or REMOTE)
     * @return Result containing the created session or failure
     */
    suspend fun createSession(cwd: String? = null, shellType: ShellType = ShellType.LOCAL): Result<TerminalSession>

    /**
     * Write input to an active terminal session
     * @param sessionId The session identifier
     * @param input The input string to write
     */
    suspend fun writeToSession(sessionId: String, input: String)

    /**
     * Flow of output from a specific terminal session
     * @param sessionId The session identifier
     * @return Flow emitting TerminalOutput as data arrives
     */
    fun readOutput(sessionId: String): Flow<TerminalOutput>

    /**
     * Resize a terminal session
     * @param sessionId The session identifier
     * @param size The new terminal size
     */
    suspend fun resize(sessionId: String, size: TerminalSize)

    /**
     * Close and cleanup a terminal session
     * @param sessionId The session identifier
     */
    suspend fun closeSession(sessionId: String)

    /**
     * Flow of all active terminal sessions
     * @return Flow emitting list of active sessions
     */
    fun getActiveSessions(): Flow<List<TerminalSession>>
}