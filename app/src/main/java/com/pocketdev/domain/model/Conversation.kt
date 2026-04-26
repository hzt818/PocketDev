package com.pocketdev.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Conversation(
    val id: String,
    val title: String,
    val messages: List<ChatMessage> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long
)

fun createConversation(
    id: String = UUID.randomUUID().toString(),
    title: String = "New Chat",
    messages: List<ChatMessage> = emptyList(),
    createdAt: Long = System.currentTimeMillis(),
    updatedAt: Long = System.currentTimeMillis()
): Conversation = Conversation(
    id = id,
    title = title,
    messages = messages,
    createdAt = createdAt,
    updatedAt = updatedAt
)