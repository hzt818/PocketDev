package com.pocketdev.agent.core

import com.pocketdev.agent.protocol.AgentType

/**
 * Registry for available subagent types.
 *
 * Subagents are lighter-weight than full agents and are
 * designed for specific, narrow tasks within a parent agent's workflow.
 *
 * Examples of subagent types:
 * - FILE_SEARCH: Search for files matching criteria
 * - CODE_GENERATOR: Generate code snippets
 * - TEST_WRITER: Write test cases
 * - DOC_WRITER: Generate documentation
 * - GIT_OPERATOR: Execute git operations
 * - SHELL_EXECUTOR: Run shell commands
 */
interface SubagentRegistry {
    /**
     * Get a subagent definition by type.
     */
    fun getSubagent(type: AgentType): SubagentDefinition?

    /**
     * Get all registered subagent definitions.
     */
    fun getAllSubagents(): List<SubagentDefinition>

    /**
     * Register a new subagent definition.
     */
    fun registerSubagent(definition: SubagentDefinition)

    /**
     * Check if a subagent type is registered.
     */
    fun isRegistered(type: AgentType): Boolean
}

/**
 * Definition of a subagent's capabilities.
 */
data class SubagentDefinition(
    val type: AgentType,
    val name: String,
    val description: String,
    val capabilities: List<String>,
    val estimatedDurationMs: Long = 5000
)

/**
 * Default implementation of SubagentRegistry with pre-registered subagents.
 */
class DefaultSubagentRegistry : SubagentRegistry {
    private val subagents = mutableMapOf<AgentType, SubagentDefinition>()

    init {
        registerDefaultSubagents()
    }

    override fun getSubagent(type: AgentType): SubagentDefinition? = subagents[type]

    override fun getAllSubagents(): List<SubagentDefinition> = subagents.values.toList()

    override fun registerSubagent(definition: SubagentDefinition) {
        subagents[definition.type] = definition
    }

    override fun isRegistered(type: AgentType): Boolean = subagents.containsKey(type)

    private fun registerDefaultSubagents() {
        registerSubagent(
            SubagentDefinition(
                type = AgentType.FILE_SEARCH,
                name = "File Search",
                description = "Searches for files matching specific criteria",
                capabilities = listOf("glob", "regex", "content-search", "recent-files"),
                estimatedDurationMs = 3000
            )
        )

        registerSubagent(
            SubagentDefinition(
                type = AgentType.CODE_GENERATOR,
                name = "Code Generator",
                description = "Generates code snippets from specifications",
                capabilities = listOf("boilerplate", "templates", "scaffolding", "test-fixtures"),
                estimatedDurationMs = 5000
            )
        )

        registerSubagent(
            SubagentDefinition(
                type = AgentType.TEST_WRITER,
                name = "Test Writer",
                description = "Writes test cases for code",
                capabilities = listOf("unit-tests", "integration-tests", "mocking", "assertions"),
                estimatedDurationMs = 8000
            )
        )

        registerSubagent(
            SubagentDefinition(
                type = AgentType.DOC_WRITER,
                name = "Documentation Writer",
                description = "Generates documentation for code",
                capabilities = listOf("kdoc", "markdown", "api-docs", "readme"),
                estimatedDurationMs = 4000
            )
        )

        registerSubagent(
            SubagentDefinition(
                type = AgentType.GIT_OPERATOR,
                name = "Git Operator",
                description = "Executes git operations",
                capabilities = listOf("status", "diff", "log", "branch", "commit"),
                estimatedDurationMs = 2000
            )
        )

        registerSubagent(
            SubagentDefinition(
                type = AgentType.SHELL_EXECUTOR,
                name = "Shell Executor",
                description = "Executes shell commands and scripts",
                capabilities = listOf("run-command", "check-output", "parse-results"),
                estimatedDurationMs = 10000
            )
        )

        registerSubagent(
            SubagentDefinition(
                type = AgentType.SEARCH,
                name = "Search",
                description = "General search functionality",
                capabilities = listOf("web-search", "code-search", "symbol-search"),
                estimatedDurationMs = 5000
            )
        )
    }
}
