package com.pocketdev.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OllamaModel(
    val name: String,
    val size: Long,
    val digest: String,
    @SerialName("modified_at")
    val modifiedAt: String
)

@Serializable
data class OllamaModelInfo(
    val name: String,
    val model: String,
    val size: Long,
    val digest: String,
    @SerialName("details")
    val details: OllamaModelDetails? = null
)

@Serializable
data class OllamaModelDetails(
    val family: String? = null,
    val format: String? = null,
    val parameterSize: String? = null,
    @SerialName("quantization_level")
    val quantizationLevel: String? = null
)

@Serializable
data class OllamaListResponse(
    val models: List<OllamaModel>
)

@Serializable
data class OllamaPullRequest(
    val name: String,
    val stream: Boolean = true
)

@Serializable
data class OllamaProgressResponse(
    val status: String,
    val digest: String? = null,
    val total: Long? = null,
    val completed: Long? = null
)

@Serializable
data class OllamaDeleteRequest(
    val name: String
)

@Serializable
data class OllamaChatRequest(
    val model: String,
    val messages: List<OllamaMessage>,
    val stream: Boolean = false
)

@Serializable
data class OllamaMessage(
    val role: String,
    val content: String
)

@Serializable
data class OllamaChatResponse(
    val model: String,
    val message: OllamaResponseMessage,
    val done: Boolean
)

@Serializable
data class OllamaResponseMessage(
    val role: String,
    val content: String
)

enum class OllamaStatus {
    NOT_INSTALLED,
    INSTALLED,
    DOWNLOADING,
    ERROR
}

data class OllamaModelWithStatus(
    val name: String,
    val displayName: String,
    val description: String,
    val size: Long,
    val status: OllamaStatus,
    val progress: Float = 0f
)

val AVAILABLE_MODELS = listOf(
    OllamaModelWithStatus(
        name = "gemma4:latest",
        displayName = "Gemma 4",
        description = "Google's latest open model, excellent for coding",
        size = 4_900_000_000,
        status = OllamaStatus.NOT_INSTALLED
    ),
    OllamaModelWithStatus(
        name = "llama3:latest",
        displayName = "Llama 3",
        description = "Meta's powerful open-source model",
        size = 4_700_000_000,
        status = OllamaStatus.NOT_INSTALLED
    ),
    OllamaModelWithStatus(
        name = "codellama:latest",
        displayName = "CodeLlama",
        description = "Meta's code-specialized model",
        size = 3_800_000_000,
        status = OllamaStatus.NOT_INSTALLED
    ),
    OllamaModelWithStatus(
        name = "mistral:latest",
        displayName = "Mistral",
        description = "Efficient and capable model",
        size = 4_100_000_000,
        status = OllamaStatus.NOT_INSTALLED
    ),
    OllamaModelWithStatus(
        name = "mixtral:latest",
        displayName = "Mixtral",
        description = "Mixture of experts model",
        size = 26_000_000_000,
        status = OllamaStatus.NOT_INSTALLED
    ),
    OllamaModelWithStatus(
        name = "qwen2.5-coder:latest",
        displayName = "Qwen 2.5 Coder",
        description = "Alibaba's code-specialized model",
        size = 2_300_000_000,
        status = OllamaStatus.NOT_INSTALLED
    )
)
