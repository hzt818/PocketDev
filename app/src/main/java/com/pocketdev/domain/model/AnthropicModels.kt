package com.pocketdev.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnthropicRequest(
    val model: String,
    val messages: List<AnthropicMessage>,
    @SerialName("max_tokens")
    val maxTokens: Int = 4096,
    val stream: Boolean = false,
    val temperature: Float? = null
)

@Serializable
data class AnthropicMessage(
    val role: String,
    val content: String
)

@Serializable
data class AnthropicResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<AnthropicContentBlock>,
    val model: String,
    @SerialName("stop_reason")
    val stopReason: String? = null,
    val usage: AnthropicUsage? = null
)

@Serializable
data class AnthropicContentBlock(
    val type: String,
    val text: String? = null
)

@Serializable
data class AnthropicUsage(
    @SerialName("input_tokens")
    val inputTokens: Int,
    @SerialName("output_tokens")
    val outputTokens: Int
)

fun List<Message>.toAnthropicFormat(): List<AnthropicMessage> = map { msg ->
    AnthropicMessage(
        role = if (msg.role == "assistant") "assistant" else msg.role,
        content = msg.content
    )
}

fun AnthropicResponse.toUnifiedContent(): String =
    content.filterIsInstance<AnthropicContentBlock>()
        .filter { it.type == "text" }
        .joinToString("") { it.text ?: "" }