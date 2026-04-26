package com.pocketdev.agent.core

import com.pocketdev.agent.protocol.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

/**
 * AgentLoopOperator - Enables autonomous agent loops for complex tasks.
 *
 * Implements the Think-Act-Observe-Decide cycle:
 * 1. THINK - Analyze situation and decide next action
 * 2. ACT - Execute action through appropriate agent
 * 3. OBSERVE - Collect results and feedback
 * 4. DECIDE - Evaluate if goal is met or continue loop
 *
 * This is the core of Claude Code-like autonomous behavior.
 */
class AgentLoopOperator(
    private val executor: AgentExecutor,
    private val config: LoopConfig = LoopConfig()
) {
    private val _state = MutableStateFlow<LoopState>(LoopState.Idle)
    val state: StateFlow<LoopState> = _state.asStateFlow()

    private val _history = MutableSharedFlow<LoopIteration>(extraBufferCapacity = 64)
    val history: Flow<LoopIteration> = _history

    private var loopJob: Job? = null

    /**
     * Starts an autonomous loop for a given goal.
     */
    suspend fun startLoop(goal: String, context: AgentContext): Flow<LoopProgress> = flow {
        val loopId = UUID.randomUUID().toString()

        _state.value = LoopState.Running(goal, 0)

        var currentGoal = goal
        var iteration = 0
        var completed = false

        while (iteration < config.maxIterations && !completed) {
            _state.value = LoopState.Running(goal, iteration)

            // THINK: Analyze and decide next action
            val action = decideAction(currentGoal, context, iteration)

            emit(LoopProgress.Thinking(action.description))

            // ACT: Execute the action
            emit(LoopProgress.Acting(action.description))
            val response = executor.execute(action.request)

            // OBSERVE: Collect results
            emit(LoopProgress.Observing(response))
            _history.emit(LoopIteration(iteration, action, response))

            // DECIDE: Evaluate progress
            val evaluation = evaluateProgress(currentGoal, response, iteration)

            if (evaluation.isComplete) {
                completed = true
                emit(LoopProgress.Completed(evaluation.summary))
            } else {
                currentGoal = evaluation.nextGoal ?: currentGoal
                if (evaluation.shouldRetry) {
                    delay(config.retryDelayMs)
                }
            }

            iteration++
        }

        if (!completed) {
            emit(LoopProgress.MaxIterationsReached(iteration))
        }

        _state.value = LoopState.Completed(iteration)
    }

    /**
     * Stops the current loop gracefully.
     */
    fun stopLoop() {
        loopJob?.cancel()
        _state.value = LoopState.Stopped
    }

    private suspend fun decideAction(goal: String, context: AgentContext, iteration: Int): LoopAction {
        // Early iterations: Understand and plan
        if (iteration == 0) {
            return LoopAction(
                description = "Analyzing goal and creating plan",
                request = ImplementationRequest(
                    id = UUID.randomUUID().toString(),
                    requirements = goal,
                    context = context
                ),
                agentType = AgentType.PLANNER
            )
        }

        // Check for errors that need fixing
        if (iteration == 1 && context.recentChanges.any { it.contains("error", ignoreCase = true) }) {
            return LoopAction(
                description = "Analyzing build errors",
                request = BuildFixRequest(
                    id = UUID.randomUUID().toString(),
                    context = context,
                    errorOutput = context.recentChanges.joinToString("\n")
                ),
                agentType = AgentType.BUILD_ERROR_RESOLVER
            )
        }

        // Security check for new code
        if (iteration == 2 && context.recentChanges.isNotEmpty()) {
            return LoopAction(
                description = "Reviewing security",
                request = SecurityReviewRequest(
                    id = UUID.randomUUID().toString(),
                    context = context
                ),
                agentType = AgentType.SECURITY_REVIEWER
            )
        }

        // Code review
        if (iteration == 3 && context.relevantFiles.isNotEmpty()) {
            return LoopAction(
                description = "Reviewing code quality",
                request = CodeAnalysisRequest(
                    id = UUID.randomUUID().toString(),
                    context = context,
                    scope = AnalysisScope.CHANGED
                ),
                agentType = AgentType.CODE_REVIEWER
            )
        }

        // Final verification
        return LoopAction(
            description = "Final verification",
            request = CodeAnalysisRequest(
                id = UUID.randomUUID().toString(),
                context = context,
                scope = AnalysisScope.FULL
            ),
            agentType = AgentType.CODE_REVIEWER
        )
    }

    private fun evaluateProgress(goal: String, response: AgentResponse, iteration: Int): ProgressEvaluation {
        val hasFindings = response.findings.isNotEmpty()
        val criticalFindings = response.findings.filter { it.severity == Severity.CRITICAL }

        return when {
            criticalFindings.isNotEmpty() -> {
                ProgressEvaluation(
                    isComplete = false,
                    nextGoal = "Fix ${criticalFindings.size} critical issues",
                    shouldRetry = true,
                    summary = "Found ${criticalFindings.size} critical issues to fix"
                )
            }
            hasFindings && iteration < 2 -> {
                ProgressEvaluation(
                    isComplete = false,
                    nextGoal = "Address ${response.findings.size} findings",
                    shouldRetry = true,
                    summary = "Continue improving code quality"
                )
            }
            response.success -> {
                ProgressEvaluation(
                    isComplete = true,
                    summary = "Goal achieved successfully"
                )
            }
            else -> {
                ProgressEvaluation(
                    isComplete = false,
                    nextGoal = goal,
                    shouldRetry = true,
                    summary = "Retry with adjusted approach"
                )
            }
        }
    }
}

/**
 * Configuration for agent loop behavior.
 */
data class LoopConfig(
    val maxIterations: Int = 10,
    val retryDelayMs: Long = 2000,
    val confidenceThreshold: Float = 0.8f,
    val parallelExecution: Boolean = true
)

/**
 * Represents an action to be taken in the loop.
 */
data class LoopAction(
    val description: String,
    val request: AgentRequest,
    val agentType: AgentType
)

/**
 * Progress evaluation result.
 */
data class ProgressEvaluation(
    val isComplete: Boolean,
    val nextGoal: String? = null,
    val shouldRetry: Boolean = false,
    val summary: String
)

/**
 * Loop execution state.
 */
sealed class LoopState {
    data object Idle : LoopState()
    data class Running(val goal: String, val iteration: Int) : LoopState()
    data class Completed(val iterations: Int) : LoopState()
    data object Stopped : LoopState()
}

/**
 * Loop iteration record.
 */
data class LoopIteration(
    val iteration: Int,
    val action: LoopAction,
    val response: AgentResponse
)

/**
 * Progress updates from the loop.
 */
sealed class LoopProgress {
    data class Thinking(val description: String) : LoopProgress()
    data class Acting(val description: String) : LoopProgress()
    data class Observing(val response: AgentResponse) : LoopProgress()
    data class Completed(val summary: String) : LoopProgress()
    data class MaxIterationsReached(val iterations: Int) : LoopProgress()
}

/**
 * Result of a complete agent loop execution.
 */
data class LoopResult(
    val goal: String,
    val iterations: Int,
    val success: Boolean,
    val findings: List<Finding>,
    val actions: List<LoopAction>
)
