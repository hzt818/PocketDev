package com.pocketdev.agent

import com.pocketdev.agent.agents.*
import com.pocketdev.agent.core.*
import com.pocketdev.agent.protocol.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AgentService - Main interface for agent operations in the app.
 *
 * Provides:
 * - Single entry point for all agent operations
 * - Pre-configured agents
 * - Subagent execution support
 * - Loop execution for autonomous tasks
 * - Execution history and metrics
 */
@Singleton
class AgentService @Inject constructor() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val registry = DefaultAgentRegistry()
    private val executor = AgentExecutor(registry)
    private val subagentRegistry = DefaultSubagentRegistry()
    private val subagentExecutor = SubagentExecutor(subagentRegistry, executor)
    private val loopOperator = AgentLoopOperator(executor)

    init {
        registerDefaultAgents()
    }

    private fun registerDefaultAgents() {
        registry.registerAgent(CodeReviewerAgent())
        registry.registerAgent(TDDGuideAgent())
        registry.registerAgent(SecurityReviewerAgent())
        registry.registerAgent(BuildErrorResolverAgent())
        registry.registerAgent(PlannerAgent())
    }

    /**
     * Execute a single agent request.
     */
    suspend fun execute(request: AgentRequest): AgentResponse {
        return executor.execute(request)
    }

    /**
     * Execute multiple requests in parallel.
     */
    suspend fun executeParallel(requests: List<AgentRequest>): List<AgentResponse> {
        return executor.executeParallel(requests)
    }

    /**
     * Execute a task graph with dependencies.
     */
    suspend fun executeTaskGraph(tasks: List<AgentTask>): List<AgentResponse> {
        return executor.executeTaskGraph(tasks)
    }

    /**
     * Execute a subagent request.
     */
    suspend fun executeSubagent(request: SubagentRequest): SubagentResult {
        return subagentExecutor.executeSubagent(request)
    }

    /**
     * Execute multiple subagent requests.
     */
    suspend fun executeSubagents(
        requests: List<SubagentRequest>,
        parallel: Boolean = true
    ): List<SubagentResult> {
        return subagentExecutor.executeSubagents(requests, parallel)
    }

    /**
     * Start an autonomous loop for complex tasks.
     */
    suspend fun startAutonomousLoop(goal: String, context: AgentContext): Flow<LoopProgress> {
        return loopOperator.startLoop(goal, context)
    }

    /**
     * Stop the current autonomous loop.
     */
    fun stopAutonomousLoop() {
        loopOperator.stopLoop()
    }

    /**
     * Get all available agent types.
     */
    fun getAvailableAgents(): List<AgentType> {
        return registry.getAllAgents().map { it.type }
    }

    /**
     * Get all available subagent types.
     */
    fun getAvailableSubagents(): List<SubagentDefinition> {
        return subagentRegistry.getAllSubagents()
    }

    /**
     * Get execution history.
     */
    fun getExecutionHistory(): List<AgentExecution> {
        return executor.getExecutionHistory()
    }

    /**
     * Quick code review.
     */
    suspend fun quickCodeReview(files: List<String>): CodeAnalysisResponse {
        val request = CodeAnalysisRequest(
            id = java.util.UUID.randomUUID().toString(),
            context = AgentContext(relevantFiles = files)
        )
        return execute(request) as CodeAnalysisResponse
    }

    /**
     * Quick security check.
     */
    suspend fun quickSecurityCheck(files: List<String>): SecurityReviewResponse {
        val request = SecurityReviewRequest(
            id = java.util.UUID.randomUUID().toString(),
            context = AgentContext(relevantFiles = files)
        )
        return execute(request) as SecurityReviewResponse
    }

    /**
     * Fix build errors.
     */
    suspend fun fixBuildErrors(errorOutput: String): BuildFixResponse {
        val request = BuildFixRequest(
            id = java.util.UUID.randomUUID().toString(),
            context = AgentContext(),
            errorOutput = errorOutput
        )
        return execute(request) as BuildFixResponse
    }

    /**
     * Create implementation plan.
     */
    suspend fun createPlan(requirements: String, constraints: List<String> = emptyList()): ImplementationResponse {
        val request = ImplementationRequest(
            id = java.util.UUID.randomUUID().toString(),
            requirements = requirements,
            constraints = constraints,
            context = AgentContext()
        )
        return execute(request) as ImplementationResponse
    }

    /**
     * Guide TDD workflow.
     */
    suspend fun guideTDD(featureDescription: String): ImplementationResponse {
        val request = TDDGuideRequest(
            id = java.util.UUID.randomUUID().toString(),
            featureDescription = featureDescription,
            context = AgentContext()
        )
        return execute(request) as ImplementationResponse
    }
}

/**
 * Extension functions for common agent operations.
 */
object AgentExtensions {

    /**
     * Creates a task graph builder with initial request.
     */
    fun taskGraph(startRequest: AgentRequest): TaskGraphBuilder {
        return TaskGraphBuilder().addTask(startRequest)
    }

    /**
     * Adds sequential dependency.
     */
    fun sequential(tasks: List<AgentTask>): List<AgentTask> {
        return tasks.mapIndexed { index, task ->
            if (index > 0) {
                task.copy(
                    dependencies = listOf(tasks[index - 1].id),
                    parallelizable = false
                )
            } else {
                task
            }
        }
    }

    /**
     * Adds parallel execution hint.
     */
    fun parallel(tasks: List<AgentTask>): List<AgentTask> {
        return tasks.map { it.copy(parallelizable = true) }
    }

    /**
     * Creates a subagent request.
     */
    fun subagentRequest(
        parentTaskId: String,
        agentType: AgentType,
        task: String,
        context: AgentContext,
        priority: SubagentPriority = SubagentPriority.NORMAL
    ): SubagentRequest {
        return SubagentRequest(
            id = java.util.UUID.randomUUID().toString(),
            parentTaskId = parentTaskId,
            agentType = agentType,
            task = task,
            context = context,
            priority = priority
        )
    }
}
