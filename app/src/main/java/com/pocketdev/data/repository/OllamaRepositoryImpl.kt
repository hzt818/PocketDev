package com.pocketdev.data.repository

import com.pocketdev.data.local.UserSettingsDataStore
import com.pocketdev.data.remote.api.OllamaApi
import com.pocketdev.domain.model.OllamaChatRequest
import com.pocketdev.domain.model.OllamaDeleteRequest
import com.pocketdev.domain.model.OllamaChatResponse
import com.pocketdev.domain.model.OllamaModel
import com.pocketdev.domain.repository.OllamaRepository
import com.pocketdev.domain.repository.PullProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OllamaRepositoryImpl @Inject constructor(
    private val userSettingsDataStore: UserSettingsDataStore
) : OllamaRepository {

    private var ollamaApi: OllamaApi? = null
    private var currentBaseUrl: String? = null

    override fun getOllamaBaseUrl(): String {
        return "http://localhost:11434"
    }

    override fun isServerRunning(): Boolean {
        return try {
            val client = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("${getOllamaBaseUrl()}/api/tags")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                response.code == 200
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun startServer(): Result<Unit> = withContext(Dispatchers.IO) {
        Result.success(Unit)
    }

    override suspend fun stopServer(): Result<Unit> = withContext(Dispatchers.IO) {
        Result.success(Unit)
    }

    private suspend fun getApi(): OllamaApi {
        val baseUrl = getOllamaBaseUrl()
        if (ollamaApi == null || currentBaseUrl != baseUrl) {
            currentBaseUrl = baseUrl
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("$baseUrl/")
                .client(client)
                .addConverterFactory(
                    kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        isLenient = true
                    }.asConverterFactory(
                        "application/json".toMediaType()
                    )
                )
                .build()

            ollamaApi = retrofit.create(OllamaApi::class.java)
        }
        return ollamaApi!!
    }

    override suspend fun listModels(): Result<List<OllamaModel>> = withContext(Dispatchers.IO) {
        try {
            val response = getApi().listModels()
            Result.success(response.models)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pullModel(modelName: String): Flow<PullProgress> = flow {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build()

            val requestBody = okhttp3.RequestBody.Companion.toRequestBody(
                "application/json".toMediaTypeOrNull(),
                """{"name":"$modelName","stream":true}"""
            )

            val request = Request.Builder()
                .url("${getOllamaBaseUrl()}/api/pull")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    emit(PullProgress.Error("Failed to pull model: ${response.code}"))
                    return@flow
                }

                response.body?.let { body ->
                    val source = body.source()
                    val buffer = okio.Buffer()

                    while (source.read(buffer, 8192) != -1L) {
                        val line = buffer.readUtf8Line()
                        line?.let {
                            try {
                                val progress = kotlinx.serialization.json.Json.decodeFromString<PullProgressJson>(
                                    it
                                )
                                emit(
                                    PullProgress.Progress(
                                        status = progress.status ?: "Downloading",
                                        completed = progress.completed ?: 0L,
                                        total = progress.total ?: 0L
                                    )
                                )
                            } catch (_: Exception) {
                            }
                        }
                    }
                }
                emit(PullProgress.Completed)
            }
        } catch (e: Exception) {
            emit(PullProgress.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteModel(modelName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            getApi().deleteModel(OllamaDeleteRequest(modelName))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun chatCompletion(request: OllamaChatRequest): Result<OllamaChatResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = getApi().chatCompletion(request)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

@kotlinx.serialization.Serializable
private data class PullProgressJson(
    val status: String? = null,
    val digest: String? = null,
    val total: Long? = null,
    val completed: Long? = null
)

private fun okhttp3.RequestBody.Companion.toRequestBody(
    contentType: okhttp3.MediaType?,
    content: String
): okhttp3.RequestBody {
    return object : okhttp3.RequestBody() {
        override fun contentType() = contentType
        override fun contentLength() = content.length.toLong()
        override fun writeTo(sink: okio.BufferedSink) {
            sink.writeUtf8(content)
        }
    }
}

private fun String.toMediaTypeOrNull() = try {
    this.toMediaType()
} catch (_: Exception) {
    null
}
