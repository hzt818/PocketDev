package com.pocketdev.data.remote.api

import com.pocketdev.domain.model.ChatCompletionRequest
import com.pocketdev.domain.model.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LlmApi {
    @POST("v1/chat/completions")
    suspend fun chatCompletion(@Body request: ChatCompletionRequest): ChatCompletionResponse
}
