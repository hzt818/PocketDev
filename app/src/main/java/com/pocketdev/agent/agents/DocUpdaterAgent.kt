package com.pocketdev.agent.agents

import com.pocketdev.agent.core.Agent
import com.pocketdev.agent.protocol.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DocUpdaterAgent - Updates and generates documentation across the codebase.
 */
@Singleton
class DocUpdaterAgent @Inject constructor() : Agent {
    override val type = AgentType.DOC_UPDATER
    override val name = "Doc Updater"
    override val description = "Updates and generates documentation, READMEs, and code comments"

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is CodeAnalysisRequest) { "Invalid request type" }

        val findings = mutableListOf<Finding>()

        for (file in request.context.relevantFiles) {
            findings.addAll(checkDocumentationQuality(file))
            findings.addAll(checkCommentQuality(file))
        }

        findings.addAll(checkReadmeStatus(request.context))

        return CodeAnalysisResponse(
            requestId = request.id,
            success = true,
            findings = findings.sortedByDescending { it.severity.ordinal }
        )
    }

    private fun checkDocumentationQuality(file: String): List<Finding> {
        val findings = mutableListOf<Finding>()
        val content = readFileSafely(file) ?: return findings

        // Check for public functions without KDoc
        val publicFunctions = Regex("""(?:public\s+)?fun\s+(\w+)\s*\(""").findAll(content)
        val kdocCount = Regex("""/\*\*[\s\S]*?\*/""").findAll(content).count()
        if (publicFunctions.count() > kdocCount * 2 && publicFunctions.count() > 4) {
            findings.add(
                Finding(
                    type = FindingType.STYLE,
                    severity = Severity.LOW,
                    file = file,
                    message = "Many public functions lack KDoc documentation",
                    suggestion = "Add KDoc comments to public API functions"
                )
            )
        }

        // Check for undocumented data classes
        val dataClasses = Regex("""data\s+class\s+(\w+)""").findAll(content)
        dataClasses.forEach { dataClass ->
            val className = dataClass.groupValues[1]
            val hasDoc = !Regex("""/\*\*[\s\S]*?\*/\s*data\s+class\s+$className""").containsMatchIn(content)
            if (hasDoc) {
                findings.add(
                    Finding(
                        type = FindingType.STYLE,
                        severity = Severity.INFO,
                        file = file,
                        message = "Data class '$className' lacks documentation",
                        suggestion = "Add KDoc describing the purpose of $className"
                    )
                )
            }
        }

        return findings
    }

    private fun checkCommentQuality(file: String): List<Finding> {
        val findings = mutableListOf<Finding>()
        val content = readFileSafely(file) ?: return findings

        // Check for commented-out code
        val commentedCode = Regex("""//\s*(?:val|var|fun|class|return|if|when|for)\s+""").findAll(content)
        if (commentedCode.count() > 2) {
            findings.add(
                Finding(
                    type = FindingType.CODE_SMELL,
                    severity = Severity.LOW,
                    file = file,
                    message = "Commented-out code detected - remove or restore",
                    suggestion = "Remove dead commented code or uncomment if still needed"
                )
            )
        }

        return findings
    }

    private fun checkReadmeStatus(context: AgentContext): List<Finding> {
        val findings = mutableListOf<Finding>()
        val readmePath = "${context.projectPath}/README.md"
        val readme = readFileSafely(readmePath)

        if (readme == null) {
            findings.add(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.HIGH,
                    file = "README.md",
                    message = "Project README.md is missing",
                    suggestion = "Create a README with project overview, setup instructions, and architecture"
                )
            )
        } else if (readme.length < 200) {
            findings.add(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.MEDIUM,
                    file = "README.md",
                    message = "README.md is too short (${readme.length} chars)",
                    suggestion = "Expand README with setup instructions, architecture, and contribution guide"
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