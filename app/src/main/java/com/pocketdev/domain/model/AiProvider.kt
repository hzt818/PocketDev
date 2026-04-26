package com.pocketdev.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AiProviderType {
    DEEPSEEK,
    OPENAI,
    ANTHROPIC,
    GEMINI,
    MINIMAX,
    KIMI,
    GLM,
    OLLAMA,
    PC_CLI
}

@Serializable
enum class ApiFormat {
    OPENAI兼容,
    ANTHROPIC格式,
    GEMINI格式,
    OLLAMA格式
}

@Serializable
data class AiProviderConfig(
    @SerialName("type")
    val type: AiProviderType,
    @SerialName("name")
    val name: String,
    @SerialName("baseUrl")
    val baseUrl: String,
    @SerialName("apiKey")
    val apiKey: String,
    @SerialName("modelName")
    val modelName: String,
    @SerialName("isActive")
    val isActive: Boolean = false,
    @SerialName("apiFormat")
    val apiFormat: ApiFormat = ApiFormat.OPENAI兼容
)

@Serializable
data class UnifiedChatRequest(
    @SerialName("provider")
    val provider: AiProviderType,
    @SerialName("model")
    val model: String,
    @SerialName("messages")
    val messages: List<Message>,
    @SerialName("stream")
    val stream: Boolean = false,
    @SerialName("maxTokens")
    val maxTokens: Int? = null,
    @SerialName("temperature")
    val temperature: Float? = null
)

@Serializable
data class UnifiedChatResponse(
    @SerialName("content")
    val content: String,
    @SerialName("provider")
    val provider: AiProviderType,
    @SerialName("model")
    val model: String,
    @SerialName("usage")
    val usage: Usage? = null
)

@Serializable
data class Message(
    @SerialName("role")
    val role: String,
    @SerialName("content")
    val content: String
)

@Serializable
data class Usage(
    @SerialName("promptTokens")
    val promptTokens: Int? = null,
    @SerialName("completionTokens")
    val completionTokens: Int? = null,
    @SerialName("totalTokens")
    val totalTokens: Int? = null
)

val DEFAULT_PROVIDERS = listOf(
    AiProviderConfig(
        type = AiProviderType.DEEPSEEK,
        name = "DeepSeek",
        baseUrl = "https://api.deepseek.com/v1",
        apiKey = "",
        modelName = "deepseek-chat",
        isActive = true,
        apiFormat = ApiFormat.OPENAI兼容
    ),
    AiProviderConfig(
        type = AiProviderType.OPENAI,
        name = "OpenAI",
        baseUrl = "https://api.openai.com/v1",
        apiKey = "",
        modelName = "gpt-4o",
        isActive = false,
        apiFormat = ApiFormat.OPENAI兼容
    ),
    AiProviderConfig(
        type = AiProviderType.ANTHROPIC,
        name = "Anthropic (Claude)",
        baseUrl = "https://api.anthropic.com/v1",
        apiKey = "",
        modelName = "claude-3-5-sonnet-20241022",
        isActive = false,
        apiFormat = ApiFormat.ANTHROPIC格式
    ),
    AiProviderConfig(
        type = AiProviderType.GEMINI,
        name = "Google Gemini",
        baseUrl = "https://generativelanguage.googleapis.com/v1beta",
        apiKey = "",
        modelName = "gemini-2.0-flash",
        isActive = false,
        apiFormat = ApiFormat.GEMINI格式
    ),
    AiProviderConfig(
        type = AiProviderType.MINIMAX,
        name = "MiniMax",
        baseUrl = "https://api.minimax.chat/v1",
        apiKey = "",
        modelName = "MiniMax-Text-01",
        isActive = false,
        apiFormat = ApiFormat.OPENAI兼容
    ),
    AiProviderConfig(
        type = AiProviderType.KIMI,
        name = "Kimi (Moonshot)",
        baseUrl = "https://api.moonshot.cn/v1",
        apiKey = "",
        modelName = "moonshot-v1-8k",
        isActive = false,
        apiFormat = ApiFormat.OPENAI兼容
    ),
    AiProviderConfig(
        type = AiProviderType.GLM,
        name = "GLM (Zhipu)",
        baseUrl = "https://open.bigmodel.cn/api/paas/v4",
        apiKey = "",
        modelName = "glm-4-flash",
        isActive = false,
        apiFormat = ApiFormat.OPENAI兼容
    ),
    AiProviderConfig(
        type = AiProviderType.OLLAMA,
        name = "Ollama (Local)",
        baseUrl = "http://10.0.2.2:11434",
        apiKey = "",
        modelName = "llama3:latest",
        isActive = false,
        apiFormat = ApiFormat.OLLAMA格式
    ),
    AiProviderConfig(
        type = AiProviderType.PC_CLI,
        name = "PC CLI (Remote)",
        baseUrl = "",
        apiKey = "",
        modelName = "",
        isActive = false,
        apiFormat = ApiFormat.OPENAI兼容
    )
)

fun AiProviderType.toApiFormat(): ApiFormat = when (this) {
    AiProviderType.ANTHROPIC -> ApiFormat.ANTHROPIC格式
    AiProviderType.GEMINI -> ApiFormat.GEMINI格式
    AiProviderType.OLLAMA -> ApiFormat.OLLAMA格式
    else -> ApiFormat.OPENAI兼容
}