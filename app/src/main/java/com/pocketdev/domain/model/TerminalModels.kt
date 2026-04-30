package com.pocketdev.domain.model

/**
 * Represents the type of shell for a terminal session
 */
enum class ShellType {
    LOCAL,
    REMOTE
}

/**
 * Represents an active terminal session
 */
data class TerminalSession(
    val id: String,
    val pid: Int,
    val cwd: String,
    val startedAt: Long,
    val isActive: Boolean,
    val shellType: ShellType = ShellType.LOCAL
)

/**
 * Represents terminal dimensions for resize operations
 */
data class TerminalSize(
    val rows: Int,
    val cols: Int
)

/**
 * Represents output from a terminal session
 */
data class TerminalOutput(
    val sessionId: String,
    val data: String,
    val isError: Boolean = false,
    val timestamp: Long
)