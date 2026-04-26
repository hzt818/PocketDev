package com.pocketdev.domain.repository

import com.pocketdev.domain.model.ChatMessage
import com.pocketdev.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getAllConversations(): Flow<List<Conversation>>
    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>>
    suspend fun getConversation(id: String): Conversation?
    suspend fun createConversation(conversation: Conversation): Result<Unit>
    suspend fun updateConversation(conversation: Conversation): Result<Unit>
    suspend fun deleteConversation(id: String): Result<Unit>
    suspend fun addMessage(conversationId: String, message: ChatMessage): Result<Unit>
    suspend fun updateTitle(conversationId: String, title: String): Result<Unit>
}