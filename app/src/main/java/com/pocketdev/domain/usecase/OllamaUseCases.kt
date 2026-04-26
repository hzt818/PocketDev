package com.pocketdev.domain.usecase

import com.pocketdev.domain.model.OllamaChatRequest
import com.pocketdev.domain.model.OllamaChatResponse
import com.pocketdev.domain.model.OllamaModel
import com.pocketdev.domain.repository.OllamaRepository
import com.pocketdev.domain.repository.PullProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOllamaModelsUseCase @Inject constructor(
    private val repository: OllamaRepository
) {
    suspend operator fun invoke(): Result<List<OllamaModel>> {
        return repository.listModels()
    }
}

class PullOllamaModelUseCase @Inject constructor(
    private val repository: OllamaRepository
) {
    suspend operator fun invoke(modelName: String): Flow<PullProgress> {
        return repository.pullModel(modelName)
    }
}

class DeleteOllamaModelUseCase @Inject constructor(
    private val repository: OllamaRepository
) {
    suspend operator fun invoke(modelName: String): Result<Unit> {
        return repository.deleteModel(modelName)
    }
}

class OllamaChatUseCase @Inject constructor(
    private val repository: OllamaRepository
) {
    suspend operator fun invoke(request: OllamaChatRequest): Result<OllamaChatResponse> {
        return repository.chatCompletion(request)
    }
}
