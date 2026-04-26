package com.pocketdev.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val htmlUrl: String,
    val defaultBranch: String
)

@Serializable
data class GitHubFileContent(
    val name: String,
    val path: String,
    val sha: String,
    val content: String,
    val encoding: String
)

@Serializable
data class GitHubCommitRequest(
    val message: String,
    val content: String,
    val sha: String? = null
)

@Serializable
data class GitHubCommitResponse(
    val commit: GitHubCommitInfo
)

@Serializable
data class GitHubCommitInfo(
    val sha: String,
    val htmlUrl: String
)

data class GitHubUser(
    val login: String,
    val name: String?,
    val avatarUrl: String,
    val accessToken: String
)
