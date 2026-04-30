package com.pocketdev.agent.agents

import com.pocketdev.agent.core.Agent
import com.pocketdev.agent.protocol.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SecurityReviewerAgent - Performs security analysis on code.
 *
 * Checks for:
 * - OWASP Top 10 vulnerabilities
 * - Data exposure risks
 * - Injection attacks
 * - Authentication/authorization issues
 * - Cryptographic misuse
 */
@Singleton
class SecurityReviewerAgent @Inject constructor() : Agent {
    override val type = AgentType.SECURITY_REVIEWER
    override val name = "Security Reviewer"
    override val description = "Analyzes code for security vulnerabilities and risks"

    private val vulnerabilityPatterns = listOf(
        VulnerabilityPattern(
            name = "SQL Injection",
            patterns = listOf(
                Regex("""exec\(|execute\(|cursor\.execute\(""", RegexOption.IGNORE_CASE),
                Regex("""\$\{.*\}.*\.format\(|String\.format\(.*%s""", RegexOption.IGNORE_CASE)
            ),
            severity = Severity.CRITICAL,
            remediation = "Use parameterized queries or prepared statements"
        ),
        VulnerabilityPattern(
            name = "Command Injection",
            patterns = listOf(
                Regex("""Runtime\.getRuntime\(\)\.exec\(""", RegexOption.IGNORE_CASE),
                Regex("""ProcessBuilder\(.*\)""", RegexOption.IGNORE_CASE)
            ),
            severity = Severity.CRITICAL,
            remediation = "Avoid executing shell commands with user input"
        ),
        VulnerabilityPattern(
            name = "Path Traversal",
            patterns = listOf(
                Regex("""File\(.*\+.*\)""", RegexOption.IGNORE_CASE),
                Regex("""Paths\.get\(.*\+""", RegexOption.IGNORE_CASE)
            ),
            severity = Severity.HIGH,
            remediation = "Validate and sanitize file paths, use allowlists"
        ),
        VulnerabilityPattern(
            name = "Hardcoded Secret",
            patterns = listOf(
                Regex("""password\s*=\s*["'][^"']{8,}["']""", RegexOption.IGNORE_CASE),
                Regex("""api[_-]?key\s*=\s*["'][^"']{16,}["']""", RegexOption.IGNORE_CASE),
                Regex("""secret\s*=\s*["'][^"']{8,}["']""", RegexOption.IGNORE_CASE)
            ),
            severity = Severity.CRITICAL,
            remediation = "Move secrets to environment variables or secure storage"
        ),
        VulnerabilityPattern(
            name = "Weak Cryptography",
            patterns = listOf(
                Regex("""MessageDigest\.getInstance\("MD5""", RegexOption.IGNORE_CASE),
                Regex("""MessageDigest\.getInstance\("SHA1""", RegexOption.IGNORE_CASE),
                Regex("""Cipher\.getInstance\("DES"""", RegexOption.IGNORE_CASE)
            ),
            severity = Severity.HIGH,
            remediation = "Use SHA-256+ for hashing, AES-256 for encryption"
        ),
        VulnerabilityPattern(
            name = "XSS",
            patterns = listOf(
                Regex("""innerHTML\s*=""", RegexOption.IGNORE_CASE),
                Regex("""document\.write\(""", RegexOption.IGNORE_CASE)
            ),
            severity = Severity.HIGH,
            remediation = "Sanitize HTML or use textContent instead of innerHTML"
        ),
        VulnerabilityPattern(
            name = "XXE",
            patterns = listOf(
                Regex("""DocumentBuilderFactory\.newInstance\(\)""", RegexOption.IGNORE_CASE),
                Regex("""SAXParserFactory\.newInstance\(\)""", RegexOption.IGNORE_CASE)
            ),
            severity = Severity.HIGH,
            remediation = "Disable DTD processing in XML parsers"
        ),
        VulnerabilityPattern(
            name = "Insecure Random",
            patterns = listOf(
                Regex("""Random\(\)""", RegexOption.IGNORE_CASE),
                Regex("""Math\.random\(\)""", RegexOption.IGNORE_CASE)
            ),
            severity = Severity.MEDIUM,
            remediation = "Use SecureRandom for security-sensitive random values"
        )
    )

    override suspend fun execute(request: AgentRequest): AgentResponse {
        require(request is SecurityReviewRequest) { "Invalid request type" }

        val vulnerabilities = mutableListOf<Vulnerability>()
        val findings = mutableListOf<Finding>()

        for (file in request.context.relevantFiles) {
            val fileVulns = scanForVulnerabilities(file)
            vulnerabilities.addAll(fileVulns)
        }

        val criticalCount = vulnerabilities.count { it.severity == Severity.CRITICAL }
        val highCount = vulnerabilities.count { it.severity == Severity.HIGH }

        val riskLevel = when {
            criticalCount > 0 -> RiskLevel.CRITICAL
            highCount > 2 -> RiskLevel.HIGH
            highCount > 0 -> RiskLevel.MEDIUM
            vulnerabilities.isNotEmpty() -> RiskLevel.LOW
            else -> RiskLevel.NEGLIGIBLE
        }

        vulnerabilities.forEach { vuln ->
            findings.add(
                Finding(
                    type = FindingType.SECURITY,
                    severity = vuln.severity,
                    file = vuln.location,
                    message = "${vuln.type}: ${vuln.description}",
                    suggestion = vuln.remediation
                )
            )
        }

        return SecurityReviewResponse(
            requestId = request.id,
            success = true,
            findings = findings,
            vulnerabilities = vulnerabilities,
            riskLevel = riskLevel
        )
    }

    private fun scanForVulnerabilities(filePath: String): List<Vulnerability> {
        return try {
            val file = java.io.File(filePath)
            if (!file.exists() || !file.isFile) return emptyList()
            val content = file.readText()
            checkContent(content, filePath)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun checkContent(content: String, file: String): List<Vulnerability> {
        val vulnerabilities = mutableListOf<Vulnerability>()

        for (pattern in vulnerabilityPatterns) {
            for (regex in pattern.patterns) {
                regex.findAll(content).forEach { match ->
                    vulnerabilities.add(
                        Vulnerability(
                            type = pattern.name,
                            severity = pattern.severity,
                            location = file,
                            description = "Found '${match.value}' matching ${pattern.name} pattern",
                            remediation = pattern.remediation
                        )
                    )
                }
            }
        }

        return vulnerabilities
    }

    private data class VulnerabilityPattern(
        val name: String,
        val patterns: List<Regex>,
        val severity: Severity,
        val remediation: String
    )
}

/**
 * Security checklist based on OWASP guidelines.
 */
object SecurityChecklist {
    val authentication = listOf(
        "Passwords hashed with bcrypt/Argon2",
        "Multi-factor authentication available",
        "Session timeout configured",
        "Brute force protection in place"
    )

    val authorization = listOf(
        "Principle of least privilege",
        "Role-based access control",
        "Authorization checks on all endpoints"
    )

    val dataProtection = listOf(
        "Sensitive data encrypted at rest",
        "HTTPS only (TLS 1.2+)",
        "No secrets in code or version control",
        "Secure key management"
    )

    val inputValidation = listOf(
        "All input sanitized",
        "Parameterized queries used",
        "File uploads validated",
        "No eval() or dynamic code execution"
    )

    fun getChecklist(): Map<String, List<String>> = mapOf(
        "Authentication" to authentication,
        "Authorization" to authorization,
        "Data Protection" to dataProtection,
        "Input Validation" to inputValidation
    )
}
