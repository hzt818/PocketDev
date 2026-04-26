package com.pocketdev.domain.usecase

import com.pocketdev.domain.model.GitHubRepo
import com.pocketdev.domain.repository.GitHubRepository
import javax.inject.Inject

class GetReposUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository
) {
    suspend operator fun invoke(): Result<List<GitHubRepo>> {
        return gitHubRepository.getRepos()
    }
}
