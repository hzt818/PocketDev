package com.pocketdev.data.remote.api

import com.pocketdev.domain.model.AnthropicRequest
import com.pocketdev.domain.model.AnthropicResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AnthropicApi {
    @POST("messages")
    suspend fun chatCompletion(@Body request: AnthropicRequest): AnthropicResponse
}