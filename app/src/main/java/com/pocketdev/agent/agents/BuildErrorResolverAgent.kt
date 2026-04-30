package com.pocketdev.agent.agents

import com.pocketdev.agent.core.Agent
import com.pocketdev.agent.protocol.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BuildErrorResolverAgent - Analyzes and fixes build errors.
 *
 * Handles:
 * - Compilation errors
 * - Dependency conflicts
 * - Missing imports
 * - Type mismatches
 * - Resource issues
 */
@Singleton
class BuildErrorResolverAgent @Inject constructor() : Agent {
    override val type = AgentType.BUILD_ERROR_RESOLVER
    override val name = "Build Error Resolver"
    override val description = "Analyzes and fixes build and compilation errors"

    private val errorPatterns = listOf(
        ErrorPattern(
            pattern = Regex("""cannot find symbol[\s\S]*?symbol:\s*(\w+)"""),
            type = ErrorType.MISSING_SYMBOL,
            explanation = "The referenced symbol (class, function, variable) is not defined in the scope"
        ),
        ErrorPattern(
            pattern = Regex("""unresolved reference:\s*(\w+)"""),
            type = ErrorType.UNRESOLVED_REFERENCE,
            explanation = "The reference cannot be resolved - check imports"
        ),
        ErrorPattern(
            pattern = Regex("""cannot inherit from\s*(\w+)"""),
            type = ErrorType.INHERITANCE_ERROR,
            explanation = "Class cannot inherit - check if class exists and is not final"
        ),
        ErrorPattern(
            pattern = Regex("""type mismatch[\s\S]*?found:\s*(\w+)[\s\S]*?required:\s*(\w+)"""),
            type = ErrorType.TYPE_MISMATCH,
            explanation = "Type mismatch - cast or convert the value"
        ),
        ErrorPattern(
            pattern = Regex("""no such property[\s\S]*?property:\s*(\w+)"""),
            type = ErrorType.MISSING_PROPERTY,
            explanation = "Property does not exist on the type"
        ),
        ErrorPattern(
            pattern = Regex("""function invocation expected[\s\S]*?got\s+(\w+)"""),
            type = ErrorType.FUNCTION_EXPECTED,
            explanation = "Attempting to call something that's not a function"
        ),
        ErrorPattern(
            pattern = Regex("""null can not be a value of non-null type[\s\S]*?(\w+)"""),
            type = ErrorType.NULL_POINTER,
            explanation = "Null assigned to non-null type - use safe call or null check"
        ),
        ErrorPattern(
            pattern = Regex("""overload resolution ambiguity[\s\S]*?(\w+)"""),
            type = ErrorType.AMBIGUOUS_OVERLOAD,
            explanation = "Multiple matching overloads - be more specific with types"
        ),
        ErrorPattern(
            pattern = Regex("""duplicate class[\s\S]*?(\w+)"""),
            type = ErrorType.DUPLICATE_CLASS,
            explanation = "Class defined multiple times - check for duplicate imports"
        ),
        ErrorPattern(
            pattern = Regex("""could not find dependency[\s\S]*?(\w+)"""),
            type = ErrorType.MISSING_DEPENDENCY,
            explanation = "Gradle dependency not found - check spelling and repository"
        ),
        ErrorPattern(
            pattern = Regex("""Could not resolve[\s\S]*?(\w+)"""),
            type = ErrorType.DEPENDENCY_RESOLUTION,
            explanation = "Dependency cannot be resolved - check repository configuration"
        )
    )

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is BuildFixRequest) { "Invalid request type" }

        val fixes = mutableListOf<CodeFix>()
        val findings = mutableListOf<Finding>()

        val errorOutput = request.errorOutput
        val errors = parseErrors(errorOutput)

        for (error in errors) {
            val fix = generateFix(error, request.context)
            if (fix != null) {
                fixes.add(fix)
                findings.add(
                    Finding(
                        type = FindingType.BUG,
                        severity = Severity.HIGH,
                        file = fix.file,
                        line = fix.line,
                        message = error.explanation,
                        suggestion = fix.explanation
                    )
                )
            }
        }

        return BuildFixResponse(
            requestId = request.id,
            success = fixes.isNotEmpty(),
            findings = findings,
            fixes = fixes,
            explanation = generateExplanation(fixes)
        )
    }

    private fun parseErrors(errorOutput: String): List<ParsedError> {
        val errors = mutableListOf<ParsedError>()

        for (pattern in errorPatterns) {
            pattern.pattern.findAll(errorOutput).forEach { match ->
                val (type, explanation) = pattern.type to pattern.explanation
                errors.add(
                    ParsedError(
                        type = type,
                        message = match.value,
                        groups = match.groupValues.toList(),
                        explanation = explanation
                    )
                )
            }
        }

        return errors
    }

    private fun generateFix(error: ParsedError, context: AgentContext): CodeFix? {
        return when (error.type) {
            ErrorType.MISSING_SYMBOL -> {
                val symbol = error.groups.getOrNull(1) ?: return null
                val isKotlin = error.message.contains(".kt:")
                val isClass = symbol[0].isUpperCase()
                val isInterface = symbol.endsWith("Listener") || symbol.endsWith("Callback")
                val isViewModel = symbol.endsWith("ViewModel")
                val isRepository = symbol.endsWith("Repository")
                val isUseCase = symbol.endsWith("UseCase")

                val fixed = when {
                    isInterface -> """
                        |interface $symbol {
                        |    fun onResult(data: Any)
                        |}
                    """.trimMargin()
                    isViewModel -> """
                        |@HiltViewModel
                        |class $symbol @Inject constructor(
                        |    private val repository: com.pocketdev.domain.repository.FileRepository
                        |) : ViewModel() {
                        |    // TODO: Implement ViewModel logic
                        |}
                    """.trimMargin()
                    isRepository -> """
                        |class $symbol @Inject constructor() {
                        |    // TODO: Implement repository logic
                        |}
                    """.trimMargin()
                    isUseCase -> """
                        |class $symbol @Inject constructor(
                        |    private val repository: com.pocketdev.domain.repository.FileRepository
                        |) {
                        |    suspend operator fun invoke(): Result<Any> {
                        |        TODO("Implement $symbol")
                        |    }
                        |}
                    """.trimMargin()
                    isClass -> """
                        |class $symbol {
                        |    // TODO: Implement class
                        |}
                    """.trimMargin()
                    else -> """
                        |fun $symbol(): Any {
                        |    TODO("Implement $symbol")
                        |}
                    """.trimMargin()
                }

                CodeFix(
                    file = extractFileFromError(error.message),
                    line = extractLineFromError(error.message),
                    original = "// TODO: Implement $symbol",
                    fixed = fixed,
                    explanation = "Add the missing $symbol declaration"
                )
            }
            ErrorType.UNRESOLVED_REFERENCE -> {
                val ref = error.groups.getOrNull(1) ?: return null
                CodeFix(
                    file = extractFileFromError(error.message),
                    line = extractLineFromError(error.message),
                    original = ref,
                    fixed = "// Add import for $ref",
                    explanation = "Add the missing import statement"
                )
            }
            ErrorType.TYPE_MISMATCH -> {
                val found = error.groups.getOrNull(1) ?: return null
                val required = error.groups.getOrNull(2) ?: return null
                CodeFix(
                    file = extractFileFromError(error.message),
                    line = extractLineFromError(error.message),
                    original = "// type mismatch: found $found required $required",
                    fixed = "// Cast or convert: $found as $required",
                    explanation = "Add type conversion"
                )
            }
            ErrorType.NULL_POINTER -> {
                val type = error.groups.getOrNull(1) ?: return null
                CodeFix(
                    file = extractFileFromError(error.message),
                    line = extractLineFromError(error.message),
                    original = "// null assignment",
                    fixed = "// Use safe call: ?. or provide default",
                    explanation = "Handle null safety for type $type"
                )
            }
            ErrorType.MISSING_DEPENDENCY -> {
                val dep = error.groups.getOrNull(1) ?: return null
                CodeFix(
                    file = "build.gradle.kts",
                    line = null,
                    original = "// dependency missing",
                    fixed = "implementation(\"$dep\")",
                    explanation = "Add missing dependency to build.gradle.kts"
                )
            }
            else -> null
        }
    }

    private fun extractFileFromError(message: String): String {
        return Regex("""([\w/]+)\.kt:(\d+)""")
            .find(message)
            ?.groupValues
            ?.getOrNull(1) ?: "Unknown"
    }

    private fun extractLineFromError(message: String): Int? {
        return Regex("""([\w/]+)\.kt:(\d+)""")
            .find(message)
            ?.groupValues
            ?.getOrNull(2)
            ?.toIntOrNull()
    }

    private fun generateExplanation(fixes: List<CodeFix>): String {
        return when {
            fixes.isEmpty() -> "No automatic fixes available"
            fixes.size == 1 -> "Generated 1 fix"
            else -> "Generated ${fixes.size} fixes"
        }
    }

    private data class ParsedError(
        val type: ErrorType,
        val message: String,
        val groups: List<String>,
        val explanation: String
    )

    private enum class ErrorType {
        MISSING_SYMBOL,
        UNRESOLVED_REFERENCE,
        INHERITANCE_ERROR,
        TYPE_MISMATCH,
        MISSING_PROPERTY,
        FUNCTION_EXPECTED,
        NULL_POINTER,
        AMBIGUOUS_OVERLOAD,
        DUPLICATE_CLASS,
        MISSING_DEPENDENCY,
        DEPENDENCY_RESOLUTION
    }

    private data class ErrorPattern(
        val pattern: Regex,
        val type: ErrorType,
        val explanation: String
    )
}

/**
 * Common build errors and their solutions.
 */
object BuildErrorSolutions {
    val gradleErrors = mapOf(
        "Could not resolve dependency" to listOf(
            "Check if repository is configured",
            "Verify dependency spelling and version",
            "Try adding google() and mavenCentral() repositories"
        ),
        "Configuration 'compile' is obsolete" to listOf(
            "Replace 'compile' with 'implementation' or 'api'",
            "'api' is for transitive dependencies, 'implementation' hides them"
        ),
        "MinifyEnabled requires R8" to listOf(
            "R8 is enabled by default in release builds",
            "Configure ProGuard rules if needed"
        )
    )

    val kotlinErrors = mapOf(
        "Unresolved reference" to listOf(
            "Check import statement",
            "Verify class/object exists",
            "Check for typos in name"
        ),
        "Type mismatch" to listOf(
            "Cast with 'as' or 'as?'",
            "Convert with .toInt(), .toString(), etc.",
            "Check nullability with ?. or !!"
        ),
        "Expecting member declaration" to listOf(
            "Check for missing braces",
            "Verify correct indentation",
            "Class members need proper declaration keywords"
        )
    )

    fun getSolutions(errorKeyword: String): List<String> {
        return kotlinErrors.entries
            .find { errorKeyword.contains(it.key, ignoreCase = true) }
            ?.value
            ?: gradleErrors.entries
                .find { errorKeyword.contains(it.key, ignoreCase = true) }
                ?.value
            ?: listOf("Review error message and Kotlin/Gradle documentation")
    }
}
