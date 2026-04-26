package com.pocketdev.domain.repository

import com.pocketdev.domain.model.GitHubCommitRequest
import com.pocketdev.domain.model.GitHubCommitResponse
import com.pocketdev.domain.model.GitHubFileContent
import com.pocketdev.domain.model.GitHubRepo
import com.pocketdev.domain.model.GitHubUser
import com.pocketdev.domain.model.RemoteBranch
import com.pocketdev.domain.model.RemoteCommit
import com.pocketdev.domain.model.RemoteFile

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

    suspend fun getBranches(owner: String, repo: String): Result<List<RemoteBranch>>
    suspend fun getFileTree(owner: String, repo: String, branch: String, path: String): Result<List<RemoteFile>>
    suspend fun getCommits(owner: String, repo: String, branch: String, path: String?): Result<List<RemoteCommit>>
    suspend fun createBranch(owner: String, repo: String, branch: String, sha: String): Result<Unit>
    suspend fun deleteBranch(owner: String, repo: String, branch: String): Result<Unit>
    suspend fun getFileContentAtRef(owner: String, repo: String, path: String, ref: String): Result<GitHubFileContent?>
}
