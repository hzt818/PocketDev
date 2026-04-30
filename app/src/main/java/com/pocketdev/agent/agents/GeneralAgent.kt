package com.pocketdev.agent.agents

import com.pocketdev.agent.core.Agent
import com.pocketdev.agent.protocol.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GeneralAgent - Routes generic requests to the most appropriate specialized agent.
 */
@Singleton
class GeneralAgent @Inject constructor() : Agent {
    override val type = AgentType.GENERAL
    override val name = "General"
    override val description = "Handles generic requests and routes them to the most appropriate specialized agent"

    override suspend fun execute(request: AgentRequest): AgentResponse {
        val findings = mutableListOf<Finding>()

        // Analyze request context to determine best routing
        val classification = classifyRequest(request)

        findings.add(
            Finding(
                type = FindingType.ARCHITECTURE,
                severity = Severity.INFO,
                file = request.context.projectPath,
                message = "Request classified as: ${classification.type} (confidence: ${classification.confidence}%)",
                suggestion = classification.routing
            )
        )

        // Perform quick preliminary analysis
        findings.addAll(preliminaryAnalysis(request.context))

        return ImplementationResponse(
            requestId = request.id,
            success = true,
            findings = findings
        )
    }

    private fun classifyRequest(request: AgentRequest): RequestClassification {
        val content = request.context.metadata.values.joinToString(" ")
        val files = request.context.relevantFiles

        return when {
            content.contains("build", ignoreCase = true) || content.contains("compile", ignoreCase = true) ->
                RequestClassification("Build Issue", 85, "Route to BuildErrorResolverAgent")
            content.contains("test", ignoreCase = true) || content.contains("tdd", ignoreCase = true) ->
                RequestClassification("Testing Task", 80, "Route to TDDGuideAgent")
            content.contains("security", ignoreCase = true) || content.contains("vulnerability", ignoreCase = true) ->
                RequestClassification("Security Audit", 90, "Route to SecurityReviewerAgent")
            content.contains("refactor", ignoreCase = true) || content.contains("clean", ignoreCase = true) ->
                RequestClassification("Code Quality", 75, "Route to CodeReviewerAgent or RefactorCleanerAgent")
            content.contains("doc", ignoreCase = true) || content.contains("readme", ignoreCase = true) ->
                RequestClassification("Documentation", 80, "Route to DocUpdaterAgent")
            content.contains("search", ignoreCase = true) || content.contains("find", ignoreCase = true) ->
                RequestClassification("Search", 85, "Route to SearchAgent")
            files.isNotEmpty() && files.any { it.endsWith(".kt") } ->
                RequestClassification("Code Analysis", 60, "Route to CodeReviewerAgent")
            else ->
                RequestClassification("General Inquiry", 40, "Consider splitting into specific sub-tasks")
        }
    }

    private fun preliminaryAnalysis(context: AgentContext): List<Finding> {
        val findings = mutableListOf<Finding>()

        // Check project health indicators
        val hasBuildGradle = context.relevantFiles.any { it.endsWith("build.gradle.kts") }
        val hasTests = context.relevantFiles.any { it.contains("/test/") || it.contains("Test.kt") }
        val hasCi = context.relevantFiles.any { it.contains(".github/workflows") }

        if (!hasBuildGradle) {
            findings.add(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.HIGH,
                    file = context.projectPath,
                    message = "No build.gradle.kts found in context",
                    suggestion = "Ensure build system configuration is properly tracked"
                )
            )
        }

        if (!hasTests && context.relevantFiles.isNotEmpty()) {
            findings.add(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.MEDIUM,
                    file = context.projectPath,
                    message = "No test files in context",
                    suggestion = "Create tests for modified functionality"
                )
            )
        }

        if (!hasCi) {
            findings.add(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.LOW,
                    file = context.projectPath,
                    message = "No CI/CD configuration detected",
                    suggestion = "Add GitHub Actions workflow for automated builds and tests"
                )
            )
        }

        return findings
    }

    private data class RequestClassification(
        val type: String,
        val confidence: Int,
        val routing: String
    )
}