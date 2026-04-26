package com.pocketdev.domain.usecase

import com.pocketdev.domain.repository.GitHubRepository
import javax.inject.Inject

class AuthenticateGitHubUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository
) {
    fun getAuthorizationUrl(): String = gitHubRepository.getAuthorizationUrl()

    suspend fun handleCallback(code: String): Result<String> {
        return gitHubRepository.handleCallback(code)
    }

    suspend fun isAuthenticated(): Boolean {
        return gitHubRepository.isAuthenticated()
    }
}
