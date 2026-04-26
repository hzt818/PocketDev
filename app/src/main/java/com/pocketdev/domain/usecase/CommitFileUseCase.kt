package com.pocketdev.domain.usecase

import android.util.Base64
import com.pocketdev.domain.model.GitHubCommitRequest
import com.pocketdev.domain.model.GitHubCommitResponse
import com.pocketdev.domain.repository.GitHubRepository
import javax.inject.Inject

class CommitFileUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository
) {
    suspend operator fun invoke(
        owner: String,
        repo: String,
        path: String,
        content: String,
        message: String
    ): Result<GitHubCommitResponse> {
        val existingFile = gitHubRepository.getFileContent(owner, repo, path)
        val sha = existingFile.getOrNull()?.sha

        val commitRequest = GitHubCommitRequest(
            message = message,
            content = Base64.encodeToString(content.toByteArray(), Base64.NO_WRAP),
            sha = sha
        )

        return gitHubRepository.commitFile(owner, repo, path, commitRequest)
    }
}
