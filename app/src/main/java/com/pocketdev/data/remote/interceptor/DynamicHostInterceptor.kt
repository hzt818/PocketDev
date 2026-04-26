package com.pocketdev.data.remote.interceptor

import com.pocketdev.data.local.UserSettingsDataStore
import com.pocketdev.domain.model.AiProviderConfig
import com.pocketdev.domain.model.AiProviderType
import com.pocketdev.domain.model.ApiFormat
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicHostInterceptor @Inject constructor(
    private val userSettingsDataStore: UserSettingsDataStore
) : Interceptor {
    private var cachedProvider: AiProviderConfig? = null

    init {
        runBlocking {
            cachedProvider = userSettingsDataStore.getActiveProvider()
        }
    }

    fun updateProvider(provider: AiProviderConfig) {
        cachedProvider = provider
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val provider = cachedProvider ?: runBlocking {
            userSettingsDataStore.getActiveProvider().also { cachedProvider = it }
        }

        if (provider == null) {
            return chain.proceed(originalRequest)
        }

        return when (provider.type) {
            AiProviderType.DEEPSEEK,
            AiProviderType.OPENAI,
            AiProviderType.MINIMAX,
            AiProviderType.KIMI,
            AiProviderType.GLM -> interceptOpenAiStyle(chain, originalRequest, provider)

            AiProviderType.ANTHROPIC -> interceptAnthropic(chain, originalRequest, provider)

            AiProviderType.GEMINI -> interceptGemini(chain, originalRequest, provider)

            AiProviderType.OLLAMA -> interceptOllama(chain, originalRequest, provider)

            AiProviderType.PC_CLI -> {
                chain.proceed(originalRequest)
            }
        }
    }

    private fun interceptOpenAiStyle(
        chain: Interceptor.Chain,
        originalRequest: okhttp3.Request,
        provider: AiProviderConfig
    ): Response {
        if (provider.apiKey.isBlank()) {
            return chain.proceed(originalRequest)
        }

        val originalUrl = originalRequest.url
        val parsedUrl = try {
            java.net.URL(provider.baseUrl)
        } catch (e: Exception) {
            return chain.proceed(originalRequest)
        }

        val newUrl = originalUrl.newBuilder()
            .scheme(parsedUrl.protocol)
            .host(parsedUrl.host)
            .port(parsedUrl.port.takeIf { it != -1 } ?: if (parsedUrl.protocol == "https") 443 else 80)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .header("Authorization", "Bearer ${provider.apiKey}")
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(newRequest)
    }

    private fun interceptAnthropic(
        chain: Interceptor.Chain,
        originalRequest: okhttp3.Request,
        provider: AiProviderConfig
    ): Response {
        if (provider.apiKey.isBlank()) {
            return chain.proceed(originalRequest)
        }

        val originalUrl = originalRequest.url
        val parsedUrl = try {
            java.net.URL(provider.baseUrl)
        } catch (e: Exception) {
            return chain.proceed(originalRequest)
        }

        val newUrl = originalUrl.newBuilder()
            .scheme(parsedUrl.protocol)
            .host(parsedUrl.host)
            .port(parsedUrl.port.takeIf { it != -1 } ?: 443)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .header("x-api-key", provider.apiKey)
            .header("anthropic-version", "2023-06-01")
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(newRequest)
    }

    private fun interceptGemini(
        chain: Interceptor.Chain,
        originalRequest: okhttp3.Request,
        provider: AiProviderConfig
    ): Response {
        if (provider.apiKey.isBlank()) {
            return chain.proceed(originalRequest)
        }

        val originalUrl = originalRequest.url
        val parsedUrl = try {
            java.net.URL(provider.baseUrl)
        } catch (e: Exception) {
            return chain.proceed(originalRequest)
        }

        val newUrl = originalUrl.newBuilder()
            .scheme(parsedUrl.protocol)
            .host(parsedUrl.host)
            .port(parsedUrl.port.takeIf { it != -1 } ?: 443)
            .addQueryParameter("key", provider.apiKey)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(newRequest)
    }

    private fun interceptOllama(
        chain: Interceptor.Chain,
        originalRequest: okhttp3.Request,
        provider: AiProviderConfig
    ): Response {
        val originalUrl = originalRequest.url
        val parsedUrl = try {
            java.net.URL(provider.baseUrl)
        } catch (e: Exception) {
            return chain.proceed(originalRequest)
        }

        val newUrl = originalUrl.newBuilder()
            .scheme(parsedUrl.protocol)
            .host(parsedUrl.host)
            .port(parsedUrl.port.takeIf { it != -1 } ?: 11434)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(newRequest)
    }
}