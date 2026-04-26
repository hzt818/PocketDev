package com.pocketdev.domain.repository

import com.pocketdev.domain.model.ChatCompletionRequest
import com.pocketdev.domain.model.ChatCompletionResponse

interface LlmRepository {
    suspend fun sendChatCompletion(request: ChatCompletionRequest): Result<ChatCompletionResponse>
}
