package com.pocketdev.domain.repository

import com.pocketdev.domain.model.*

interface AiRepository {
    suspend fun sendChat(
        provider: AiProviderType,
        model: String,
        messages: List<Message>,
        baseUrl: String,
        apiKey: String,
        apiFormat: ApiFormat
    ): Result<UnifiedChatResponse>
}

interface UserSettingsRepository {
    suspend fun getConfig(): LlmConfig
    suspend fun updateConfig(config: LlmConfig)
    suspend fun getGitHubToken(): String?
    suspend fun setGitHubToken(token: String?)
    suspend fun getActiveProvider(): AiProviderConfig?
    suspend fun getProviders(): List<AiProviderConfig>
    suspend fun updateProvider(provider: AiProviderConfig)
    suspend fun setActiveProvider(type: AiProviderType)
}