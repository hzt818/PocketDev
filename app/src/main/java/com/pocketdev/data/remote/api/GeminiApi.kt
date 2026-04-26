package com.pocketdev.data.remote.api

import com.pocketdev.domain.model.GeminiRequest
import com.pocketdev.domain.model.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiApi {
    @POST("models/{model}:generateContent")
    suspend fun generateContent(
        @retrofit2.http.Path("model") model: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}