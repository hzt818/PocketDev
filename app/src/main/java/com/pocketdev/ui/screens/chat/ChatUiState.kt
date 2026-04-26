package com.pocketdev.ui.screens.chat

import com.pocketdev.domain.model.AiResponse
import com.pocketdev.domain.model.ChatMessage
import com.pocketdev.domain.model.Conversation

data class ChatUiState(
    val conversations: List<Conversation> = emptyList(),
    val currentConversationId: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentAiResponse: AiResponse? = null,
    val selectedRepo: SelectedRepo? = null,
    val drawerVisible: Boolean = false
)

data class SelectedRepo(
    val owner: String,
    val repo: String,
    val branch: String
)

sealed interface ChatEvent {
    data class SendMessage(val content: String) : ChatEvent
    data class SelectRepo(val owner: String, val repo: String, val branch: String) : ChatEvent
    data object ClearError : ChatEvent
    data object ClearAiResponse : ChatEvent
    data object CreateConversation : ChatEvent
    data class SelectConversation(val conversationId: String) : ChatEvent
    data class DeleteConversation(val conversationId: String) : ChatEvent
    data object ToggleDrawer : ChatEvent
}