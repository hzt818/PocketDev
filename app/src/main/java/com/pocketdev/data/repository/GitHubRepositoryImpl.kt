package com.pocketdev.data.repository

import android.util.Base64
import com.pocketdev.data.local.UserSettingsDataStore
import com.pocketdev.data.remote.api.GitHubApi
import com.pocketdev.data.remote.api.GitHubCommitResponse as ApiCommitResponse
import com.pocketdev.domain.model.GitHubCommitAuthor
import com.pocketdev.domain.model.GitHubCommitInfo
import com.pocketdev.domain.model.GitHubCommitRequest
import com.pocketdev.domain.model.GitHubCommitResponse
import com.pocketdev.domain.model.GitHubCommitTree
import com.pocketdev.domain.model.GitHubFileContent
import com.pocketdev.domain.model.GitHubRepo
import com.pocketdev.domain.model.GitHubUser
import com.pocketdev.domain.model.RemoteBranch
import com.pocketdev.domain.model.RemoteCommit
import com.pocketdev.domain.model.RemoteCommitAuthor
import com.pocketdev.domain.model.RemoteFile
import com.pocketdev.domain.model.RemoteFileType
import com.pocketdev.domain.repository.GitHubRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GitHubRepositoryImpl @Inject constructor(
    private val gitHubApi: GitHubApi,
    private val userSettingsDataStore: UserSettingsDataStore,
    @Named("github_token_provider") private val tokenProvider: () -> String?
) : GitHubRepository {

    companion object {
        private const val CLIENT_ID = "Ov23liMKMvMxdzfx7TY6"
        private const val GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize"
    }

    override suspend fun getUser(): Result<GitHubUser> {
        return try {
            val response = gitHubApi.getUser()
            val token = tokenProvider() ?: return Result.failure(Exception("Not authenticated"))
            Result.success(
                GitHubUser(
                    login = response.login,
                    name = response.name,
                    avatarUrl = response.avatar_url,
                    accessToken = token
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRepos(): Result<List<GitHubRepo>> {
        return try {
            val response = gitHubApi.getRepos()
            Result.success(
                response.map {
                    GitHubRepo(
                        id = it.id,
                        name = it.name,
                        fullName = it.full_name,
                        description = it.description,
                        htmlUrl = it.html_url,
                        defaultBranch = it.default_branch
                    )
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFileContent(owner: String, repo: String, path: String): Result<GitHubFileContent?> {
        return try {
            val response = gitHubApi.getFileContent(owner, repo, path)
            Result.success(
                GitHubFileContent(
                    name = response.name,
                    path = response.path,
                    sha = response.sha,
                    content = response.content,
                    encoding = response.encoding
                )
            )
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                Result.success(null)
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun commitFile(
        owner: String,
        repo: String,
        path: String,
        request: GitHubCommitRequest
    ): Result<GitHubCommitResponse> {
        return try {
            val apiRequest = com.pocketdev.data.remote.api.GitHubCommitRequestBody(
                message = request.message,
                content = request.content,
                sha = request.sha
            )
            val response: ApiCommitResponse = gitHubApi.updateFile(owner, repo, path, apiRequest)
            Result.success(
                GitHubCommitResponse(
                    sha = response.sha,
                    htmlUrl = response.html_url,
                    commit = GitHubCommitInfo(
                        sha = response.commit.sha,
                        htmlUrl = response.html_url,
                        message = response.commit.message,
                        author = GitHubCommitAuthor(
                            name = response.commit.author.name,
                            email = response.commit.author.email,
                            date = response.commit.author.date
                        ),
                        tree = GitHubCommitTree(
                            sha = response.commit.tree.sha
                        )
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return tokenProvider() != null
    }

    override fun getAuthorizationUrl(): String {
        return "$GITHUB_AUTH_URL?client_id=$CLIENT_ID&scope=repo"
    }

    override suspend fun handleCallback(code: String): Result<String> {
        return Result.failure(Exception("OAuth callback handling not implemented - use Chrome Custom Tabs"))
    }

    override suspend fun getBranches(owner: String, repo: String): Result<List<RemoteBranch>> {
        return try {
            val response = gitHubApi.getBranches(owner, repo)
            Result.success(
                response.map {
                    RemoteBranch(
                        name = it.name,
                        sha = it.commit.sha,
                        isDefault = it.name == "main" || it.name == "master",
                        isProtected = it.protected_
                    )
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFileTree(owner: String, repo: String, branch: String, path: String): Result<List<RemoteFile>> {
        return try {
            val branchRef = gitHubApi.getBranchRef(owner, repo, branch)
            val treeResponse = gitHubApi.getFileTree(owner, repo, branchRef.sha)

            val filteredTree = if (path.isEmpty()) {
                treeResponse.tree
            } else {
                treeResponse.tree.filter { it.path.startsWith(path) }
            }

            val files = filteredTree.mapNotNull { item ->
                val itemPath = item.path.removePrefix("$path/").removePrefix(path)
                if (itemPath.contains("/")) return@mapNotNull null

                RemoteFile(
                    name = itemPath.ifEmpty { item.path.substringAfterLast("/") },
                    path = item.path,
                    sha = item.sha,
                    size = item.size ?: 0,
                    type = when (item.type) {
                        "blob" -> RemoteFileType.FILE
                        "tree" -> RemoteFileType.DIRECTORY
                        "symlink" -> RemoteFileType.SYMLINK
                        "submodule" -> RemoteFileType.SUBMODULE
                        else -> RemoteFileType.FILE
                    },
                    downloadUrl = item.url
                )
            }
            Result.success(files)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCommits(owner: String, repo: String, branch: String, path: String?): Result<List<RemoteCommit>> {
        return try {
            val response = gitHubApi.getCommits(owner, repo, branch, path)
            Result.success(
                response.map {
                    RemoteCommit(
                        sha = it.sha,
                        message = it.commit.message.lines().firstOrNull() ?: "",
                        author = RemoteCommitAuthor(
                            name = it.commit.author.name,
                            email = it.commit.author.email,
                            avatarUrl = null
                        ),
                        date = it.commit.author.date,
                        htmlUrl = it.html_url
                    )
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBranch(owner: String, repo: String, branch: String, sha: String): Result<Unit> {
        return try {
            gitHubApi.createBranchRef(
                owner,
                repo,
                com.pocketdev.data.remote.api.GitHubCreateRefRequest(
                    ref = "refs/heads/$branch",
                    sha = sha
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBranch(owner: String, repo: String, branch: String): Result<Unit> {
        return try {
            gitHubApi.deleteBranch(owner, repo, branch)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFileContentAtRef(owner: String, repo: String, path: String, ref: String): Result<GitHubFileContent?> {
        return try {
            val response = gitHubApi.getFileContentAtRef(owner, repo, path, ref)
            Result.success(
                GitHubFileContent(
                    name = response.name,
                    path = response.path,
                    sha = response.sha,
                    content = response.content,
                    encoding = response.encoding
                )
            )
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                Result.success(null)
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
