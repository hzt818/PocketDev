package com.pocketdev.data.repository

import com.pocketdev.data.local.database.ConversationDao
import com.pocketdev.data.local.database.ConversationEntity
import com.pocketdev.data.local.database.MessageDao
import com.pocketdev.data.local.database.MessageEntity
import com.pocketdev.domain.model.ChatMessage
import com.pocketdev.domain.model.Conversation
import com.pocketdev.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ConversationRepository {

    override fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations().map { entities ->
            entities.map { entity ->
                Conversation(
                    id = entity.id,
                    title = entity.title,
                    messages = emptyList(),
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    override fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessage>> {
        return messageDao.getMessagesForConversation(conversationId).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    role = entity.role,
                    content = entity.content,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun getConversation(id: String): Conversation? {
        val entity = conversationDao.getConversationById(id) ?: return null
        val messages = messageDao.getMessagesForConversationSync(id)
        return Conversation(
            id = entity.id,
            title = entity.title,
            messages = messages.map { e ->
                ChatMessage(e.id, e.role, e.content, e.timestamp)
            },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    override suspend fun createConversation(conversation: Conversation): Result<Unit> {
        return try {
            val entity = ConversationEntity(
                id = conversation.id,
                title = conversation.title,
                createdAt = conversation.createdAt,
                updatedAt = conversation.updatedAt
            )
            conversationDao.insertConversation(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateConversation(conversation: Conversation): Result<Unit> {
        return try {
            val entity = ConversationEntity(
                id = conversation.id,
                title = conversation.title,
                createdAt = conversation.createdAt,
                updatedAt = conversation.updatedAt
            )
            conversationDao.updateConversation(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteConversation(id: String): Result<Unit> {
        return try {
            conversationDao.deleteConversationById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addMessage(conversationId: String, message: ChatMessage): Result<Unit> {
        return try {
            val entity = MessageEntity(
                id = message.id,
                conversationId = conversationId,
                role = message.role,
                content = message.content,
                timestamp = message.timestamp
            )
            messageDao.insertMessage(entity)
            conversationDao.updateTimestamp(conversationId, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTitle(conversationId: String, title: String): Result<Unit> {
        return try {
            conversationDao.updateTitle(conversationId, title, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}