package com.pocketdev.domain.model

data class BuildConfig(
    val projectPath: String,
    val tasks: List<String> = listOf("assembleDebug"),
    val variant: String? = null,
    val stackTraceEnabled: Boolean = false,
    val dryRun: Boolean = false,
    val cleanBeforeBuild: Boolean = false,
    val buildType: String = "debug",
    val parallel: Boolean = true,
    val daemon: Boolean = true,
    val stacktrace: Boolean = false,
    val info: Boolean = false
)

data class BuildResult(
    val id: String,
    val success: Boolean,
    val exitCode: Int,
    val output: String,
    val durationMs: Long,
    val timestamp: Long,
    val phase: BuildPhase = BuildPhase.FINALIZING
)

data class BuildProgress(
    val buildId: String,
    val phase: BuildPhase,
    val progress: Int,
    val message: String
)

enum class BuildPhase {
    INITIALIZING,
    CONFIGURING,
    EXECUTING,
    FINALIZING,
    COMPLETED,
    FAILED
}

data class GradleInfo(
    val available: Boolean,
    val version: String?,
    val homeDir: String?,
    val daemonRunning: Boolean
)