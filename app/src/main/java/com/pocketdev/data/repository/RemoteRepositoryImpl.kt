package com.pocketdev.data.repository

import android.util.Base64
import com.pocketdev.domain.model.GitHubCommitRequest
import com.pocketdev.domain.model.RemoteBranch
import com.pocketdev.domain.model.RemoteCommit
import com.pocketdev.domain.model.RemoteCommitAuthor
import com.pocketdev.domain.model.RemoteFile
import com.pocketdev.domain.model.RemoteFileContent
import com.pocketdev.domain.model.RemoteFileType
import com.pocketdev.domain.model.RemoteRepository
import com.pocketdev.domain.model.RemoteRepositoryResult
import com.pocketdev.domain.model.RepositoryProvider
import com.pocketdev.domain.repository.GitHubRepository
import com.pocketdev.domain.repository.RemoteRepositoryGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRepositoryImpl @Inject constructor(
    private val gitHubRepository: GitHubRepository
) : RemoteRepositoryGateway {

    override val provider: RepositoryProvider = RepositoryProvider.GITHUB

    override suspend fun getRepositories(): RemoteRepositoryResult {
        val result = gitHubRepository.getRepos()
        return if (result.isSuccess) {
            val repos = result.getOrNull()?.map { repo ->
                RemoteRepository(
                    id = repo.id,
                    name = repo.name,
                    fullName = repo.fullName,
                    description = repo.description,
                    htmlUrl = repo.htmlUrl,
                    defaultBranch = repo.defaultBranch,
                    provider = RepositoryProvider.GITHUB,
                    owner = repo.fullName.split("/").firstOrNull() ?: ""
                )
            } ?: emptyList()
            RemoteRepositoryResult.Success(repos)
        } else {
            RemoteRepositoryResult.Error(
                result.exceptionOrNull()?.message ?: "Failed to fetch repositories"
            )
        }
    }

    override suspend fun getBranches(repository: RemoteRepository): RemoteRepositoryResult {
        if (repository.provider != RepositoryProvider.GITHUB) {
            return RemoteRepositoryResult.Error("Unsupported provider")
        }

        val result = gitHubRepository.getBranches(repository.owner, repository.name)
        return if (result.isSuccess) {
            RemoteRepositoryResult.Success(result.getOrNull() ?: emptyList())
        } else {
            RemoteRepositoryResult.Error(
                result.exceptionOrNull()?.message ?: "Failed to fetch branches"
            )
        }
    }

    override suspend fun getFileTree(
        repository: RemoteRepository,
        branch: String,
        path: String
    ): RemoteRepositoryResult {
        if (repository.provider != RepositoryProvider.GITHUB) {
            return RemoteRepositoryResult.Error("Unsupported provider")
        }

        val result = gitHubRepository.getFileTree(repository.owner, repository.name, branch, path)
        return if (result.isSuccess) {
            RemoteRepositoryResult.Success(result.getOrNull() ?: emptyList())
        } else {
            RemoteRepositoryResult.Error(
                result.exceptionOrNull()?.message ?: "Failed to fetch file tree"
            )
        }
    }

    override suspend fun getFileContent(
        repository: RemoteRepository,
        path: String,
        ref: String?
    ): RemoteRepositoryResult {
        if (repository.provider != RepositoryProvider.GITHUB) {
            return RemoteRepositoryResult.Error("Unsupported provider")
        }

        val effectiveRef = ref ?: repository.defaultBranch
        val contentResult = if (ref != null) {
            gitHubRepository.getFileContentAtRef(repository.owner, repository.name, path, ref)
        } else {
            gitHubRepository.getFileContent(repository.owner, repository.name, path)
        }

        return if (contentResult.isSuccess) {
            val content = contentResult.getOrNull()
            if (content != null) {
                val decodedContent = if (content.encoding == "base64") {
                    String(Base64.decode(content.content, Base64.NO_WRAP))
                } else {
                    content.content
                }
                RemoteRepositoryResult.Success(
                    RemoteFileContent(
                        name = content.name,
                        path = content.path,
                        sha = content.sha,
                        content = decodedContent,
                        encoding = content.encoding,
                        size = decodedContent.length.toLong(),
                        type = RemoteFileType.FILE
                    )
                )
            } else {
                RemoteRepositoryResult.Error("File not found", 404)
            }
        } else {
            val exception = contentResult.exceptionOrNull()
            if (exception is retrofit2.HttpException && exception.code() == 404) {
                RemoteRepositoryResult.Error("File not found", 404)
            } else {
                RemoteRepositoryResult.Error(exception?.message ?: "Failed to fetch file content")
            }
        }
    }

    override suspend fun getCommits(
        repository: RemoteRepository,
        branch: String,
        path: String?
    ): RemoteRepositoryResult {
        if (repository.provider != RepositoryProvider.GITHUB) {
            return RemoteRepositoryResult.Error("Unsupported provider")
        }

        val result = gitHubRepository.getCommits(repository.owner, repository.name, branch, path)
        return if (result.isSuccess) {
            RemoteRepositoryResult.Success(result.getOrNull() ?: emptyList())
        } else {
            RemoteRepositoryResult.Error(
                result.exceptionOrNull()?.message ?: "Failed to fetch commits"
            )
        }
    }

    override suspend fun commitFile(
        repository: RemoteRepository,
        path: String,
        content: String,
        message: String,
        sha: String?,
        branch: String?
    ): RemoteRepositoryResult {
        if (repository.provider != RepositoryProvider.GITHUB) {
            return RemoteRepositoryResult.Error("Unsupported provider")
        }

        val commitRequest = GitHubCommitRequest(
            message = message,
            content = Base64.encodeToString(content.toByteArray(), Base64.NO_WRAP),
            sha = sha
        )

        val result = gitHubRepository.commitFile(
            repository.owner,
            repository.name,
            path,
            commitRequest
        )
        return if (result.isSuccess) {
            RemoteRepositoryResult.Success(result.getOrNull()?.commit?.sha ?: "")
        } else {
            RemoteRepositoryResult.Error(
                result.exceptionOrNull()?.message ?: "Failed to commit file"
            )
        }
    }

    override suspend fun createBranch(
        repository: RemoteRepository,
        branchName: String,
        ref: String
    ): RemoteRepositoryResult {
        if (repository.provider != RepositoryProvider.GITHUB) {
            return RemoteRepositoryResult.Error("Unsupported provider")
        }

        val result = gitHubRepository.createBranch(repository.owner, repository.name, branchName, ref)
        return if (result.isSuccess) {
            RemoteRepositoryResult.Success(Unit)
        } else {
            RemoteRepositoryResult.Error(
                result.exceptionOrNull()?.message ?: "Failed to create branch"
            )
        }
    }

    override suspend fun deleteBranch(
        repository: RemoteRepository,
        branchName: String
    ): RemoteRepositoryResult {
        if (repository.provider != RepositoryProvider.GITHUB) {
            return RemoteRepositoryResult.Error("Unsupported provider")
        }

        val result = gitHubRepository.deleteBranch(repository.owner, repository.name, branchName)
        return if (result.isSuccess) {
            RemoteRepositoryResult.Success(Unit)
        } else {
            RemoteRepositoryResult.Error(
                result.exceptionOrNull()?.message ?: "Failed to delete branch"
            )
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return gitHubRepository.isAuthenticated()
    }

    override fun getAuthorizationUrl(): String {
        return gitHubRepository.getAuthorizationUrl()
    }

    override suspend fun handleCallback(code: String): RemoteRepositoryResult {
        val result = gitHubRepository.handleCallback(code)
        return if (result.isSuccess) {
            RemoteRepositoryResult.Success(result.getOrNull() ?: "")
        } else {
            RemoteRepositoryResult.Error(
                result.exceptionOrNull()?.message ?: "Authentication failed"
            )
        }
    }

    override fun getRepositoryId(repository: RemoteRepository): String {
        return "${repository.provider.name}:${repository.fullName}"
    }
}