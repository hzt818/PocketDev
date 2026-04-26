package com.pocketdev.data.remote.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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
    ): GitHubFileContentResponse

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContentAtRef(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Query("ref") ref: String
    ): GitHubFileContentResponse

    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun updateFile(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Body body: GitHubCommitRequestBody
    ): GitHubCommitResponse

    @GET("repos/{owner}/{repo}/branches")
    suspend fun getBranches(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<GitHubBranchResponse>

    @GET("repos/{owner}/{repo}/git/trees/{tree_sha}")
    suspend fun getFileTree(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("tree_sha") treeSha: String,
        @Query("recursive") recursive: Int = 1
    ): GitHubTreeResponse

    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("sha") branch: String? = null,
        @Query("path") path: String? = null,
        @Query("per_page") perPage: Int = 100
    ): List<GitHubCommitResponse>

    @POST("repos/{owner}/{repo}/git/refs")
    suspend fun createBranchRef(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body body: GitHubCreateRefRequest
    ): GitHubRefResponse

    @DELETE("repos/{owner}/{repo}/git/refs/heads/{branch}")
    suspend fun deleteBranch(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String
    ): Unit

    @GET("repos/{owner}/{repo}/git/ref/heads/{branch}")
    suspend fun getBranchRef(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String
    ): GitHubRefResponse
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

@kotlinx.serialization.Serializable
data class GitHubBranchResponse(
    val name: String,
    val commit: GitHubBranchCommit,
    val protected_: Boolean = false
)

@kotlinx.serialization.Serializable
data class GitHubBranchCommit(
    val sha: String,
    val url: String
)

@kotlinx.serialization.Serializable
data class GitHubTreeResponse(
    val sha: String,
    val tree: List<GitHubTreeItem>,
    val truncated: Boolean
)

@kotlinx.serialization.Serializable
data class GitHubTreeItem(
    val path: String,
    val mode: String,
    val type: String,
    val sha: String,
    val size: Long? = null,
    val url: String
)

@kotlinx.serialization.Serializable
data class GitHubCreateRefRequest(
    val ref: String,
    val sha: String
)

@kotlinx.serialization.Serializable
data class GitHubRefResponse(
    val ref: String,
    val sha: String,
    val url: String
)

@kotlinx.serialization.Serializable
data class GitHubFileContentResponse(
    val name: String,
    val path: String,
    val sha: String,
    val size: Long?,
    val content: String,
    val encoding: String
)

@kotlinx.serialization.Serializable
data class GitHubCommitRequestBody(
    val message: String,
    val content: String,
    val sha: String? = null
)

@kotlinx.serialization.Serializable
data class GitHubCommitResponse(
    val sha: String,
    val html_url: String,
    val commit: GitHubCommitData,
    val url: String
)

@kotlinx.serialization.Serializable
data class GitHubCommitData(
    val sha: String,
    val message: String,
    val author: GitHubCommitAuthorData,
    val tree: GitHubCommitTreeData
)

@kotlinx.serialization.Serializable
data class GitHubCommitAuthorData(
    val name: String,
    val email: String,
    val date: String
)

@kotlinx.serialization.Serializable
data class GitHubCommitTreeData(
    val sha: String
)
