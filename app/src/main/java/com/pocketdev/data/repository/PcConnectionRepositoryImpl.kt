package com.pocketdev.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pocketdev.data.remote.api.PcCliApi
import com.pocketdev.domain.model.PcConnectionConfig
import com.pocketdev.domain.model.PcFileReadRequest
import com.pocketdev.domain.model.PcFileWriteRequest
import com.pocketdev.domain.model.PcGitCommitRequest
import com.pocketdev.domain.model.PcShellRequest
import com.pocketdev.domain.repository.PcConnectionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private val Context.pcConnectionsDataStore: DataStore<Preferences> by preferencesDataStore(name = "pc_connections")

@Singleton
class PcConnectionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PcConnectionRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private object PreferencesKeys {
        val CONNECTIONS = stringPreferencesKey("connections")
        val ACTIVE_CONNECTION_ID = stringPreferencesKey("active_connection_id")
    }

    private var cachedApi: PcCliApi? = null
    private var cachedConfig: PcConnectionConfig? = null

    override val connectionsFlow: Flow<List<PcConnectionConfig>> = context.pcConnectionsDataStore.data.map { preferences ->
        val connectionsJson = preferences[PreferencesKeys.CONNECTIONS] ?: "[]"
        try {
            json.decodeFromString<List<PcConnectionConfig>>(connectionsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override val activeConnectionFlow: Flow<PcConnectionConfig?> = context.pcConnectionsDataStore.data.map { preferences ->
        val activeId = preferences[PreferencesKeys.ACTIVE_CONNECTION_ID]
        if (activeId != null) {
            val connectionsJson = preferences[PreferencesKeys.CONNECTIONS] ?: "[]"
            try {
                val connections: List<PcConnectionConfig> = json.decodeFromString(connectionsJson)
                connections.find { it.id == activeId }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    private suspend fun getApi(config: PcConnectionConfig): PcCliApi {
        if (cachedApi == null || cachedConfig?.id != config.id) {
            cachedConfig = config
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer ${config.apiKey}")
                        .header("Content-Type", "application/json")
                        .build()
                    chain.proceed(newRequest)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://${config.host}:${config.port}/")
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()

            cachedApi = retrofit.create(PcCliApi::class.java)
        }
        return cachedApi!!
    }

    override suspend fun getConnections(): List<PcConnectionConfig> {
        return connectionsFlow.first()
    }

    override suspend fun addConnection(config: PcConnectionConfig): Result<Unit> {
        return try {
            context.pcConnectionsDataStore.edit { preferences ->
                val currentJson = preferences[PreferencesKeys.CONNECTIONS] ?: "[]"
                val current: MutableList<PcConnectionConfig> = try {
                    json.decodeFromString<List<PcConnectionConfig>>(currentJson).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }

                val newConfig = config.copy(id = config.id.ifBlank { UUID.randomUUID().toString() })
                current.add(newConfig)

                preferences[PreferencesKeys.CONNECTIONS] = json.encodeToString(current)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeConnection(id: String): Result<Unit> {
        return try {
            context.pcConnectionsDataStore.edit { preferences ->
                val currentJson = preferences[PreferencesKeys.CONNECTIONS] ?: "[]"
                val current: List<PcConnectionConfig> = try {
                    json.decodeFromString(currentJson)
                } catch (e: Exception) {
                    emptyList()
                }

                val updated = current.filter { it.id != id }
                preferences[PreferencesKeys.CONNECTIONS] = json.encodeToString(updated)

                if (preferences[PreferencesKeys.ACTIVE_CONNECTION_ID] == id) {
                    preferences.remove(PreferencesKeys.ACTIVE_CONNECTION_ID)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setActiveConnection(id: String): Result<Unit> {
        return try {
            context.pcConnectionsDataStore.edit { preferences ->
                preferences[PreferencesKeys.ACTIVE_CONNECTION_ID] = id
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun testConnection(id: String): Result<Boolean> {
        return try {
            val connections = getConnections()
            val config = connections.find { it.id == id }
                ?: return Result.failure(Exception("Connection not found"))

            val api = getApi(config)
            api.getSystemInfo()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun readFile(request: PcFileReadRequest): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val config = activeConnectionFlow.first()
                    ?: return@withContext Result.failure(Exception("No active connection"))

                val api = getApi(config)
                val response = api.readFile(request)

                if (response.success && response.content != null) {
                    Result.success(response.content)
                } else {
                    Result.failure(Exception(response.error ?: "Failed to read file"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun writeFile(request: PcFileWriteRequest): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val config = activeConnectionFlow.first()
                    ?: return@withContext Result.failure(Exception("No active connection"))

                val api = getApi(config)
                val response = api.writeFile(request)

                if (response.success) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.error ?: "Failed to write file"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun gitCommit(request: PcGitCommitRequest): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val config = activeConnectionFlow.first()
                    ?: return@withContext Result.failure(Exception("No active connection"))

                val api = getApi(config)
                val response = api.gitCommit(request)

                if (response.success && response.sha != null) {
                    Result.success(response.sha)
                } else {
                    Result.failure(Exception(response.error ?: "Failed to commit"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun executeShell(request: PcShellRequest): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val config = activeConnectionFlow.first()
                    ?: return@withContext Result.failure(Exception("No active connection"))

                val api = getApi(config)
                val response = api.executeShell(request)

                if (response.success && response.stdout != null) {
                    Result.success(response.stdout)
                } else {
                    Result.failure(Exception(response.error ?: response.stderr ?: "Command failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
