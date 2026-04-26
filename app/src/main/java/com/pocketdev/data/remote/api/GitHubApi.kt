package com.pocketdev.data.remote.api

import com.pocketdev.domain.model.GitHubCommitRequest
import com.pocketdev.domain.model.GitHubCommitResponse
import com.pocketdev.domain.model.GitHubFileContent
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {
    @GET("user")
    suspend fun getUser(): GitHubUserResponse

    @GET("user/repos")
    suspend fun getRepos(
        @Query("per_page") perPage: Int = 100,
        @Query("sort") sort: String = "updated"
    ): List<GitHubRepoResponse>

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String
    ): GitHubFileContent

    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun updateFile(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Body body: GitHubCommitRequest
    ): GitHubCommitResponse
}

@kotlinx.serialization.Serializable
data class GitHubUserResponse(
    val login: String,
    val name: String?,
    val avatar_url: String
)

@kotlinx.serialization.Serializable
data class GitHubRepoResponse(
    val id: Long,
    val name: String,
    val full_name: String,
    val description: String?,
    val html_url: String,
    val default_branch: String
)
