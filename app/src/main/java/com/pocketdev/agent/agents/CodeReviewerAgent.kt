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
        val content = try {
            val f = java.io.File(file)
            if (f.exists() && f.isFile) f.readText() else null
        } catch (e: Exception) { null }

        if (content == null) return findings

        // Check file naming conventions
        if (file.endsWith(".kt") && !file.substringAfterLast("/").matches(Regex("^[A-Z][a-zA-Z0-9]*\\.kt$"))) {
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

        // Check for TODO/FIXME comments in file content
        Regex("""(TODO|FIXME|HACK|XXX)[:\s].*""").findAll(content).forEach { match ->
            findings.add(
                Finding(
                    type = FindingType.CODE_SMELL,
                    severity = Severity.LOW,
                    file = file,
                    message = "Unresolved ${match.groupValues[1]} found: ${match.value.trim()}",
                    suggestion = "Address or create tracking issue for this task"
                )
            )
        }

        // Check for empty catch blocks
        Regex("""catch\s*\([^)]*\)\s*\{[\s;]*\}""").findAll(content).forEach {
            findings.add(
                Finding(
                    type = FindingType.BUG,
                    severity = Severity.MEDIUM,
                    file = file,
                    message = "Empty catch block detected",
                    suggestion = "Add error handling or logging"
                )
            )
        }

        // Check for hardcoded configuration values
        if (content.contains("http://") || content.contains("https://")) {
            Regex("""["']https?://[^"']+["']""").findAll(content).forEach { match ->
                if (!match.value.contains("\${")) {
                    findings.add(
                        Finding(
                            type = FindingType.CODE_SMELL,
                            severity = Severity.INFO,
                            file = file,
                            message = "Hardcoded URL detected: ${match.value.take(60)}",
                            suggestion = "Extract to a constant or configuration"
                        )
                    )
                }
            }
        }

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
        var totalComplexity = 0
        var fileCount = 0

        for (filePath in context.relevantFiles) {
            val content = try {
                val f = java.io.File(filePath)
                if (f.exists() && f.isFile) f.readText() else null
            } catch (e: Exception) { null } ?: continue

            val lines = content.lines()
            totalLines += lines.size
            fileCount++

            // Calculate cyclomatic complexity: count decision points
            val decisionPatterns = listOf(
                Regex("""\bif\s*\("""), Regex("""\bwhen\s*\{"""),
                Regex("""\bfor\s*\("""), Regex("""\bwhile\s*\("""),
                Regex("""\bcatch\s*\("""), Regex("""\bcase\s+"""),
                Regex("""&&"""), Regex("""\|\|""")
            )
            totalComplexity += decisionPatterns.sumOf { pattern ->
                pattern.findAll(content).count()
            }
        }

        // Maintainability index (simplified): higher for small well-structured files
        val avgLinesPerFile = if (fileCount > 0) totalLines / fileCount else 0
        val maintainabilityIndex = when {
            avgLinesPerFile == 0 -> 100f
            avgLinesPerFile < 100 -> 85f
            avgLinesPerFile < 300 -> 70f
            avgLinesPerFile < 500 -> 55f
            else -> 40f
        }

        // Technical debt estimate: files > 300 lines need refactoring
        val technicalDebt = (totalLines / 300) + (totalComplexity / 10)

        return CodeMetrics(
            linesOfCode = totalLines,
            cyclomaticComplexity = totalComplexity.coerceAtLeast(1),
            maintainabilityIndex = maintainabilityIndex,
            testCoverage = 0f, // Requires external test coverage tool
            technicalDebt = technicalDebt
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
