package com.pocketdev.data.repository

import com.pocketdev.data.local.UserSettingsDataStore
import com.pocketdev.domain.model.AiProviderConfig
import com.pocketdev.domain.model.AiProviderType
import com.pocketdev.domain.model.LlmConfig
import com.pocketdev.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    private val userSettingsDataStore: UserSettingsDataStore
) : UserSettingsRepository {

    override suspend fun getConfig(): LlmConfig {
        val provider = userSettingsDataStore.getActiveProvider()
        return if (provider != null) {
            LlmConfig(
                baseUrl = provider.baseUrl,
                apiKey = provider.apiKey,
                modelName = provider.modelName
            )
        } else {
            LlmConfig(
                baseUrl = "https://api.deepseek.com",
                apiKey = "",
                modelName = "deepseek-chat"
            )
        }
    }

    override suspend fun updateConfig(config: LlmConfig) {
        val currentProviders = userSettingsDataStore.getProviders()
        val activeProvider = currentProviders.find { it.isActive } ?: currentProviders.firstOrNull()
        if (activeProvider != null) {
            userSettingsDataStore.updateProvider(
                activeProvider.copy(
                    baseUrl = config.baseUrl,
                    apiKey = config.apiKey,
                    modelName = config.modelName
                )
            )
        }
    }

    override suspend fun getGitHubToken(): String? {
        return userSettingsDataStore.getGitHubToken()
    }

    override suspend fun setGitHubToken(token: String?) {
        userSettingsDataStore.setGitHubToken(token)
    }

    override suspend fun getActiveProvider(): AiProviderConfig? {
        return userSettingsDataStore.getActiveProvider()
    }

    override suspend fun getProviders(): List<AiProviderConfig> {
        return userSettingsDataStore.getProviders()
    }

    override suspend fun updateProvider(provider: AiProviderConfig) {
        userSettingsDataStore.updateProvider(provider)
    }

    override suspend fun setActiveProvider(type: AiProviderType) {
        userSettingsDataStore.setActiveProvider(type)
    }
}