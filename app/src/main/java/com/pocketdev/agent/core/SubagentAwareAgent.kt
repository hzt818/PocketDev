package com.pocketdev.agent.core

import com.pocketdev.agent.protocol.*

/**
 * Interface for agents that can spawn and coordinate subagents.
 *
 * Subagent-aware agents can:
 * - Dynamically spawn specialized subagents for specific tasks
 * - Coordinate multiple subagents in parallel
 * - Aggregate results from subagents
 * - Cancel subagents when needed
 *
 * This enables hierarchical agent architectures where a parent agent
 * orchestrates specialized child agents for complex workflows.
 */
interface SubagentAwareAgent : Agent {
    /**
     * The subagent executor for this agent.
     */
    val subagentExecutor: SubagentExecutor

    /**
     * The current parent task ID.
     */
    val parentTaskId: String

    /**
     * Spawn a single subagent for a specialized task.
     *
     * @param agentType Type of subagent to spawn
     * @param task Description of the task for the subagent
     * @param priority Priority of the subagent task
     * @return Result from the subagent execution
     */
    suspend fun spawnSubagent(
        agentType: AgentType,
        task: String,
        priority: SubagentPriority = SubagentPriority.NORMAL
    ): SubagentResult

    /**
     * Spawn multiple subagents for parallel execution.
     *
     * @param requests List of subagent requests
     * @param parallel If true, execute in parallel; if false, execute sequentially
     * @return List of results from all subagents
     */
    suspend fun spawnSubagents(
        requests: List<SubagentRequest>,
        parallel: Boolean = true
    ): List<SubagentResult>

    /**
     * Spawn multiple subagents with the same context but different tasks.
     *
     * @param agentType Type of subagent to spawn
     * @param tasks List of task descriptions
     * @param priority Priority of the subagent tasks
     * @param parallel If true, execute in parallel
     * @return List of results from all subagents
     */
    suspend fun spawnMultipleSubagents(
        agentType: AgentType,
        tasks: List<String>,
        priority: SubagentPriority = SubagentPriority.NORMAL,
        parallel: Boolean = true
    ): List<SubagentResult>

    /**
     * Cancel all active subagents for this agent.
     */
    fun cancelAllSubagents()

    /**
     * Get all subagent results collected so far.
     */
    fun getSubagentResults(): List<SubagentResult>

    /**
     * Get count of currently active subagents.
     */
    fun getActiveSubagentCount(): Int

    /**
     * Check if there are any active subagents.
     */
    fun hasActiveSubagents(): Boolean = getActiveSubagentCount() > 0
}

/**
 * Base implementation of SubagentAwareAgent.
 * Provides common functionality for spawning and managing subagents.
 */
abstract class BaseSubagentAwareAgent(
    override val subagentExecutor: SubagentExecutor,
    override val parentTaskId: String
) : SubagentAwareAgent {

    private val subagentResults = mutableListOf<SubagentResult>()

    override suspend fun spawnSubagent(
        agentType: AgentType,
        task: String,
        priority: SubagentPriority
    ): SubagentResult {
        val request = SubagentRequest(
            id = java.util.UUID.randomUUID().toString(),
            parentTaskId = parentTaskId,
            agentType = agentType,
            task = task,
            context = AgentContext(), // Override in subclass with proper context
            priority = priority
        )

        val result = subagentExecutor.executeSubagent(request)
        subagentResults.add(result)
        return result
    }

    override suspend fun spawnSubagents(
        requests: List<SubagentRequest>,
        parallel: Boolean
    ): List<SubagentResult> {
        val requestsWithParent = requests.map { request ->
            request.copy(parentTaskId = parentTaskId)
        }

        val results = subagentExecutor.executeSubagents(requestsWithParent, parallel)
        subagentResults.addAll(results)
        return results
    }

    override suspend fun spawnMultipleSubagents(
        agentType: AgentType,
        tasks: List<String>,
        priority: SubagentPriority,
        parallel: Boolean
    ): List<SubagentResult> {
        val requests = tasks.map { task ->
            SubagentRequest(
                id = java.util.UUID.randomUUID().toString(),
                parentTaskId = parentTaskId,
                agentType = agentType,
                task = task,
                context = AgentContext(),
                priority = priority
            )
        }

        return spawnSubagents(requests, parallel)
    }

    override fun cancelAllSubagents() {
        subagentExecutor.cancelAllSubagents(parentTaskId)
    }

    override fun getSubagentResults(): List<SubagentResult> {
        return subagentResults.toList()
    }

    override fun getActiveSubagentCount(): Int {
        return subagentExecutor.getActiveSubagentCount()
    }
}
