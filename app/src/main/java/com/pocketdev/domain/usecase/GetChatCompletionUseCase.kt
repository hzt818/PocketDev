package com.pocketdev.domain.usecase

import com.pocketdev.domain.model.ChatCompletionRequest
import com.pocketdev.domain.model.ChatCompletionResponse
import com.pocketdev.domain.repository.LlmRepository
import javax.inject.Inject

class GetChatCompletionUseCase @Inject constructor(
    private val llmRepository: LlmRepository
) {
    suspend operator fun invoke(request: ChatCompletionRequest): Result<ChatCompletionResponse> {
        return llmRepository.sendChatCompletion(request)
    }
}
