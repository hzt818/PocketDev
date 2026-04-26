package com.pocketdev.agent.core

import com.pocketdev.agent.protocol.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import com.pocketdev.agent.protocol.SubagentRequest
import com.pocketdev.agent.protocol.SubagentResult

/**
 * AgentExecutor - Manages execution of agents and task orchestration.
 *
 * Features:
 * - Async agent execution with timeout
 * - Task dependency graph
 * - Parallel execution for independent tasks
 * - Execution history and metrics
 * - Error recovery and retry logic
 */
class AgentExecutor(
    private val registry: AgentRegistry,
    private val config: ExecutorConfig = ExecutorConfig()
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val executions = ConcurrentHashMap<String, AgentExecution>()
    private val taskQueue = MutableSharedFlow<AgentTask>(extraBufferCapacity = 64)

    val executionHistory: Flow<List<AgentExecution>> = flow {
        emit(executions.values.toList())
    }

    suspend fun execute(request: AgentRequest): AgentResponse = withTimeout(config.defaultTimeout) {
        val execution = startExecution(request)
        try {
            val agent = registry.getAgent(request.agentType)
                ?: throw AgentNotFoundException(request.agentType)

            val response = agent.execute(request)
            completeExecution(execution, response)
            response
        } catch (e: Exception) {
            val errorResponse = createErrorResponse(request, e)
            completeExecution(execution, errorResponse)
            errorResponse
        }
    }

    suspend fun executeParallel(requests: List<AgentRequest>): List<AgentResponse> =
        coroutineScope {
            requests.map { request ->
                async { execute(request) }
            }.awaitAll()
        }

    suspend fun executeWithRetry(
        request: AgentRequest,
        maxRetries: Int = config.maxRetries
    ): AgentResponse {
        var lastError: Exception? = null
        var lastResponse: AgentResponse? = null

        repeat(maxRetries) { attempt ->
            try {
                lastResponse = execute(request)
                return@repeat
            } catch (e: Exception) {
                lastError = e
                if (attempt < maxRetries - 1) {
                    delay(config.retryDelayMs * (attempt + 1))
                }
            }
        }

        return lastResponse ?: throw lastError ?: Exception("Max retries exceeded")
    }

    suspend fun executeTaskGraph(tasks: List<AgentTask>): List<AgentResponse> {
        val results = mutableListOf<AgentResponse>()
        val completed = mutableSetOf<String>()

        while (completed.size < tasks.size) {
            val readyTasks = tasks.filter { task ->
                task.dependencies.all { dep -> completed.contains(dep) } && !completed.contains(task.id)
            }

            if (readyTasks.isEmpty() && completed.size < tasks.size) {
                throw Exception("Circular dependency detected or missing dependencies")
            }

            val parallelReady = readyTasks.filter { it.parallelizable }
            val responses = executeParallel(parallelReady.map { it.request })

            responses.forEachIndexed { index, response ->
                results.add(response)
                completed.add(parallelReady[index].id)
            }

            val sequentialReady = readyTasks.filter { !it.parallelizable }
            for (task in sequentialReady) {
                val response = execute(task.request)
                results.add(response)
                completed.add(task.id)
            }
        }

        return results
    }

    fun getExecution(id: String): AgentExecution? = executions[id]

    fun getExecutionHistory(): List<AgentExecution> = executions.values.toList()

    fun cancelExecution(id: String) {
        executions[id]?.let { execution ->
            // Cancellation handled by structured concurrency
        }
    }

    /**
     * Execute a request as a subagent (lightweight, no execution tracking).
     */
    suspend fun executeAsSubagent(request: SubagentRequest): SubagentResult {
        val startTime = System.currentTimeMillis()

        try {
            val agent = registry.getAgent(request.agentType)
                ?: return createSubagentErrorResult(request, "Agent not found: ${request.agentType}")

            val response = withTimeout(config.defaultTimeout) {
                agent.execute(
                    ImplementationRequest(
                        id = request.id,
                        context = request.context,
                        requirements = request.task
                    )
                )
            }

            return SubagentResult(
                requestId = request.id,
                parentTaskId = request.parentTaskId,
                agentType = request.agentType,
                success = response.success,
                findings = response.findings,
                output = extractFindingsAsOutput(response),
                executionTimeMs = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            return createSubagentErrorResult(
                request,
                e.message ?: "Subagent execution failed",
                System.currentTimeMillis() - startTime
            )
        }
    }

    private fun createSubagentErrorResult(
        request: SubagentRequest,
        error: String,
        executionTimeMs: Long = 0
    ): SubagentResult {
        return SubagentResult(
            requestId = request.id,
            parentTaskId = request.parentTaskId,
            agentType = request.agentType,
            success = false,
            findings = listOf(
                Finding(
                    type = FindingType.BUG,
                    severity = Severity.HIGH,
                    file = "",
                    message = error
                )
            ),
            error = error,
            executionTimeMs = executionTimeMs
        )
    }

    private fun extractFindingsAsOutput(response: AgentResponse): String {
        return response.findings.joinToString("\n") { finding ->
            "${finding.severity.name}: ${finding.message}"
        }
    }

    fun getActiveSubagentCount(): Int = 0 // Tracked by SubagentExecutor

    private fun startExecution(request: AgentRequest): AgentExecution {
        val execution = AgentExecution(
            agentType = request.agentType,
            requestId = request.id,
            startTime = System.currentTimeMillis(),
            endTime = 0,
            success = false,
            response = null
        )
        executions[request.id] = execution
        return execution
    }

    private fun completeExecution(execution: AgentExecution, response: AgentResponse) {
        executions[execution.requestId] = execution.copy(
            endTime = System.currentTimeMillis(),
            success = response.success,
            response = response
        )
    }

    private fun createErrorResponse(request: AgentRequest, error: Exception): AgentResponse {
        return when (request.agentType) {
            AgentType.CODE_REVIEWER -> CodeAnalysisResponse(
                requestId = request.id,
                success = false,
                findings = listOf(
                    Finding(
                        type = FindingType.BUG,
                        severity = Severity.HIGH,
                        file = "",
                        message = "Analysis failed: ${error.message}"
                    )
                )
            )
            AgentType.BUILD_ERROR_RESOLVER -> BuildFixResponse(
                requestId = request.id,
                success = false,
                findings = emptyList(),
                explanation = "Build fix failed: ${error.message}"
            )
            else -> CodeAnalysisResponse(
                requestId = request.id,
                success = false,
                findings = listOf(
                    Finding(
                        type = FindingType.BUG,
                        severity = Severity.HIGH,
                        file = "",
                        message = "${request.agentType} failed: ${error.message}"
                    )
                )
            )
        }
    }
}

/**
 * Configuration for AgentExecutor.
 */
data class ExecutorConfig(
    val defaultTimeout: Long = 120_000, // 2 minutes
    val maxRetries: Int = 3,
    val retryDelayMs: Long = 1000,
    val maxParallelAgents: Int = 4
)

/**
 * Represents a task in the execution graph.
 */
data class AgentTask(
    val id: String,
    val request: AgentRequest,
    val dependencies: List<String> = emptyList(),
    val parallelizable: Boolean = true
) {
    companion object {
        fun fromRequest(request: AgentRequest, dependencies: List<String> = emptyList()): AgentTask {
            return AgentTask(
                id = request.id,
                request = request,
                dependencies = dependencies
            )
        }
    }
}

/**
 * Exception thrown when an agent is not found.
 */
class AgentNotFoundException(type: AgentType) : Exception("Agent not found: $type")

/**
 * Builder for creating agent task graphs.
 */
class TaskGraphBuilder {
    private val tasks = mutableListOf<AgentTask>()

    fun addTask(request: AgentRequest, dependencies: List<String> = emptyList()): TaskGraphBuilder {
        tasks.add(AgentTask.fromRequest(request, dependencies))
        return this
    }

    fun build(): List<AgentTask> = tasks.toList()
}
