package com.pocketdev.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pocketdev.domain.model.AiProviderConfig
import com.pocketdev.domain.model.AiProviderType
import com.pocketdev.domain.model.DEFAULT_PROVIDERS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.aiProvidersDataStore: DataStore<Preferences> by preferencesDataStore(name = "ai_providers")

@Singleton
class UserSettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private object PreferencesKeys {
        val PROVIDERS = stringPreferencesKey("ai_providers")
        val ACTIVE_PROVIDER_TYPE = stringPreferencesKey("active_provider_type")
        val GITHUB_TOKEN = stringPreferencesKey("github_token")
    }

    val providersFlow: Flow<List<AiProviderConfig>> = context.aiProvidersDataStore.data.map { preferences ->
        val providersJson = preferences[PreferencesKeys.PROVIDERS]
        if (providersJson != null) {
            try {
                json.decodeFromString<List<AiProviderConfig>>(providersJson)
            } catch (e: Exception) {
                DEFAULT_PROVIDERS
            }
        } else {
            DEFAULT_PROVIDERS
        }
    }

    val activeProviderFlow: Flow<AiProviderConfig?> = context.aiProvidersDataStore.data.map { preferences ->
        val activeTypeStr = preferences[PreferencesKeys.ACTIVE_PROVIDER_TYPE]
        val providersJson = preferences[PreferencesKeys.PROVIDERS] ?: "[]"
        try {
            val providers: List<AiProviderConfig> = json.decodeFromString(providersJson)
            val activeType = activeTypeStr?.let { AiProviderType.valueOf(it) }
            providers.find { it.type == activeType } ?: providers.firstOrNull { it.isActive }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getProviders(): List<AiProviderConfig> {
        return providersFlow.first()
    }

    suspend fun getActiveProvider(): AiProviderConfig? {
        return activeProviderFlow.first()
    }

    suspend fun updateProvider(provider: AiProviderConfig) {
        context.aiProvidersDataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.PROVIDERS] ?: "[]"
            val current: MutableList<AiProviderConfig> = try {
                json.decodeFromString<List<AiProviderConfig>>(currentJson).toMutableList()
            } catch (e: Exception) {
                DEFAULT_PROVIDERS.toMutableList()
            }

            val index = current.indexOfFirst { it.type == provider.type }
            if (index != -1) {
                current[index] = provider
            } else {
                current.add(provider)
            }

            preferences[PreferencesKeys.PROVIDERS] = json.encodeToString(current)
        }
    }

    suspend fun setActiveProvider(type: AiProviderType) {
        context.aiProvidersDataStore.edit { preferences ->
            preferences[PreferencesKeys.ACTIVE_PROVIDER_TYPE] = type.name

            val currentJson = preferences[PreferencesKeys.PROVIDERS] ?: "[]"
            val current: List<AiProviderConfig> = try {
                json.decodeFromString(currentJson)
            } catch (e: Exception) {
                DEFAULT_PROVIDERS
            }

            val updated = current.map { it.copy(isActive = it.type == type) }
            preferences[PreferencesKeys.PROVIDERS] = json.encodeToString(updated)
        }
    }

    suspend fun getGitHubToken(): String? {
        return context.aiProvidersDataStore.data.first()[PreferencesKeys.GITHUB_TOKEN]
    }

    suspend fun setGitHubToken(token: String?) {
        context.aiProvidersDataStore.edit { preferences ->
            if (token != null) {
                preferences[PreferencesKeys.GITHUB_TOKEN] = token
            } else {
                preferences.remove(PreferencesKeys.GITHUB_TOKEN)
            }
        }
    }
}