package com.pocketdev.agent.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Subagent Protocol - Types for subagent communication and lifecycle management
 *
 * Subagents are lightweight, task-scoped agents spawned by parent agents
 * to handle specific, specialized subtasks in parallel.
 */

/**
 * Request to spawn a subagent for a specific task.
 */
@Serializable
data class SubagentRequest(
    val id: String,
    val parentTaskId: String,
    val agentType: AgentType,
    val task: String,
    val context: AgentContext,
    val priority: SubagentPriority = SubagentPriority.NORMAL
)

/**
 * Priority levels for subagent task execution.
 * Higher priority tasks are executed first.
 */
@Serializable
enum class SubagentPriority {
    @SerialName("low")
    LOW,

    @SerialName("normal")
    NORMAL,

    @SerialName("high")
    HIGH,

    @SerialName("critical")
    CRITICAL
}

/**
 * Result from a completed subagent execution.
 */
@Serializable
data class SubagentResult(
    val requestId: String,
    val parentTaskId: String,
    val agentType: AgentType,
    val success: Boolean,
    val findings: List<Finding> = emptyList(),
    val output: String = "",
    val error: String? = null,
    val executionTimeMs: Long = 0
)

/**
 * Tracks the execution state of a subagent.
 */
@Serializable
data class SubagentExecution(
    val id: String,
    val parentTaskId: String,
    val agentType: AgentType,
    val task: String,
    val priority: SubagentPriority,
    val startTime: Long,
    val endTime: Long = 0,
    val status: SubagentStatus = SubagentStatus.PENDING
) {
    val durationMs: Long get() = if (endTime > 0) endTime - startTime else 0
}

/**
 * Status of a subagent execution.
 */
@Serializable
enum class SubagentStatus {
    @SerialName("pending")
    PENDING,

    @SerialName("running")
    RUNNING,

    @SerialName("completed")
    COMPLETED,

    @SerialName("failed")
    FAILED,

    @SerialName("cancelled")
    CANCELLED
}

/**
 * Response from a subagent-enabled agent with subagent results.
 */
@Serializable
data class SubagentResponse(
    override val requestId: String,
    override val success: Boolean,
    override val findings: List<Finding>,
    val subagentResults: List<SubagentResult> = emptyList(),
    val totalSubagentTimeMs: Long = 0
) : AgentResponse()

/**
 * Collection of results from multiple subagents.
 */
@Serializable
data class SubagentResultCollection(
    val parentTaskId: String,
    val results: List<SubagentResult>,
    val totalTimeMs: Long = 0,
    val success: Boolean = results.all { it.success }
) {
    val allFindings: List<Finding> get() = results.flatMap { it.findings }
    val successfulCount: Int get() = results.count { it.success }
    val failedCount: Int get() = results.count { !it.success }
}
