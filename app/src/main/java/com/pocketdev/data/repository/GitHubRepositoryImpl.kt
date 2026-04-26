package com.pocketdev.data.repository

import android.util.Base64
import com.pocketdev.data.local.UserSettingsDataStore
import com.pocketdev.data.remote.api.GitHubApi
import com.pocketdev.domain.model.GitHubCommitRequest
import com.pocketdev.domain.model.GitHubCommitResponse
import com.pocketdev.domain.model.GitHubFileContent
import com.pocketdev.domain.model.GitHubRepo
import com.pocketdev.domain.model.GitHubUser
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
            val response = gitHubApi.updateFile(owner, repo, path, request)
            Result.success(
                GitHubCommitResponse(
                    commit = com.pocketdev.domain.model.GitHubCommitInfo(
                        sha = response.commit.sha,
                        htmlUrl = response.commit.htmlUrl
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
}
