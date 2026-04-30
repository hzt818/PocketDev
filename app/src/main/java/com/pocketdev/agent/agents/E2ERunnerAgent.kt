package com.pocketdev.agent.agents

import com.pocketdev.agent.core.Agent
import com.pocketdev.agent.protocol.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * E2ERunnerAgent - Manages end-to-end testing execution and reporting.
 */
@Singleton
class E2ERunnerAgent @Inject constructor() : Agent {
    override val type = AgentType.E2E_RUNNER
    override val name = "E2E Runner"
    override val description = "Executes and manages end-to-end tests, tracks test journeys"

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is ImplementationRequest) { "Invalid request type" }

        val findings = mutableListOf<Finding>()

        findings.addAll(analyzeTestReadiness(request))
        findings.addAll(generateTestPlan(request))
        findings.addAll(checkTestEnvironment(request.context))

        return ImplementationResponse(
            requestId = request.id,
            success = true,
            findings = findings.sortedByDescending { it.severity.ordinal }
        )
    }

    private fun analyzeTestReadiness(request: ImplementationRequest): List<Finding> {
        val findings = mutableListOf<Finding>()

        // Check for test directories
        val hasAndroidTests = request.context.relevantFiles.any {
            it.contains("androidTest") || it.contains("test")
        }
        if (!hasAndroidTests) {
            findings.add(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.HIGH,
                    file = request.context.projectPath,
                    message = "No test directories found in the project",
                    suggestion = "Set up test directories: src/test/ and src/androidTest/"
                )
            )
        }

        // Check if any test files were provided
        val testFiles = request.context.relevantFiles.filter {
            it.endsWith("Test.kt") || it.endsWith("Tests.kt") || it.contains("/test/")
        }
        if (testFiles.isEmpty()) {
            findings.add(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.MEDIUM,
                    file = request.context.projectPath,
                    message = "No existing test files detected",
                    suggestion = "Create test files for critical user flows"
                )
            )
        }

        return findings
    }

    private fun generateTestPlan(request: ImplementationRequest): List<Finding> {
        val findings = mutableListOf<Finding>()

        // Identify screens that likely need E2E tests
        val screenFiles = request.context.relevantFiles.filter { it.endsWith("Screen.kt") }
        screenFiles.forEach { screenFile ->
            val screenName = screenFile.substringAfterLast("/").removeSuffix(".kt")
            val className = screenName.replace("Screen", "Test")
            val testPath = screenFile.replace("Screen.kt", "Test.kt")
                .replace("/screens/", "/test/")

            findings.add(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.INFO,
                    file = screenFile,
                    message = "E2E test recommended for $screenName",
                    suggestion = "Create $className at $testPath covering:\n" +
                            "- Navigation to screen\n" +
                            "- Core user interactions\n" +
                            "- Error state handling\n" +
                            "- Loading state handling"
                )
            )
        }

        return findings
    }

    private fun checkTestEnvironment(context: AgentContext): List<Finding> {
        val findings = mutableListOf<Finding>()

        val hasEspresso = context.relevantFiles.any { it.contains("espresso") }
        val hasComposeTestRule = context.relevantFiles.any {
            readFileSafely(it)?.contains("ComposeTestRule") == true
        }

        if (!hasEspresso && !hasComposeTestRule) {
            findings.add(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.MEDIUM,
                    file = "build.gradle.kts",
                    message = "No E2E test framework detected",
                    suggestion = "Add Compose UI testing dependencies to build.gradle.kts:\n" +
                            "androidTestImplementation(\"androidx.compose.ui:ui-test-junit4\")\n" +
                            "androidTestImplementation(\"androidx.compose.ui:ui-test-manifest\")"
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