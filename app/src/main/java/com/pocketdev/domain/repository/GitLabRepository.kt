package com.pocketdev.domain.repository

import com.pocketdev.domain.model.RemoteBranch
import com.pocketdev.domain.model.RemoteCommit
import com.pocketdev.domain.model.RemoteFile
import com.pocketdev.domain.model.RemoteFileContent
import com.pocketdev.domain.model.RemoteRepository

interface GitLabRepository {
    suspend fun getUser(): Result<GitLabUser>
    suspend fun getProjects(): Result<List<RemoteRepository>>
    suspend fun getProject(projectId: String): Result<RemoteRepository>
    suspend fun getBranches(projectId: String): Result<List<RemoteBranch>>
    suspend fun getFileTree(projectId: String, branch: String, path: String): Result<List<RemoteFile>>
    suspend fun getFileContent(projectId: String, path: String, ref: String): Result<RemoteFileContent?>
    suspend fun getCommits(projectId: String, branch: String, path: String?): Result<List<RemoteCommit>>
    suspend fun commitFile(
        projectId: String,
        path: String,
        content: String,
        message: String,
        branch: String
    ): Result<RemoteCommit>
    suspend fun createBranch(projectId: String, branch: String, ref: String): Result<Unit>
    suspend fun deleteBranch(projectId: String, branch: String): Result<Unit>
    suspend fun isAuthenticated(): Boolean
    fun getAuthorizationUrl(): String
    suspend fun handleCallback(code: String): Result<String>
}

data class GitLabUser(
    val id: Long,
    val login: String,
    val name: String,
    val avatarUrl: String?,
    val accessToken: String
)
