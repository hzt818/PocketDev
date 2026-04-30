package com.pocketdev.agent.agents

import com.pocketdev.agent.core.Agent
import com.pocketdev.agent.protocol.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SearchAgent - Searches the codebase for symbols, files, patterns, and references.
 */
@Singleton
class SearchAgent @Inject constructor() : Agent {
    override val type = AgentType.SEARCH
    override val name = "Search"
    override val description = "Searches codebase for symbols, files, patterns, and code references"

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is CodeAnalysisRequest) { "Invalid request type" }

        val findings = mutableListOf<Finding>()

        // Extract search terms from context metadata
        val searchTerms = request.context.metadata["searchTerms"]?.split(",") ?: emptyList()
        val searchType = request.context.metadata["searchType"] ?: "symbol"

        when (searchType) {
            "symbol" -> findings.addAll(searchSymbols(request.context, searchTerms))
            "file" -> findings.addAll(searchFiles(request.context, searchTerms))
            "pattern" -> findings.addAll(searchPatterns(request.context, searchTerms))
            "reference" -> findings.addAll(findReferences(request.context, searchTerms))
        }

        // Always do a general search across relevant files
        findings.addAll(generalSearch(request.context))

        return CodeAnalysisResponse(
            requestId = request.id,
            success = true,
            findings = findings.sortedByDescending { it.severity.ordinal }
        )
    }

    private fun searchSymbols(context: AgentContext, terms: List<String>): List<Finding> {
        val findings = mutableListOf<Finding>()

        for (file in context.relevantFiles) {
            val content = readFileSafely(file) ?: continue
            for (term in terms) {
                // Search for class, function, interface declarations
                val matches = Regex("""(class|interface|object|fun|val|var)\s+$term\b""")
                    .findAll(content)
                matches.forEach { match ->
                    findings.add(
                        Finding(
                            type = FindingType.STYLE,
                            severity = Severity.INFO,
                            file = file,
                            message = "Symbol '$term' found: ${match.value.trim()}",
                            suggestion = "Referenced symbol at ${file}:${match.range.first}"
                        )
                    )
                }
            }
        }

        return findings
    }

    private fun searchFiles(context: AgentContext, terms: List<String>): List<Finding> {
        val findings = mutableListOf<Finding>()

        for (file in context.relevantFiles) {
            val fileName = file.substringAfterLast("/")
            for (term in terms) {
                if (fileName.contains(term, ignoreCase = true)) {
                    findings.add(
                        Finding(
                            type = FindingType.STYLE,
                            severity = Severity.INFO,
                            file = file,
                            message = "File matching '$term': $fileName",
                            suggestion = "File path: $file"
                        )
                    )
                }
            }
        }

        return findings
    }

    private fun searchPatterns(context: AgentContext, terms: List<String>): List<Finding> {
        val findings = mutableListOf<Finding>()

        for (file in context.relevantFiles) {
            val content = readFileSafely(file) ?: continue
            for (pattern in terms) {
                val regex = try {
                    Regex(pattern)
                } catch (e: Exception) {
                    Regex(Regex.escape(pattern))
                }
                val matchCount = regex.findAll(content).count()
                if (matchCount > 0) {
                    findings.add(
                        Finding(
                            type = FindingType.STYLE,
                            severity = Severity.INFO,
                            file = file,
                            message = "Pattern '$pattern' matched $matchCount time(s) in file",
                            suggestion = "Review occurrences in $file"
                        )
                    )
                }
            }
        }

        return findings
    }

    private fun findReferences(context: AgentContext, terms: List<String>): List<Finding> {
        val findings = mutableListOf<Finding>()

        for (file in context.relevantFiles) {
            val content = readFileSafely(file) ?: continue
            for (term in terms) {
                val refCount = Regex("""\b${Regex.escape(term)}\b""").findAll(content).count()
                if (refCount > 0) {
                    findings.add(
                        Finding(
                            type = FindingType.STYLE,
                            severity = Severity.INFO,
                            file = file,
                            message = "'$term' referenced $refCount time(s)",
                            suggestion = "Used in $file"
                        )
                    )
                }
            }
        }

        return findings
    }

    private fun generalSearch(context: AgentContext): List<Finding> {
        val findings = mutableListOf<Finding>()

        for (file in context.relevantFiles) {
            val content = readFileSafely(file) ?: continue

            // Count key symbols
            val classCount = Regex("""\bclass\s+""").findAll(content).count()
            val funCount = Regex("""\bfun\s+""").findAll(content).count()
            val valCount = Regex("""\bval\s+""").findAll(content).count()

            if (classCount + funCount + valCount > 0) {
                findings.add(
                    Finding(
                        type = FindingType.STYLE,
                        severity = Severity.INFO,
                        file = file,
                        message = "File summary: $classCount classes, $funCount functions, $valCount properties",
                        suggestion = "File contains significant code - consider review"
                    )
                )
            }
        }

        return findings.take(20)
    }

    private fun readFileSafely(file: String): String? {
        return try {
            val f = java.io.File(file)
            if (f.exists() && f.isFile) f.readText() else null
        } catch (e: Exception) { null }
    }
}