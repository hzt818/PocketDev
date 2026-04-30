package com.pocketdev.ui.screens.chat;

import com.pocketdev.domain.repository.AiRepository;
import com.pocketdev.domain.repository.ConversationRepository;
import com.pocketdev.domain.repository.UserSettingsRepository;
import com.pocketdev.domain.usecase.CommitFileUseCase;
import com.pocketdev.domain.usecase.GetChatCompletionUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<GetChatCompletionUseCase> getChatCompletionProvider;

  private final Provider<CommitFileUseCase> commitFileUseCaseProvider;

  private final Provider<UserSettingsRepository> userSettingsRepositoryProvider;

  private final Provider<AiRepository> aiRepositoryProvider;

  private final Provider<ConversationRepository> conversationRepositoryProvider;

  public ChatViewModel_Factory(Provider<GetChatCompletionUseCase> getChatCompletionProvider,
      Provider<CommitFileUseCase> commitFileUseCaseProvider,
      Provider<UserSettingsRepository> userSettingsRepositoryProvider,
      Provider<AiRepository> aiRepositoryProvider,
      Provider<ConversationRepository> conversationRepositoryProvider) {
    this.getChatCompletionProvider = getChatCompletionProvider;
    this.commitFileUseCaseProvider = commitFileUseCaseProvider;
    this.userSettingsRepositoryProvider = userSettingsRepositoryProvider;
    this.aiRepositoryProvider = aiRepositoryProvider;
    this.conversationRepositoryProvider = conversationRepositoryProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(getChatCompletionProvider.get(), commitFileUseCaseProvider.get(), userSettingsRepositoryProvider.get(), aiRepositoryProvider.get(), conversationRepositoryProvider.get());
  }

  public static ChatViewModel_Factory create(
      Provider<GetChatCompletionUseCase> getChatCompletionProvider,
      Provider<CommitFileUseCase> commitFileUseCaseProvider,
      Provider<UserSettingsRepository> userSettingsRepositoryProvider,
      Provider<AiRepository> aiRepositoryProvider,
      Provider<ConversationRepository> conversationRepositoryProvider) {
    return new ChatViewModel_Factory(getChatCompletionProvider, commitFileUseCaseProvider, userSettingsRepositoryProvider, aiRepositoryProvider, conversationRepositoryProvider);
  }

  public static ChatViewModel newInstance(GetChatCompletionUseCase getChatCompletion,
      CommitFileUseCase commitFileUseCase, UserSettingsRepository userSettingsRepository,
      AiRepository aiRepository, ConversationRepository conversationRepository) {
    return new ChatViewModel(getChatCompletion, commitFileUseCase, userSettingsRepository, aiRepository, conversationRepository);
  }
}
