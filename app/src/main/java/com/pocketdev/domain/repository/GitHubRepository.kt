package com.pocketdev.domain.repository

import com.pocketdev.domain.model.GitHubCommitRequest
import com.pocketdev.domain.model.GitHubCommitResponse
import com.pocketdev.domain.model.GitHubFileContent
import com.pocketdev.domain.model.GitHubRepo
import com.pocketdev.domain.model.GitHubUser

interface GitHubRepository {
    suspend fun getUser(): Result<GitHubUser>
    suspend fun getRepos(): Result<List<GitHubRepo>>
    suspend fun getFileContent(owner: String, repo: String, path: String): Result<GitHubFileContent?>
    suspend fun commitFile(
        owner: String,
        repo: String,
        path: String,
        request: GitHubCommitRequest
    ): Result<GitHubCommitResponse>
    suspend fun isAuthenticated(): Boolean
    fun getAuthorizationUrl(): String
    suspend fun handleCallback(code: String): Result<String>
}
