package com.pocketdev.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String,
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false
)

@Serializable
data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage? = null
)

@Serializable
data class Choice(
    val message: Message,
    val finishReason: String? = null
)
