package com.pocketdev.data.remote.api

import com.pocketdev.domain.model.OllamaChatRequest
import com.pocketdev.domain.model.OllamaChatResponse
import com.pocketdev.domain.model.OllamaDeleteRequest
import com.pocketdev.domain.model.OllamaListResponse
import com.pocketdev.domain.model.OllamaModelInfo
import com.pocketdev.domain.model.OllamaPullRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OllamaApi {
    @GET("api/tags")
    suspend fun listModels(): OllamaListResponse

    @POST("api/show")
    suspend fun showModel(@Body request: Map<String, String>): OllamaModelInfo

    @POST("api/pull")
    @Streaming
    suspend fun pullModel(@Body request: OllamaPullRequest): ResponseBody

    @POST("api/delete")
    suspend fun deleteModel(@Body request: OllamaDeleteRequest)

    @POST("v1/chat/completions")
    suspend fun chatCompletion(@Body request: OllamaChatRequest): OllamaChatResponse
}
