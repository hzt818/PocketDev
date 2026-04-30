package com.pocketdev.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pocketdev.domain.model.AiActionMode
import com.pocketdev.domain.model.AiProviderConfig
import com.pocketdev.domain.model.AiProviderType
import com.pocketdev.domain.model.AppSettings
import com.pocketdev.domain.model.DEFAULT_PROVIDERS
import com.pocketdev.domain.model.EditorPreferences
import com.pocketdev.domain.model.ThemeMode
import com.pocketdev.domain.model.UserProfile
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
        val GITLAB_TOKEN = stringPreferencesKey("gitlab_token")
        val GITHUB_CLIENT_SECRET = stringPreferencesKey("github_client_secret")
        val ACTION_MODE = stringPreferencesKey("action_mode")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = stringPreferencesKey("dynamic_color")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val TAB_SIZE = stringPreferencesKey("tab_size")
        val SHOW_LINE_NUMBERS = stringPreferencesKey("show_line_numbers")
        val WORD_WRAP = stringPreferencesKey("word_wrap")
        val USER_PROFILE = stringPreferencesKey("user_profile")
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

    suspend fun getGitLabToken(): String? {
        return context.aiProvidersDataStore.data.first()[PreferencesKeys.GITLAB_TOKEN]
    }

    suspend fun getGitHubClientSecret(): String? {
        return context.aiProvidersDataStore.data.first()[PreferencesKeys.GITHUB_CLIENT_SECRET]
    }

    suspend fun setGitHubClientSecret(secret: String?) {
        context.aiProvidersDataStore.edit { preferences ->
            if (secret != null) {
                preferences[PreferencesKeys.GITHUB_CLIENT_SECRET] = secret
            } else {
                preferences.remove(PreferencesKeys.GITHUB_CLIENT_SECRET)
            }
        }
    }

    suspend fun setGitLabToken(token: String?) {
        context.aiProvidersDataStore.edit { preferences ->
            if (token != null) {
                preferences[PreferencesKeys.GITLAB_TOKEN] = token
            } else {
                preferences.remove(PreferencesKeys.GITLAB_TOKEN)
            }
        }
    }

    val actionModeFlow: Flow<AiActionMode> = context.aiProvidersDataStore.data.map { preferences ->
        val modeStr = preferences[PreferencesKeys.ACTION_MODE]
        try {
            modeStr?.let { AiActionMode.valueOf(it) } ?: AiActionMode.PLAN
        } catch (e: Exception) {
            AiActionMode.PLAN
        }
    }

    suspend fun getActionMode(): AiActionMode {
        return actionModeFlow.first()
    }

    suspend fun setActionMode(mode: AiActionMode) {
        context.aiProvidersDataStore.edit { preferences ->
            preferences[PreferencesKeys.ACTION_MODE] = mode.name
        }
    }

    // App Settings
    val appSettingsFlow: Flow<AppSettings> = context.aiProvidersDataStore.data.map { preferences ->
        val themeModeStr = preferences[PreferencesKeys.THEME_MODE]
        val dynamicColorStr = preferences[PreferencesKeys.DYNAMIC_COLOR]
        val fontSizeStr = preferences[PreferencesKeys.FONT_SIZE]
        val tabSizeStr = preferences[PreferencesKeys.TAB_SIZE]
        val showLineNumbersStr = preferences[PreferencesKeys.SHOW_LINE_NUMBERS]
        val wordWrapStr = preferences[PreferencesKeys.WORD_WRAP]

        val themeMode = try {
            themeModeStr?.let { ThemeMode.valueOf(it) } ?: ThemeMode.SYSTEM
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }

        val dynamicColor = dynamicColorStr?.toBoolean() ?: true
        val fontSize = fontSizeStr?.toIntOrNull() ?: 14
        val tabSize = tabSizeStr?.toIntOrNull() ?: 4
        val showLineNumbers = showLineNumbersStr?.toBoolean() ?: true
        val wordWrap = wordWrapStr?.toBoolean() ?: false

        AppSettings(
            themeMode = themeMode,
            dynamicColor = dynamicColor,
            editorPreferences = EditorPreferences(
                fontSize = fontSize,
                tabSize = tabSize,
                showLineNumbers = showLineNumbers,
                wordWrap = wordWrap
            )
        )
    }

    suspend fun getAppSettings(): AppSettings {
        return appSettingsFlow.first()
    }

    suspend fun updateAppSettings(settings: AppSettings) {
        context.aiProvidersDataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = settings.themeMode.name
            preferences[PreferencesKeys.DYNAMIC_COLOR] = settings.dynamicColor.toString()
            preferences[PreferencesKeys.FONT_SIZE] = settings.editorPreferences.fontSize.toString()
            preferences[PreferencesKeys.TAB_SIZE] = settings.editorPreferences.tabSize.toString()
            preferences[PreferencesKeys.SHOW_LINE_NUMBERS] = settings.editorPreferences.showLineNumbers.toString()
            preferences[PreferencesKeys.WORD_WRAP] = settings.editorPreferences.wordWrap.toString()
        }
    }

    // User Profile
    val userProfileFlow: Flow<UserProfile?> = context.aiProvidersDataStore.data.map { preferences ->
        val profileJson = preferences[PreferencesKeys.USER_PROFILE]
        if (profileJson != null) {
            try {
                json.decodeFromString<UserProfile>(profileJson)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    suspend fun getUserProfile(): UserProfile? {
        return userProfileFlow.first()
    }

    suspend fun updateUserProfile(profile: UserProfile?) {
        context.aiProvidersDataStore.edit { preferences ->
            if (profile != null) {
                preferences[PreferencesKeys.USER_PROFILE] = json.encodeToString(profile)
            } else {
                preferences.remove(PreferencesKeys.USER_PROFILE)
            }
        }
    }

    suspend fun clearUserSession() {
        context.aiProvidersDataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_PROFILE)
            preferences.remove(PreferencesKeys.GITHUB_TOKEN)
            preferences.remove(PreferencesKeys.GITLAB_TOKEN)
        }
    }
}