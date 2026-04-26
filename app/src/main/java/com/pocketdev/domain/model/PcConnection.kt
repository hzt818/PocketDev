package com.pocketdev.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PcConnectionConfig(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val apiKey: String,
    val isActive: Boolean = false
)

@Serializable
data class PcFileReadRequest(
    val path: String
)

@Serializable
data class PcFileWriteRequest(
    val path: String,
    val content: String
)

@Serializable
data class PcFileResponse(
    val success: Boolean,
    val content: String? = null,
    val error: String? = null
)

@Serializable
data class PcGitCommitRequest(
    val message: String,
    val files: List<String> = emptyList()
)

@Serializable
data class PcGitCommitResponse(
    val success: Boolean,
    val sha: String? = null,
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class PcShellRequest(
    val command: String,
    val cwd: String? = null
)

@Serializable
data class PcShellResponse(
    val success: Boolean,
    val stdout: String? = null,
    val stderr: String? = null,
    val exitCode: Int? = null,
    val error: String? = null
)

@Serializable
data class PcSystemInfo(
    val hostname: String,
    val platform: String,
    val pythonVersion: String,
    val gitVersion: String?,
    val workingDirectory: String
)

@Serializable
data class PcApiError(
    val error: String,
    val detail: String? = null
)
