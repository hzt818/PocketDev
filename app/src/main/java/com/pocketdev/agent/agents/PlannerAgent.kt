package com.pocketdev.agent.agents

import com.pocketdev.agent.core.Agent
import com.pocketdev.agent.protocol.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PlannerAgent - Creates implementation plans for features.
 *
 * Analyzes requirements and generates:
 * - Phased implementation plan
 * - Task breakdown
 * - Dependency graph
 * - Risk assessment
 * - Effort estimation
 */
@Singleton
class PlannerAgent @Inject constructor() : Agent {
    override val type = AgentType.PLANNER
    override val name = "Planner"
    override val description = "Creates detailed implementation plans for features"

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is ImplementationRequest) { "Invalid request type" }

        val plan = generateImplementationPlan(request, request.context)

        return ImplementationResponse(
            requestId = request.id,
            success = true,
            findings = listOf(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.INFO,
                    file = "",
                    message = "Implementation plan generated for: ${request.requirements}"
                )
            ),
            plan = plan,
            estimatedComplexity = assessComplexity(request)
        )
    }

    private fun generateImplementationPlan(request: ImplementationRequest, context: AgentContext): ImplementationPlan {
        val requirements = request.requirements.lowercase()

        val phases = mutableListOf<ImplementationPhase>()

        // Phase 1: Research and Analysis
        phases.add(
            ImplementationPhase(
                name = "Phase 1: Research & Analysis",
                tasks = listOf(
                    ImplementationTask(
                        id = "plan-1-1",
                        description = "Analyze requirements and constraints",
                        estimatedComplexity = Complexity.LOW
                    ),
                    ImplementationTask(
                        id = "plan-1-2",
                        description = "Check existing codebase for similar implementations",
                        estimatedComplexity = Complexity.MEDIUM
                    ),
                    ImplementationTask(
                        id = "plan-1-3",
                        description = "Research library/API options",
                        estimatedComplexity = Complexity.MEDIUM
                    )
                )
            )
        )

        // Phase 2: Architecture Design
        phases.add(
            ImplementationPhase(
                name = "Phase 2: Architecture & Design",
                tasks = listOf(
                    ImplementationTask(
                        id = "plan-2-1",
                        description = "Define interfaces and data models",
                        estimatedComplexity = Complexity.HIGH
                    ),
                    ImplementationTask(
                        id = "plan-2-2",
                        description = "Design component interactions",
                        estimatedComplexity = Complexity.MEDIUM
                    ),
                    ImplementationTask(
                        id = "plan-2-3",
                        description = "Create ADR if needed",
                        estimatedComplexity = Complexity.LOW
                    )
                ),
                dependencies = listOf("plan-1-1", "plan-1-2", "plan-1-3")
            )
        )

        // Phase 3: Implementation
        phases.add(
            ImplementationPhase(
                name = "Phase 3: Implementation",
                tasks = generateImplementationTasks(requirements),
                dependencies = listOf("plan-2-1", "plan-2-2", "plan-2-3")
            )
        )

        // Phase 4: Testing
        phases.add(
            ImplementationPhase(
                name = "Phase 4: Testing",
                tasks = listOf(
                    ImplementationTask(
                        id = "plan-4-1",
                        description = "Write unit tests",
                        estimatedComplexity = Complexity.MEDIUM
                    ),
                    ImplementationTask(
                        id = "plan-4-2",
                        description = "Write integration tests",
                        estimatedComplexity = Complexity.MEDIUM
                    ),
                    ImplementationTask(
                        id = "plan-4-3",
                        description = "Run full test suite",
                        estimatedComplexity = Complexity.LOW
                    )
                ),
                dependencies = listOf("plan-3-1")
            )
        )

        // Phase 5: Code Review
        phases.add(
            ImplementationPhase(
                name = "Phase 5: Review & Refine",
                tasks = listOf(
                    ImplementationTask(
                        id = "plan-5-1",
                        description = "Self code review",
                        estimatedComplexity = Complexity.LOW
                    ),
                    ImplementationTask(
                        id = "plan-5-2",
                        description = "Address review feedback",
                        estimatedComplexity = Complexity.MEDIUM
                    )
                ),
                dependencies = listOf("plan-4-1", "plan-4-2", "plan-4-3")
            )
        )

        return ImplementationPlan(
            phases = phases,
            estimatedTime = estimateTime(requirements),
            risks = assessRisks(requirements, request.constraints)
        )
    }

    private fun generateImplementationTasks(requirements: String): List<ImplementationTask> {
        val tasks = mutableListOf<ImplementationTask>()

        // Domain Layer
        if (requirements.contains("model") || requirements.contains("entity")) {
            tasks.add(
                ImplementationTask(
                    id = "plan-3-1",
                    description = "Implement domain models",
                    estimatedComplexity = Complexity.MEDIUM
                )
            )
        }

        // Data Layer
        if (requirements.contains("repository") || requirements.contains("database")) {
            tasks.add(
                ImplementationTask(
                    id = "plan-3-2",
                    description = "Implement data layer (Repository, API, Local storage)",
                    estimatedComplexity = Complexity.HIGH
                )
            )
        }

        // Use Cases
        if (requirements.contains("usecase") || requirements.contains("business logic")) {
            tasks.add(
                ImplementationTask(
                    id = "plan-3-3",
                    description = "Implement use cases",
                    estimatedComplexity = Complexity.MEDIUM
                )
            )
        }

        // UI Layer
        if (requirements.contains("screen") || requirements.contains("ui") || requirements.contains("view")) {
            tasks.add(
                ImplementationTask(
                    id = "plan-3-4",
                    description = "Implement UI (Compose screens, ViewModels)",
                    estimatedComplexity = Complexity.HIGH
                )
            )
        }

        // Navigation
        if (requirements.contains("navigation") || requirements.contains("routing")) {
            tasks.add(
                ImplementationTask(
                    id = "plan-3-5",
                    description = "Implement navigation",
                    estimatedComplexity = Complexity.LOW
                )
            )
        }

        // DI
        if (requirements.contains("dependency") || requirements.contains("injection") || requirements.contains("hilt")) {
            tasks.add(
                ImplementationTask(
                    id = "plan-3-6",
                    description = "Configure dependency injection",
                    estimatedComplexity = Complexity.LOW
                )
            )
        }

        return tasks.ifEmpty { listOf(
            ImplementationTask(
                id = "plan-3-1",
                description = "Implement feature",
                estimatedComplexity = Complexity.MEDIUM
            )
        ) }
    }

    private fun assessComplexity(request: ImplementationRequest): Complexity {
        val req = request.requirements
        var score = 0

        // New files
        if (req.contains("new file") || req.contains("create")) score += 1

        // Complex patterns
        if (req.contains("async") || req.contains("concurrent")) score += 2
        if (req.contains("database") || req.contains("repository")) score += 2
        if (req.contains("api") || req.contains("network")) score += 2
        if (req.contains("test")) score += 1

        // Multiple constraints
        score += request.constraints.size

        return when {
            score >= 8 -> Complexity.CRITICAL
            score >= 5 -> Complexity.HIGH
            score >= 3 -> Complexity.MEDIUM
            else -> Complexity.LOW
        }
    }

    private fun estimateTime(requirements: String): String {
        val complexity = assessComplexity(
            ImplementationRequest(
                id = "",
                requirements = requirements,
                context = AgentContext()
            )
        )

        return when (complexity) {
            Complexity.TRIVIAL -> "30 minutes - 1 hour"
            Complexity.LOW -> "1 - 2 hours"
            Complexity.MEDIUM -> "2 - 4 hours"
            Complexity.HIGH -> "4 - 8 hours"
            Complexity.CRITICAL -> "8+ hours (consider breaking into smaller tasks)"
        }
    }

    private fun assessRisks(requirements: String, constraints: List<String>): List<String> {
        val risks = mutableListOf<String>()

        if (requirements.contains("migration") || requirements.contains("upgrade")) {
            risks.add("Migration may have breaking changes")
        }

        if (requirements.contains("api") || requirements.contains("network")) {
            risks.add("Network failures need handling")
        }

        if (requirements.contains("database") || requirements.contains("storage")) {
            risks.add("Data migration and backup strategies needed")
        }

        if (constraints.any { it.contains("performance") }) {
            risks.add("Performance requirements may require optimization")
        }

        if (constraints.any { it.contains("security") }) {
            risks.add("Security requirements need thorough review")
        }

        return risks.ifEmpty { listOf("No significant risks identified") }
    }
}

/**
 * Template for common feature implementations.
 */
object FeatureTemplates {
    val cleanArchitecture = """
## Clean Architecture Layers

### Domain Layer (innermost - no dependencies)
- Entities/Models
- Repository interfaces
- Use case interfaces

### Data Layer
- Repository implementations
- API services
- Local storage

### Presentation Layer
- ViewModels
- Compose UI
- Navigation
    """.trimIndent()

    val mvvmPattern = """
## MVVM Pattern

### Model
- Data classes
- Repository interfaces

### View
- Compose UI
- Observes ViewModel state

### ViewModel
- Exposes StateFlow
- Handles user actions
- Calls use cases
    """.trimIndent()

    fun getTemplate(type: String): String = when (type.lowercase()) {
        "clean" -> cleanArchitecture
        "mvvm" -> mvvmPattern
        else -> ""
    }
}
