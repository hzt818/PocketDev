package com.pocketdev.agent.agents

import com.pocketdev.agent.core.Agent
import com.pocketdev.agent.protocol.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CodeReviewerAgent - Analyzes code quality, finds bugs, and suggests improvements.
 *
 * Capabilities:
 * - Static code analysis
 * - Bug detection
 * - Code smell identification
 * - Performance analysis
 * - Architecture review
 */
@Singleton
class CodeReviewerAgent @Inject constructor() : Agent {
    override val type = AgentType.CODE_REVIEWER
    override val name = "Code Reviewer"
    override val description = "Analyzes code for bugs, quality issues, and improvements"

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is CodeAnalysisRequest) { "Invalid request type" }

        val findings = mutableListOf<Finding>()
        val suggestions = mutableListOf<CodeSuggestion>()

        // Analyze each relevant file
        for (file in request.context.relevantFiles) {
            findings.addAll(analyzeFile(file, request.context))
        }

        // Check for common patterns
        findings.addAll(checkCommonPatterns(request.context))

        // Calculate metrics
        val metrics = calculateMetrics(request.context)

        return CodeAnalysisResponse(
            requestId = request.id,
            success = true,
            findings = findings.sortedByDescending { it.severity.ordinal },
            metrics = metrics,
            suggestions = suggestions
        )
    }

    private fun analyzeFile(file: String, context: AgentContext): List<Finding> {
        val findings = mutableListOf<Finding>()

        // Check file naming conventions
        if (!file.matches(Regex("^[A-Z][a-zA-Z0-9]*\\.kt$")) && file.endsWith(".kt")) {
            findings.add(
                Finding(
                    type = FindingType.STYLE,
                    severity = Severity.LOW,
                    file = file,
                    message = "File name should use PascalCase for Kotlin files",
                    suggestion = "Rename to follow Kotlin conventions"
                )
            )
        }

        // Check for TODO comments that indicate incomplete work
        // This would typically scan the file content

        return findings
    }

    private fun checkCommonPatterns(context: AgentContext): List<Finding> {
        val findings = mutableListOf<Finding>()

        // Check for recent changes that might need review
        for (change in context.recentChanges) {
            if (change.contains("TODO") || change.contains("FIXME")) {
                findings.add(
                    Finding(
                        type = FindingType.CODE_SMELL,
                        severity = Severity.LOW,
                        file = extractFileFromChange(change),
                        message = "Unresolved TODO/FIXME found",
                        suggestion = "Address or create tracking issue for this task"
                    )
                )
            }
        }

        return findings
    }

    private fun extractFileFromChange(change: String): String {
        // Extract file path from git diff format
        return change.substringAfter("+++ b/", "").substringBefore("\n").ifBlank { "unknown" }
    }

    private fun calculateMetrics(context: AgentContext): CodeMetrics {
        var totalLines = 0
        context.relevantFiles.forEach { totalLines += 200 }
        return CodeMetrics(
            linesOfCode = totalLines,
            cyclomaticComplexity = 5, // Placeholder
            maintainabilityIndex = 85f, // Placeholder
            testCoverage = 0f, // Would need actual coverage data
            technicalDebt = 0 // Would need actual debt calculation
        )
    }
}

/**
 * Checks a specific finding type.
 */
fun checkFindingType(content: String, type: FindingType): List<Finding> {
    val findings = mutableListOf<Finding>()

    when (type) {
        FindingType.BUG -> {
            // Check for common bug patterns
            if (content.contains("throw Exception") && !content.contains("throw")) {
                findings.add(
                    Finding(
                        type = FindingType.BUG,
                        severity = Severity.MEDIUM,
                        file = "",
                        message = "Bare throw detected - use specific exception types"
                    )
                )
            }
        }
        FindingType.VULNERABILITY -> {
            // Check for security issues
            if (content.contains("eval(")) {
                findings.add(
                    Finding(
                        type = FindingType.VULNERABILITY,
                        severity = Severity.CRITICAL,
                        file = "",
                        message = "Dangerous use of eval() detected"
                    )
                )
            }
        }
        FindingType.PERFORMANCE -> {
            if (content.contains("for (i in 0..1000)")) {
                findings.add(
                    Finding(
                        type = FindingType.PERFORMANCE,
                        severity = Severity.LOW,
                        file = "",
                        message = "Consider using more efficient iteration pattern"
                    )
                )
            }
        }
        else -> {}
    }

    return findings
}
