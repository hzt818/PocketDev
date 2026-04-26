package com.pocketdev.agent.agents

import com.pocketdev.agent.core.Agent
import com.pocketdev.agent.protocol.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TDDGuideAgent - Guides test-driven development workflow.
 *
 * Implements the Red-Green-Refactor cycle:
 * 1. RED - Write a failing test
 * 2. GREEN - Write minimal code to pass
 * 3. REFACTOR - Improve code while keeping tests green
 */
@Singleton
class TDDGuideAgent @Inject constructor() : Agent {
    override val type = AgentType.TDD_GUIDE
    override val name = "TDD Guide"
    override val description = "Guides test-driven development with Red-Green-Refactor cycle"

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is TDDGuideRequest) { "Invalid request type" }

        val plan = generateTDDPlan(request.featureDescription, request.context)

        return ImplementationResponse(
            requestId = request.id,
            success = true,
            findings = listOf(
                Finding(
                    type = FindingType.ARCHITECTURE,
                    severity = Severity.INFO,
                    file = "",
                    message = "TDD workflow generated for: ${request.featureDescription}"
                )
            ),
            plan = plan,
            estimatedComplexity = Complexity.MEDIUM
        )
    }

    private fun generateTDDPlan(feature: String, context: AgentContext): ImplementationPlan {
        val phases = listOf(
            ImplementationPhase(
                name = "Phase 1: RED - Write Failing Test",
                tasks = listOf(
                    ImplementationTask(
                        id = "tdd-1-1",
                        description = "Write the test case first - it should fail",
                        files = listOf("**/test/**/${feature}Test.kt"),
                        estimatedComplexity = Complexity.LOW
                    ),
                    ImplementationTask(
                        id = "tdd-1-2",
                        description = "Verify test fails with expected error",
                        files = emptyList(),
                        estimatedComplexity = Complexity.TRIVIAL
                    )
                )
            ),
            ImplementationPhase(
                name = "Phase 2: GREEN - Make it Pass",
                tasks = listOf(
                    ImplementationTask(
                        id = "tdd-2-1",
                        description = "Write minimal production code to pass the test",
                        files = listOf("**/main/**/${feature}.kt"),
                        estimatedComplexity = Complexity.MEDIUM
                    ),
                    ImplementationTask(
                        id = "tdd-2-2",
                        description = "Run all tests to verify green",
                        files = emptyList(),
                        estimatedComplexity = Complexity.TRIVIAL
                    )
                ),
                dependencies = listOf("tdd-1-1", "tdd-1-2")
            ),
            ImplementationPhase(
                name = "Phase 3: REFACTOR",
                tasks = listOf(
                    ImplementationTask(
                        id = "tdd-3-1",
                        description = "Improve code structure and readability",
                        files = context.relevantFiles,
                        estimatedComplexity = Complexity.LOW
                    ),
                    ImplementationTask(
                        id = "tdd-3-2",
                        description = "Run full test suite",
                        files = emptyList(),
                        estimatedComplexity = Complexity.TRIVIAL
                    ),
                    ImplementationTask(
                        id = "tdd-3-3",
                        description = "Verify all tests pass after refactoring",
                        files = emptyList(),
                        estimatedComplexity = Complexity.TRIVIAL
                    )
                ),
                dependencies = listOf("tdd-2-1", "tdd-2-2")
            )
        )

        return ImplementationPlan(
            phases = phases,
            estimatedTime = "2-4 hours",
            risks = listOf(
                "Test coverage might be incomplete",
                "Integration tests needed for full coverage"
            )
        )
    }

    companion object {
        /**
         * Generates test template for a given class/function.
         */
        fun generateTestTemplate(
            className: String,
            packageName: String,
            testFramework: TestFramework = TestFramework.JUNIT
        ): String {
            return when (testFramework) {
                TestFramework.JUNIT -> generateJUnitTemplate(className, packageName)
                TestFramework.KOTEST -> generateKotestTemplate(className, packageName)
                TestFramework.MOCKK -> generateMockkTemplate(className, packageName)
            }
        }

        private fun generateJUnitTemplate(className: String, packageName: String): String {
            return """
package $packageName

import org.junit.Test
import org.junit.Assert.*

class ${className}Test {

    @Test
    fun `should return expected result when valid input`() {
        // Arrange
        val sut = ${className}()

        // Act
        val result = sut.execute()

        // Assert
        assertEquals("expected", result)
    }

    @Test
    fun `should throw exception when invalid input`() {
        // Arrange
        val sut = ${className}()

        // Act & Assert
        assertThrows(Exception::class.java) {
            sut.executeWithInvalidInput()
        }
    }
}
            """.trimIndent()
        }

        private fun generateKotestTemplate(className: String, packageName: String): String {
            return """
package $packageName

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ${className}Test : DescribeSpec({
    describe("$className") {
        it("should return expected result when valid input") {
            val sut = ${className}()
            sut.execute() shouldBe "expected"
        }
    }
})
            """.trimIndent()
        }

        private fun generateMockkTemplate(className: String, packageName: String): String {
            return """
package $packageName

import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class ${className}Test {

    @Test
    fun `should interact with mock correctly`() {
        val mock = mockk<Dependency> {
            every { getData() } returns "mocked"
        }
        val sut = ${className}(mock)
        sut.execute() shouldBe "mocked"
    }
}
            """.trimIndent()
        }
    }
}

enum class TestFramework {
    JUNIT,
    KOTEST,
    MOCKK
}
