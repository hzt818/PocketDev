package com.pocketdev.domain.repository

import com.pocketdev.domain.model.RemoteBranch
import com.pocketdev.domain.model.RemoteCommit
import com.pocketdev.domain.model.RemoteFile
import com.pocketdev.domain.model.RemoteFileContent
import com.pocketdev.domain.model.RemoteRepository
import com.pocketdev.domain.model.RemoteRepositoryResult

interface RemoteRepositoryGateway {
    val provider: com.pocketdev.domain.model.RepositoryProvider

    suspend fun getRepositories(): RemoteRepositoryResult

    suspend fun getBranches(repository: RemoteRepository): RemoteRepositoryResult

    suspend fun getFileTree(
        repository: RemoteRepository,
        branch: String,
        path: String
    ): RemoteRepositoryResult

    suspend fun getFileContent(
        repository: RemoteRepository,
        path: String,
        ref: String? = null
    ): RemoteRepositoryResult

    suspend fun getCommits(
        repository: RemoteRepository,
        branch: String,
        path: String? = null
    ): RemoteRepositoryResult

    suspend fun commitFile(
        repository: RemoteRepository,
        path: String,
        content: String,
        message: String,
        sha: String? = null,
        branch: String? = null
    ): RemoteRepositoryResult

    suspend fun createBranch(
        repository: RemoteRepository,
        branchName: String,
        ref: String
    ): RemoteRepositoryResult

    suspend fun deleteBranch(
        repository: RemoteRepository,
        branchName: String
    ): RemoteRepositoryResult

    suspend fun isAuthenticated(): Boolean

    fun getAuthorizationUrl(): String

    suspend fun handleCallback(code: String): RemoteRepositoryResult

    fun getRepositoryId(repository: RemoteRepository): String
}