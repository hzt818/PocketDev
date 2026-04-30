package com.pocketdev.agent.core

import com.pocketdev.agent.protocol.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Executes subagents within a parent agent's context.
 *
 * Manages:
 * - Subagent lifecycle (creation, execution, cancellation)
 * - Parallel execution of multiple subagents
 * - Result aggregation
 * - Priority-based scheduling
 */
class SubagentExecutor(
    private val registry: SubagentRegistry,
    private val agentExecutor: AgentExecutor,
    private val config: SubagentExecutorConfig = SubagentExecutorConfig()
) {
    private val activeSubagents = ConcurrentHashMap<String, SubagentExecution>()
    private val subagentResults = ConcurrentHashMap<String, SubagentResult>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Execute a single subagent request.
     */
    suspend fun executeSubagent(request: SubagentRequest): SubagentResult {
        val execution = SubagentExecution(
            id = request.id,
            parentTaskId = request.parentTaskId,
            agentType = request.agentType,
            task = request.task,
            priority = request.priority,
            startTime = System.currentTimeMillis(),
            status = SubagentStatus.RUNNING
        )

        activeSubagents[request.id] = execution

        try {
            val result = withTimeout(config.subagentTimeout) {
                val response = agentExecutor.execute(
                    createAgentRequest(request)
                )

                SubagentResult(
                    requestId = request.id,
                    parentTaskId = request.parentTaskId,
                    agentType = request.agentType,
                    success = response.success,
                    findings = response.findings,
                    output = extractOutput(response),
                    executionTimeMs = System.currentTimeMillis() - execution.startTime
                )
            }

            subagentResults[request.id] = result
            activeSubagents[request.id] = execution.copy(
                endTime = System.currentTimeMillis(),
                status = SubagentStatus.COMPLETED
            )

            return result
        } catch (e: Exception) {
            val errorResult = SubagentResult(
                requestId = request.id,
                parentTaskId = request.parentTaskId,
                agentType = request.agentType,
                success = false,
                findings = emptyList(),
                error = e.message ?: "Subagent execution failed",
                executionTimeMs = System.currentTimeMillis() - execution.startTime
            )

            subagentResults[request.id] = errorResult
            activeSubagents[request.id] = execution.copy(
                endTime = System.currentTimeMillis(),
                status = SubagentStatus.FAILED
            )

            return errorResult
        }
    }

    /**
     * Execute multiple subagent requests.
     *
     * @param requests List of subagent requests
     * @param parallel If true, execute in parallel; if false, execute sequentially by priority
     */
    suspend fun executeSubagents(
        requests: List<SubagentRequest>,
        parallel: Boolean = true
    ): List<SubagentResult> {
        if (requests.isEmpty()) return emptyList()

        return if (parallel) {
            executeInParallel(requests)
        } else {
            executeSequentially(requests)
        }
    }

    /**
     * Execute subagents in parallel, up to maxConcurrent limit.
     */
    private suspend fun executeInParallel(requests: List<SubagentRequest>): List<SubagentResult> =
        coroutineScope {
            requests
                .sortedByDescending { it.priority.ordinal }
                .chunked(config.maxConcurrent)
                .flatMap { batch ->
                    batch.map { request ->
                        async {
                            executeSubagent(request)
                        }
                    }.awaitAll()
                }
        }

    /**
     * Execute subagents sequentially, sorted by priority.
     */
    private suspend fun executeSequentially(requests: List<SubagentRequest>): List<SubagentResult> {
        return requests
            .sortedByDescending { it.priority.ordinal }
            .map { executeSubagent(it) }
    }

    /**
     * Cancel a specific subagent by ID.
     */
    fun cancelSubagent(id: String) {
        activeSubagents[id]?.let { execution ->
            activeSubagents[id] = execution.copy(
                endTime = System.currentTimeMillis(),
                status = SubagentStatus.CANCELLED
            )
        }
        subagentResults.remove(id)
    }

    /**
     * Cancel all subagents for a parent task.
     */
    fun cancelAllSubagents(parentTaskId: String) {
        activeSubagents
            .filter { it.value.parentTaskId == parentTaskId }
            .keys
            .forEach { cancelSubagent(it) }
    }

    /**
     * Get all active subagents for a parent task.
     */
    fun getActiveSubagents(parentTaskId: String): List<SubagentExecution> {
        return activeSubagents.values
            .filter { it.parentTaskId == parentTaskId }
            .filter { it.status == SubagentStatus.RUNNING || it.status == SubagentStatus.PENDING }
    }

    /**
     * Get all subagent results for a parent task.
     */
    fun getSubagentResults(parentTaskId: String): List<SubagentResult> {
        return subagentResults.values
            .filter { it.parentTaskId == parentTaskId }
            .toList()
    }

    /**
     * Get count of currently active subagents.
     */
    fun getActiveSubagentCount(): Int {
        return activeSubagents.values.count {
            it.status == SubagentStatus.RUNNING || it.status == SubagentStatus.PENDING
        }
    }

    /**
     * Get total execution time for all subagents of a parent task.
     */
    fun getTotalExecutionTime(parentTaskId: String): Long {
        return subagentResults.values
            .filter { it.parentTaskId == parentTaskId }
            .sumOf { it.executionTimeMs }
    }

    /**
     * Clear completed subagents from tracking.
     */
    fun cleanup(parentTaskId: String) {
        activeSubagents
            .filter { it.value.parentTaskId == parentTaskId }
            .filter { it.value.status != SubagentStatus.RUNNING && it.value.status != SubagentStatus.PENDING }
            .keys
            .forEach { activeSubagents.remove(it) }
    }

    private fun createAgentRequest(request: SubagentRequest): AgentRequest {
        // Map subagent types to appropriate agent requests
        return when (request.agentType) {
            AgentType.FILE_SEARCH -> CodeAnalysisRequest(
                id = request.id,
                context = request.context.copy(
                    projectPath = request.context.projectPath,
                    relevantFiles = request.context.relevantFiles
                ),
                scope = AnalysisScope.CHANGED
            )
            AgentType.CODE_REVIEWER,
            AgentType.CODE_GENERATOR -> CodeAnalysisRequest(
                id = request.id,
                context = request.context,
                scope = AnalysisScope.FULL
            )
            AgentType.TEST_WRITER -> TDDGuideRequest(
                id = request.id,
                context = request.context,
                featureDescription = request.task
            )
            AgentType.BUILD_ERROR_RESOLVER -> BuildFixRequest(
                id = request.id,
                context = request.context,
                errorOutput = request.task
            )
            AgentType.SECURITY_REVIEWER -> SecurityReviewRequest(
                id = request.id,
                context = request.context
            )
            else -> ImplementationRequest(
                id = request.id,
                context = request.context,
                requirements = request.task
            )
        }
    }

    private fun extractOutput(response: AgentResponse): String {
        return when (response) {
            is CodeAnalysisResponse -> response.findings.joinToString("\n") { it.message }
            is ImplementationResponse -> response.plan?.phases?.joinToString("\n") { it.name } ?: ""
            is SecurityReviewResponse -> response.findings.joinToString("\n") { it.message }
            is BuildFixResponse -> response.explanation
            else -> ""
        }
    }
}

/**
 * Configuration for SubagentExecutor.
 */
data class SubagentExecutorConfig(
    val maxConcurrent: Int = 3,
    val subagentTimeout: Long = 60_000, // 1 minute per subagent
    val enablePriorityScheduling: Boolean = true
)
