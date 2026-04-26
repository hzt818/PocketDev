package com.pocketdev.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiGenerationConfig(
    @SerialName("maxOutputTokens")
    val maxOutputTokens: Int? = null,
    val temperature: Float? = null
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    @SerialName("promptFeedback")
    val promptFeedback: GeminiPromptFeedback? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent,
    @SerialName("finishReason")
    val finishReason: String? = null,
    val usageMetadata: GeminiUsageMetadata? = null
)

@Serializable
data class GeminiPromptFeedback(
    val safetyRatings: List<GeminiSafetyRating>? = null
)

@Serializable
data class GeminiUsageMetadata(
    @SerialName("promptTokenCount")
    val promptTokenCount: Int? = null,
    @SerialName("candidatesTokenCount")
    val candidatesTokenCount: Int? = null,
    @SerialName("totalTokenCount")
    val totalTokenCount: Int? = null
)

@Serializable
data class GeminiSafetyRating(
    val category: String,
    val probability: String
)

fun List<Message>.toGeminiFormat(): List<GeminiContent> = map { msg ->
    GeminiContent(
        parts = listOf(GeminiPart(text = msg.content))
    )
}

fun GeminiResponse.toUnifiedContent(): String =
    candidates?.firstOrNull()
        ?.content
        ?.parts
        ?.filter { it.text.isNotBlank() }
        ?.joinToString("") { it.text }
        ?: ""

fun GeminiResponse.toUnifiedUsage(): Usage? {
    val metadata = candidates?.firstOrNull()?.usageMetadata
    return metadata?.let {
        Usage(
            promptTokens = it.promptTokenCount,
            completionTokens = it.candidatesTokenCount,
            totalTokens = it.totalTokenCount
        )
    }
}