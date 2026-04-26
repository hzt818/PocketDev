package com.pocketdev.domain.repository

import com.pocketdev.domain.model.OllamaChatRequest
import com.pocketdev.domain.model.OllamaChatResponse
import com.pocketdev.domain.model.OllamaModel
import com.pocketdev.domain.model.OllamaModelWithStatus
import kotlinx.coroutines.flow.Flow

interface OllamaRepository {
    suspend fun listModels(): Result<List<OllamaModel>>
    suspend fun pullModel(modelName: String): Flow<PullProgress>
    suspend fun deleteModel(modelName: String): Result<Unit>
    suspend fun chatCompletion(request: OllamaChatRequest): Result<OllamaChatResponse>
    fun getOllamaBaseUrl(): String
    fun isServerRunning(): Boolean
    suspend fun startServer(): Result<Unit>
    suspend fun stopServer(): Result<Unit>
}

sealed class PullProgress {
    data class Progress(val status: String, val completed: Long, val total: Long) : PullProgress()
    data object Completed : PullProgress()
    data class Error(val message: String) : PullProgress()
}
