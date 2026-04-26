package com.pocketdev.data.repository

import com.pocketdev.data.remote.api.LlmApi
import com.pocketdev.domain.model.ChatCompletionRequest
import com.pocketdev.domain.model.ChatCompletionResponse
import com.pocketdev.domain.repository.LlmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmRepositoryImpl @Inject constructor(
    private val llmApi: LlmApi
) : LlmRepository {

    override suspend fun sendChatCompletion(
        request: ChatCompletionRequest
    ): Result<ChatCompletionResponse> {
        return try {
            val response = llmApi.chatCompletion(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
