package com.pocketdev.agent.core

import com.pocketdev.agent.protocol.*

/**
 * Base interface for all agents in the PocketDev agent system.
 *
 * Each agent:
 * - Receives a request with context
 * - Performs specialized analysis or action
 * - Returns structured findings and recommendations
 */
interface Agent {
    val type: AgentType
    val name: String
    val description: String

    suspend fun execute(request: AgentRequest): AgentResponse

    fun canHandle(request: AgentRequest): Boolean = request.agentType == type
}

/**
 * Agent that orchestrates multiple agents for complex tasks.
 */
interface OrchestratorAgent : Agent {
    suspend fun planExecution(request: AgentRequest): ExecutionPlan
    suspend fun coordinate(agents: List<Agent>, plan: ExecutionPlan): List<AgentResponse>
}

/**
 * Represents an execution plan for orchestrating multiple agents.
 */
data class ExecutionPlan(
    val phases: List<ExecutionPhase>,
    val estimatedTime: Long,
    val parallelExecution: Boolean = true
)

data class ExecutionPhase(
    val name: String,
    val agents: List<AgentType>,
    val waitFor: List<String> = emptyList()
)

/**
 * Agent execution result with timing and metadata.
 */
data class AgentExecution(
    val agentType: AgentType,
    val requestId: String,
    val startTime: Long,
    val endTime: Long,
    val success: Boolean,
    val response: AgentResponse?,
    val error: String? = null
) {
    val durationMs: Long get() = endTime - startTime
}

/**
 * Agent registry for looking up available agents.
 */
interface AgentRegistry {
    fun getAgent(type: AgentType): Agent?
    fun getAllAgents(): List<Agent>
    fun registerAgent(agent: Agent)
}

/**
 * Default implementation of AgentRegistry.
 */
class DefaultAgentRegistry : AgentRegistry {
    private val agents = mutableMapOf<AgentType, Agent>()

    override fun getAgent(type: AgentType): Agent? = agents[type]

    override fun getAllAgents(): List<Agent> = agents.values.toList()

    override fun registerAgent(agent: Agent) {
        agents[agent.type] = agent
    }
}
