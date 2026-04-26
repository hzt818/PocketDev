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
    suspend fun getActionMode(): AiActionMode
    suspend fun setActionMode(mode: AiActionMode)
    suspend fun getAppSettings(): AppSettings
    suspend fun updateAppSettings(settings: AppSettings)
    suspend fun getUserProfile(): UserProfile?
    suspend fun updateUserProfile(profile: UserProfile?)
    suspend fun clearUserSession()
}