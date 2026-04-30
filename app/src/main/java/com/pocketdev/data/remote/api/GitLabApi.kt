package com.pocketdev.data.remote.api

import retrofit2.http.*

interface GitLabApi {
    @GET("user")
    suspend fun getUser(): GitLabUserResponse

    @GET("projects")
    suspend fun getProjects(
        @Query("membership") membership: Boolean = true,
        @Query("per_page") perPage: Int = 100,
        @Query("order_by") orderBy: String = "updated_at"
    ): List<GitLabProjectResponse>

    @GET("projects/{id}")
    suspend fun getProject(@Path("id") projectId: String): GitLabProjectResponse

    @GET("projects/{id}/repository/branches")
    suspend fun getBranches(@Path("id") projectId: String): List<GitLabBranchResponse>

    @GET("projects/{id}/repository/tree")
    suspend fun getFileTree(
        @Path("id") projectId: String,
        @Query("ref") ref: String,
        @Query("path") path: String? = null,
        @Query("per_page") perPage: Int = 100
    ): List<GitLabTreeItemResponse>

    @GET("projects/{id}/repository/files/{path}")
    suspend fun getFileContent(
        @Path("id") projectId: String,
        @Path("path") path: String,
        @Query("ref") ref: String
    ): GitLabFileContentResponse

    @GET("projects/{id}/repository/commits")
    suspend fun getCommits(
        @Path("id") projectId: String,
        @Query("ref_name") refName: String,
        @Query("path") path: String? = null,
        @Query("per_page") perPage: Int = 100
    ): List<GitLabCommitResponse>

    @PUT("projects/{id}/repository/files/{path}")
    suspend fun updateFile(
        @Path("id") projectId: String,
        @Path("path") path: String,
        @Query("ref") ref: String,
        @Body body: GitLabFileRequest
    ): GitLabCommitResponse

    @POST("projects/{id}/repository/branches")
    suspend fun createBranch(
        @Path("id") projectId: String,
        @Body body: GitLabBranchRequest
    ): GitLabBranchResponse

    @DELETE("projects/{id}/repository/branches/{branch}")
    suspend fun deleteBranch(
        @Path("id") projectId: String,
        @Path("branch") branch: String
    ): Unit

    @GET("oauth/authorize")
    suspend fun getAuthorizationUrl(
        @Query("client_id") clientId: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("response_type") responseType: String = "code",
        @Query("scope") scope: String = "api write_repository"
    ): String
}

@kotlinx.serialization.Serializable
data class GitLabUserResponse(
    val id: Long,
    val username: String,
    val name: String,
    val avatar_url: String?
)

@kotlinx.serialization.Serializable
data class GitLabProjectResponse(
    val id: Long,
    val name: String,
    val path_with_namespace: String,
    val description: String?,
    val web_url: String,
    val default_branch: String
)

@kotlinx.serialization.Serializable
data class GitLabBranchResponse(
    val name: String,
    val commit: GitLabCommitInfoResponse,
    val protected_: Boolean = false
) {
    companion object {
        private const val PROTECTED_FIELD = "protected"
    }
}

@kotlinx.serialization.Serializable
data class GitLabCommitInfoResponse(
    val id: String,
    val short_id: String,
    val title: String,
    val author_name: String,
    val author_email: String,
    val created_at: String
)

@kotlinx.serialization.Serializable
data class GitLabTreeItemResponse(
    val id: String,
    val name: String,
    val type: String,
    val path: String,
    val mode: String
)

@kotlinx.serialization.Serializable
data class GitLabFileContentResponse(
    val file_name: String,
    val file_path: String,
    val size: Long,
    val encoding: String,
    val content: String,
    val content_sha256: String,
    val ref: String,
    val blob_id: String
)

@kotlinx.serialization.Serializable
data class GitLabCommitResponse(
    val id: String,
    val short_id: String,
    val title: String,
    val author_name: String,
    val author_email: String,
    val created_at: String,
    val web_url: String,
    val parent_ids: List<String> = emptyList()
)

@kotlinx.serialization.Serializable
data class GitLabFileRequest(
    val branch: String,
    val content: String,
    val commit_message: String
)

@kotlinx.serialization.Serializable
data class GitLabBranchRequest(
    val branch: String,
    val ref: String
)
