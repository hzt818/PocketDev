package com.pocketdev.domain.model

enum class RepositoryProvider {
    GITHUB,
    GITLAB
}

data class RemoteRepository(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val htmlUrl: String,
    val defaultBranch: String,
    val provider: RepositoryProvider,
    val owner: String
)

data class RemoteBranch(
    val name: String,
    val sha: String,
    val isDefault: Boolean,
    val isProtected: Boolean
)

data class RemoteFile(
    val name: String,
    val path: String,
    val sha: String,
    val size: Long,
    val type: RemoteFileType,
    val downloadUrl: String?
)

enum class RemoteFileType {
    FILE,
    DIRECTORY,
    SYMLINK,
    SUBMODULE
}

data class RemoteCommit(
    val sha: String,
    val message: String,
    val author: RemoteCommitAuthor,
    val date: String,
    val htmlUrl: String
)

data class RemoteCommitAuthor(
    val name: String,
    val email: String,
    val avatarUrl: String?
)

data class RemoteCommitRequest(
    val message: String,
    val content: String,
    val sha: String? = null,
    val branch: String? = null
)

data class RemoteFileContent(
    val name: String,
    val path: String,
    val sha: String,
    val content: String,
    val encoding: String,
    val size: Long,
    val type: RemoteFileType
)

sealed class RemoteRepositoryResult {
    data class Success<T>(val data: T) : RemoteRepositoryResult()
    data class Error(val message: String, val code: Int? = null) : RemoteRepositoryResult()
    data object NotAuthenticated : RemoteRepositoryResult()
    data object RateLimited : RemoteRepositoryResult()
}
