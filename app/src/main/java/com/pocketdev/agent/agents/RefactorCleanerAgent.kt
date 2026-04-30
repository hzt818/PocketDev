package com.pocketdev.agent.agents

import com.pocketdev.agent.core.Agent
import com.pocketdev.agent.protocol.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RefactorCleanerAgent - Identifies dead code, duplicates, and refactoring opportunities.
 */
@Singleton
class RefactorCleanerAgent @Inject constructor() : Agent {
    override val type = AgentType.REFACTOR_CLEANER
    override val name = "Refactor Cleaner"
    override val description = "Scans for dead code, duplicates, and suggests refactoring improvements"

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is CodeAnalysisRequest) { "Invalid request type" }

        val findings = mutableListOf<Finding>()

        for (file in request.context.relevantFiles) {
            findings.addAll(analyzeDeadCode(file))
            findings.addAll(analyzeCodeDuplication(file))
            findings.addAll(analyzeImports(file))
        }

        return CodeAnalysisResponse(
            requestId = request.id,
            success = true,
            findings = findings.sortedByDescending { it.severity.ordinal }
        )
    }

    private fun analyzeDeadCode(file: String): List<Finding> {
        val findings = mutableListOf<Finding>()
        val content = readFileSafely(file) ?: return findings

        // Check for unused properties (TODO/FIXME markers)
        val todoCount = Regex("""(TODO|FIXME):\s*(remove|delete|refactor)""", RegexOption.IGNORE_CASE)
            .findAll(content).count()
        if (todoCount > 0) {
            findings.add(
                Finding(
                    type = FindingType.CODE_SMELL,
                    severity = Severity.MEDIUM,
                    file = file,
                    message = "$todoCount deferred removal/refactor markers found",
                    suggestion = "Clean up items marked for removal"
                )
            )
        }

        // Check for @Deprecated usage
        val deprecatedCount = content.lines().count { it.trim().startsWith("@Deprecated(") }
        if (deprecatedCount > 5) {
            findings.add(
                Finding(
                    type = FindingType.CODE_SMELL,
                    severity = Severity.MEDIUM,
                    file = file,
                    message = "$deprecatedCount deprecated annotations found",
                    suggestion = "Review and remove deprecated code or plan migration"
                )
            )
        }

        // Check for empty classes/functions
        Regex("""class\s+\w+\s*\{[\s\n]*\}""").findAll(content).forEach { match ->
            findings.add(
                Finding(
                    type = FindingType.CODE_SMELL,
                    severity = Severity.INFO,
                    file = file,
                    message = "Empty class found: ${match.value.take(40)}",
                    suggestion = "Remove empty class or add implementation"
                )
            )
        }

        // Check large files
        val lineCount = content.lines().size
        if (lineCount > 800) {
            findings.add(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.HIGH,
                    file = file,
                    message = "File exceeds 800 lines ($lineCount lines) - consider splitting",
                    suggestion = "Extract related functionality into separate files"
                )
            )
        }

        return findings
    }

    private fun analyzeCodeDuplication(file: String): List<Finding> {
        val findings = mutableListOf<Finding>()
        val content = readFileSafely(file) ?: return findings

        // Simple duplicate detection: repeated code blocks of 5+ lines
        val lines = content.lines()
        val blockMap = mutableMapOf<String, Int>()
        for (i in 0 until lines.size - 5) {
            val block = lines.subList(i, (i + 5).coerceAtMost(lines.size)).joinToString("\n")
            blockMap[block] = (blockMap[block] ?: 0) + 1
        }

        blockMap.entries.filter { it.value > 1 }.take(3).forEach { (block, count) ->
            findings.add(
                Finding(
                    type = FindingType.CODE_SMELL,
                    severity = Severity.LOW,
                    file = file,
                    message = "Duplicate code block found ($count occurrences)",
                    suggestion = "Extract duplicated block into a shared function"
                )
            )
        }

        return findings
    }

    private fun analyzeImports(file: String): List<Finding> {
        val findings = mutableListOf<Finding>()
        val content = readFileSafely(file) ?: return findings

        // Check for wildcard imports
        val wildcardCount = content.lines().count { it.trim().matches(Regex("""^import\s+[\w.]+\*""")) }
        if (wildcardCount > 3) {
            findings.add(
                Finding(
                    type = FindingType.STYLE,
                    severity = Severity.LOW,
                    file = file,
                    message = "$wildcardCount wildcard imports found",
                    suggestion = "Replace wildcard imports with explicit imports for clarity"
                )
            )
        }

        return findings
    }

    private fun readFileSafely(file: String): String? {
        return try {
            val f = java.io.File(file)
            if (f.exists() && f.isFile) f.readText() else null
        } catch (e: Exception) { null }
    }
}