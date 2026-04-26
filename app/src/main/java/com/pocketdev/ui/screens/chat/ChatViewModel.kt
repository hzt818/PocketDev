package com.pocketdev.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketdev.domain.model.AiResponse
import com.pocketdev.domain.model.ChatMessage
import com.pocketdev.domain.model.Conversation
import com.pocketdev.domain.model.Message
import com.pocketdev.domain.model.createConversation
import com.pocketdev.domain.repository.AiRepository
import com.pocketdev.domain.repository.ConversationRepository
import com.pocketdev.domain.repository.UserSettingsRepository
import com.pocketdev.domain.usecase.CommitFileUseCase
import com.pocketdev.domain.usecase.GetChatCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatCompletion: GetChatCompletionUseCase,
    private val commitFileUseCase: CommitFileUseCase,
    private val userSettingsRepository: UserSettingsRepository,
    private val aiRepository: AiRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            conversationRepository.getAllConversations().collect { conversations ->
                val currentId = _uiState.value.currentConversationId
                val validCurrentId = if (currentId != null && conversations.any { it.id == currentId }) {
                    currentId
                } else {
                    conversations.firstOrNull()?.id
                }

                _uiState.update {
                    it.copy(
                        conversations = conversations,
                        currentConversationId = validCurrentId
                    )
                }

                if (validCurrentId != null) {
                    loadMessagesForConversation(validCurrentId)
                }
            }
        }
    }

    private fun loadMessagesForConversation(conversationId: String) {
        viewModelScope.launch {
            conversationRepository.getMessagesForConversation(conversationId).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SendMessage -> sendMessage(event.content)
            is ChatEvent.SelectRepo -> selectRepo(event.owner, event.repo, event.branch)
            is ChatEvent.ClearError -> clearError()
            is ChatEvent.ClearAiResponse -> clearAiResponse()
            is ChatEvent.CreateConversation -> createNewConversation()
            is ChatEvent.SelectConversation -> selectConversation(event.conversationId)
            is ChatEvent.DeleteConversation -> deleteConversation(event.conversationId)
            is ChatEvent.ToggleDrawer -> toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        _uiState.update { it.copy(drawerVisible = !it.drawerVisible) }
    }

    private fun createNewConversation() {
        viewModelScope.launch {
            val conversation = com.pocketdev.domain.model.createConversation()
            conversationRepository.createConversation(conversation)
            _uiState.update {
                it.copy(
                    currentConversationId = conversation.id,
                    messages = emptyList(),
                    drawerVisible = false
                )
            }
        }
    }

    private fun selectConversation(conversationId: String) {
        _uiState.update {
            it.copy(
                currentConversationId = conversationId,
                drawerVisible = false
            )
        }
        loadMessagesForConversation(conversationId)
    }

    private fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            conversationRepository.deleteConversation(conversationId)
            if (_uiState.value.currentConversationId == conversationId) {
                val remaining = _uiState.value.conversations.filter { it.id != conversationId }
                val newCurrentId = remaining.firstOrNull()?.id
                _uiState.update {
                    it.copy(
                        currentConversationId = newCurrentId,
                        messages = emptyList()
                    )
                }
                if (newCurrentId != null) {
                    loadMessagesForConversation(newCurrentId)
                }
            }
        }
    }

    private fun sendMessage(content: String) {
        val conversationId = _uiState.value.currentConversationId
            ?: _uiState.value.conversations.firstOrNull()?.id
            ?: return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = "user",
            content = content
        )

        viewModelScope.launch {
            conversationRepository.addMessage(conversationId, userMessage)
        }

        _uiState.update { it.copy(messages = it.messages + userMessage, isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val activeProvider = userSettingsRepository.getActiveProvider()
                    ?: throw Exception("No AI provider configured")

                val messages = _uiState.value.messages.map { Message(it.role, it.content) }
                val requestMessages = listOf(Message("system", SYSTEM_PROMPT)) + messages

                val result = aiRepository.sendChat(
                    provider = activeProvider.type,
                    model = activeProvider.modelName,
                    messages = requestMessages,
                    baseUrl = activeProvider.baseUrl,
                    apiKey = activeProvider.apiKey,
                    apiFormat = activeProvider.apiFormat
                )

                result.fold(
                    onSuccess = { response ->
                        val aiMessage = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            role = "assistant",
                            content = response.content
                        )
                        viewModelScope.launch {
                            conversationRepository.addMessage(conversationId, aiMessage)
                        }
                        _uiState.update { it.copy(messages = it.messages + aiMessage, isLoading = false) }
                        parseAiResponse(response.content)
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Unknown error occurred"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    private fun parseAiResponse(content: String) {
        viewModelScope.launch {
            try {
                val cleanedContent = content
                    .trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val aiResponse = json.decodeFromString<AiResponse>(cleanedContent)
                _uiState.update { it.copy(currentAiResponse = aiResponse) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to parse AI response: ${e.message}") }
            }
        }
    }

    private fun selectRepo(owner: String, repo: String, branch: String) {
        _uiState.update {
            it.copy(selectedRepo = SelectedRepo(owner, repo, branch))
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun clearAiResponse() {
        _uiState.update { it.copy(currentAiResponse = null) }
    }

    fun commitFile(fileName: String, codeContent: String, commitMessage: String) {
        val repo = _uiState.value.selectedRepo ?: return

        viewModelScope.launch {
            try {
                val result = commitFileUseCase(
                    owner = repo.owner,
                    repo = repo.repo,
                    path = fileName,
                    content = codeContent,
                    message = commitMessage
                )

                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(currentAiResponse = it.currentAiResponse?.copy(
                                actions = it.currentAiResponse.actions.filter { action -> action.fileName != fileName }
                            ))
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(error = "Commit failed: ${error.message}") }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Commit failed: ${e.message}") }
            }
        }
    }
}