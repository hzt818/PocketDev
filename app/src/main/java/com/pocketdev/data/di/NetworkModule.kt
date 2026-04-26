package com.pocketdev.data.di

import com.pocketdev.data.local.UserSettingsDataStore
import com.pocketdev.data.remote.api.AnthropicApi
import com.pocketdev.data.remote.api.GeminiApi
import com.pocketdev.data.remote.api.GitHubApi
import com.pocketdev.data.remote.api.GitLabApi
import com.pocketdev.data.remote.api.LlmApi
import com.pocketdev.data.remote.api.OllamaApi
import com.pocketdev.data.remote.interceptor.DynamicHostInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        dynamicHostInterceptor: DynamicHostInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(dynamicHostInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideLlmApi(okHttpClient: OkHttpClient): LlmApi {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.deepseek.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(LlmApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAnthropicApi(): AnthropicApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(AnthropicApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiApi(): GeminiApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(GeminiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOllamaApi(): OllamaApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:11434/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(OllamaApi::class.java)
    }

    @Provides
    @Singleton
    @Named("github_token_provider")
    fun provideGitHubTokenProvider(userSettingsDataStore: UserSettingsDataStore): () -> String? {
        return { runBlocking { userSettingsDataStore.getGitHubToken() } }
    }

    @Provides
    @Singleton
    fun provideGitHubApi(
        userSettingsDataStore: UserSettingsDataStore,
        @Named("github_token_provider") tokenProvider: () -> String?
    ): GitHubApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val originalRequest = chain.request()
            val token = tokenProvider()

            val newRequest = if (token != null) {
                originalRequest.newBuilder()
                    .header("Authorization", "token $token")
                    .header("Accept", "application/vnd.github.v3+json")
                    .build()
            } else {
                originalRequest.newBuilder()
                    .header("Accept", "application/vnd.github.v3+json")
                    .build()
            }
            chain.proceed(newRequest)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(GitHubApi::class.java)
    }

    @Provides
    @Singleton
    @Named("gitlab_token_provider")
    fun provideGitLabTokenProvider(userSettingsDataStore: UserSettingsDataStore): () -> String? {
        return { runBlocking { userSettingsDataStore.getGitLabToken() } }
    }

    @Provides
    @Singleton
    fun provideGitLabApi(
        userSettingsDataStore: UserSettingsDataStore,
        @Named("gitlab_token_provider") tokenProvider: () -> String?
    ): GitLabApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val originalRequest = chain.request()
            val token = tokenProvider()

            val newRequest = if (token != null) {
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Accept", "application/json")
                    .build()
            } else {
                originalRequest.newBuilder()
                    .header("Accept", "application/json")
                    .build()
            }
            chain.proceed(newRequest)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://gitlab.com/api/v4/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(GitLabApi::class.java)
    }

    @Provides
    @Singleton
    @Named("collaboration")
    fun provideOkHttpClientForCollaboration(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .pingInterval(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }
}