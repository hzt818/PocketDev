package com.pocketdev.data.repository

import com.pocketdev.data.remote.api.AnthropicApi
import com.pocketdev.data.remote.api.GeminiApi
import com.pocketdev.data.remote.api.LlmApi
import com.pocketdev.data.remote.api.OllamaApi
import com.pocketdev.domain.model.*
import com.pocketdev.domain.repository.AiRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val llmApi: LlmApi,
    private val anthropicApi: AnthropicApi,
    private val geminiApi: GeminiApi,
    private val ollamaApi: OllamaApi
) : AiRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    override suspend fun sendChat(
        provider: AiProviderType,
        model: String,
        messages: List<Message>,
        baseUrl: String,
        apiKey: String,
        apiFormat: ApiFormat
    ): Result<UnifiedChatResponse> {
        return try {
            when (apiFormat) {
                ApiFormat.OPENAI兼容 -> sendOpenAiStyle(provider, model, messages, baseUrl, apiKey)
                ApiFormat.ANTHROPIC格式 -> sendAnthropic(provider, model, messages, baseUrl, apiKey)
                ApiFormat.GEMINI格式 -> sendGemini(provider, model, messages, baseUrl, apiKey)
                ApiFormat.OLLAMA格式 -> sendOllama(provider, model, messages, baseUrl, apiKey)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun sendOpenAiStyle(
        provider: AiProviderType,
        model: String,
        messages: List<Message>,
        baseUrl: String,
        apiKey: String
    ): Result<UnifiedChatResponse> {
        val api = createLlmApi(baseUrl, apiKey)
        val request = ChatCompletionRequest(model = model, messages = messages)
        val response = api.chatCompletion(request)
        val content = response.choices.firstOrNull()?.message?.content ?: ""
        return Result.success(
            UnifiedChatResponse(
                content = content,
                provider = provider,
                model = model,
                usage = response.usage?.let {
                    Usage(
                        promptTokens = it.promptTokens,
                        completionTokens = it.completionTokens,
                        totalTokens = it.totalTokens
                    )
                }
            )
        )
    }

    private suspend fun sendAnthropic(
        provider: AiProviderType,
        model: String,
        messages: List<Message>,
        baseUrl: String,
        apiKey: String
    ): Result<UnifiedChatResponse> {
        val api = createAnthropicApi(baseUrl, apiKey)
        val request = AnthropicRequest(
            model = model,
            messages = messages.toAnthropicFormat(),
            maxTokens = 4096
        )
        val response = api.chatCompletion(request)
        val content = response.toUnifiedContent()
        return Result.success(
            UnifiedChatResponse(
                content = content,
                provider = provider,
                model = model,
                usage = response.usage?.let {
                    Usage(
                        promptTokens = it.inputTokens,
                        completionTokens = it.outputTokens,
                        totalTokens = it.inputTokens + it.outputTokens
                    )
                }
            )
        )
    }

    private suspend fun sendGemini(
        provider: AiProviderType,
        model: String,
        messages: List<Message>,
        baseUrl: String,
        apiKey: String
    ): Result<UnifiedChatResponse> {
        val api = createGeminiApi(baseUrl, apiKey)
        val request = GeminiRequest(contents = messages.toGeminiFormat())
        val response = api.generateContent(model, request)
        val content = response.toUnifiedContent()
        return Result.success(
            UnifiedChatResponse(
                content = content,
                provider = provider,
                model = model,
                usage = response.toUnifiedUsage()
            )
        )
    }

    private suspend fun sendOllama(
        provider: AiProviderType,
        model: String,
        messages: List<Message>,
        baseUrl: String,
        apiKey: String
    ): Result<UnifiedChatResponse> {
        val request = OllamaChatRequest(
            model = model,
            messages = messages.map { OllamaMessage(role = it.role, content = it.content) }
        )
        val response = ollamaApi.chatCompletion(request)
        return Result.success(
            UnifiedChatResponse(
                content = response.message.content,
                provider = provider,
                model = model,
                usage = null
            )
        )
    }

    private fun createLlmApi(baseUrl: String, apiKey: String): LlmApi {
        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl.ensureTrailingSlash())
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(LlmApi::class.java)
    }

    private fun createAnthropicApi(baseUrl: String, apiKey: String): AnthropicApi {
        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl.ensureTrailingSlash())
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(AnthropicApi::class.java)
    }

    private fun createGeminiApi(baseUrl: String, apiKey: String): GeminiApi {
        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalUrl = chain.request().url
                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("key", apiKey)
                    .build()
                val request = chain.request().newBuilder()
                    .url(newUrl)
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl.ensureTrailingSlash())
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(GeminiApi::class.java)
    }

    private fun String.ensureTrailingSlash(): String =
        if (endsWith("/")) this else "$this/"
}