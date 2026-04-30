package com.pocketdev.agent.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Agent Protocol - Standard interface for all agents
 *
 * This defines how agents communicate with each other and with the orchestrator.
 * Based on the Anthropic Agent Protocol with extensions for Android.
 */

@Serializable
sealed class AgentRequest {
    abstract val id: String
    abstract val agentType: AgentType
    abstract val context: AgentContext
}

@Serializable
data class CodeAnalysisRequest(
    override val id: String,
    override val agentType: AgentType = AgentType.CODE_REVIEWER,
    override val context: AgentContext,
    val scope: AnalysisScope = AnalysisScope.FULL
) : AgentRequest()

@Serializable
data class ImplementationRequest(
    override val id: String,
    override val agentType: AgentType = AgentType.PLANNER,
    override val context: AgentContext,
    val requirements: String,
    val constraints: List<String> = emptyList()
) : AgentRequest()

@Serializable
data class SecurityReviewRequest(
    override val id: String,
    override val agentType: AgentType = AgentType.SECURITY_REVIEWER,
    override val context: AgentContext
) : AgentRequest()

@Serializable
data class TDDGuideRequest(
    override val id: String,
    override val agentType: AgentType = AgentType.TDD_GUIDE,
    override val context: AgentContext,
    val featureDescription: String
) : AgentRequest()

@Serializable
data class BuildFixRequest(
    override val id: String,
    override val agentType: AgentType = AgentType.BUILD_ERROR_RESOLVER,
    override val context: AgentContext,
    val errorOutput: String
) : AgentRequest()

@Serializable
sealed class AgentResponse {
    abstract val requestId: String
    abstract val success: Boolean
    abstract val findings: List<Finding>
}

@Serializable
data class CodeAnalysisResponse(
    override val requestId: String,
    override val success: Boolean,
    override val findings: List<Finding>,
    val metrics: CodeMetrics? = null,
    val suggestions: List<CodeSuggestion> = emptyList()
) : AgentResponse()

@Serializable
data class ImplementationResponse(
    override val requestId: String,
    override val success: Boolean,
    override val findings: List<Finding>,
    val plan: ImplementationPlan? = null,
    val estimatedComplexity: Complexity = Complexity.MEDIUM
) : AgentResponse()

@Serializable
data class SecurityReviewResponse(
    override val requestId: String,
    override val success: Boolean,
    override val findings: List<Finding>,
    val vulnerabilities: List<Vulnerability> = emptyList(),
    val riskLevel: RiskLevel = RiskLevel.LOW
) : AgentResponse()

@Serializable
data class BuildFixResponse(
    override val requestId: String,
    override val success: Boolean,
    override val findings: List<Finding>,
    val fixes: List<CodeFix> = emptyList(),
    val explanation: String = ""
) : AgentResponse()

// Supporting Types

@Serializable
enum class AgentType {
    @SerialName("orchestrator")
    ORCHESTRATOR,

    @SerialName("planner")
    PLANNER,

    @SerialName("code_reviewer")
    CODE_REVIEWER,

    @SerialName("tdd_guide")
    TDD_GUIDE,

    @SerialName("security_reviewer")
    SECURITY_REVIEWER,

    @SerialName("build_error_resolver")
    BUILD_ERROR_RESOLVER,

    @Deprecated("No implementation available - scheduled for removal in v2.0")
    @SerialName("refactor_cleaner")
    REFACTOR_CLEANER,

    @Deprecated("No implementation available - scheduled for removal in v2.0")
    @SerialName("doc_updater")
    DOC_UPDATER,

    @Deprecated("No implementation available - scheduled for removal in v2.0")
    @SerialName("e2e_runner")
    E2E_RUNNER,

    @Deprecated("No implementation available - scheduled for removal in v2.0")
    @SerialName("search")
    SEARCH,

    @Deprecated("No implementation available - scheduled for removal in v2.0")
    @SerialName("general")
    GENERAL,

    // Subagent types
    @SerialName("file_search")
    FILE_SEARCH,

    @SerialName("code_generator")
    CODE_GENERATOR,

    @SerialName("test_writer")
    TEST_WRITER,

    @SerialName("doc_writer")
    DOC_WRITER,

    @SerialName("git_operator")
    GIT_OPERATOR,

    @SerialName("shell_executor")
    SHELL_EXECUTOR
}

@Serializable
data class AgentContext(
    val projectPath: String = "",
    val language: String = "kotlin",
    val framework: String = "android",
    val relevantFiles: List<String> = emptyList(),
    val recentChanges: List<String> = emptyList(),
    val taskHistory: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
    val parentTaskId: String? = null
)

@Serializable
data class Finding(
    val type: FindingType,
    val severity: Severity,
    val file: String,
    val line: Int? = null,
    val message: String,
    val rule: String? = null,
    val suggestion: String? = null
)

@Serializable
enum class FindingType {
    @SerialName("bug")
    BUG,

    @SerialName("vulnerability")
    VULNERABILITY,

    @SerialName("code_smell")
    CODE_SMELL,

    @SerialName("performance")
    PERFORMANCE,

    @SerialName("style")
    STYLE,

    @SerialName("security")
    SECURITY,

    @SerialName("architecture")
    ARCHITECTURE
}

@Serializable
enum class Severity {
    @SerialName("critical")
    CRITICAL,

    @SerialName("high")
    HIGH,

    @SerialName("medium")
    MEDIUM,

    @SerialName("low")
    LOW,

    @SerialName("info")
    INFO
}

@Serializable
enum class Complexity {
    @SerialName("trivial")
    TRIVIAL,

    @SerialName("low")
    LOW,

    @SerialName("medium")
    MEDIUM,

    @SerialName("high")
    HIGH,

    @SerialName("critical")
    CRITICAL
}

@Serializable
enum class RiskLevel {
    @SerialName("critical")
    CRITICAL,

    @SerialName("high")
    HIGH,

    @SerialName("medium")
    MEDIUM,

    @SerialName("low")
    LOW,

    @SerialName("negligible")
    NEGLIGIBLE
}

@Serializable
enum class AnalysisScope {
    @SerialName("full")
    FULL,

    @SerialName("changed")
    CHANGED,

    @SerialName("diff")
    DIFF
}

@Serializable
data class CodeMetrics(
    val linesOfCode: Int = 0,
    val cyclomaticComplexity: Int = 0,
    val maintainabilityIndex: Float = 0f,
    val testCoverage: Float = 0f,
    val technicalDebt: Int = 0
)

@Serializable
data class CodeSuggestion(
    val file: String,
    val original: String,
    val suggested: String,
    val rationale: String
)

@Serializable
data class ImplementationPlan(
    val phases: List<ImplementationPhase>,
    val estimatedTime: String = "",
    val risks: List<String> = emptyList()
)

@Serializable
data class ImplementationPhase(
    val name: String,
    val tasks: List<ImplementationTask>,
    val dependencies: List<String> = emptyList()
)

@Serializable
data class ImplementationTask(
    val id: String,
    val description: String,
    val files: List<String> = emptyList(),
    val estimatedComplexity: Complexity = Complexity.MEDIUM
)

@Serializable
data class Vulnerability(
    val type: String,
    val severity: Severity,
    val location: String,
    val description: String,
    val remediation: String
)

@Serializable
data class CodeFix(
    val file: String,
    val line: Int? = null,
    val original: String,
    val fixed: String,
    val explanation: String
)
