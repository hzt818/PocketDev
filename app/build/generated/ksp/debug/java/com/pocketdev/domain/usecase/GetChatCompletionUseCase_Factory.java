package com.pocketdev.domain.usecase;

import com.pocketdev.domain.repository.LlmRepository;
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
public final class GetChatCompletionUseCase_Factory implements Factory<GetChatCompletionUseCase> {
  private final Provider<LlmRepository> llmRepositoryProvider;

  public GetChatCompletionUseCase_Factory(Provider<LlmRepository> llmRepositoryProvider) {
    this.llmRepositoryProvider = llmRepositoryProvider;
  }

  @Override
  public GetChatCompletionUseCase get() {
    return newInstance(llmRepositoryProvider.get());
  }

  public static GetChatCompletionUseCase_Factory create(
      Provider<LlmRepository> llmRepositoryProvider) {
    return new GetChatCompletionUseCase_Factory(llmRepositoryProvider);
  }

  public static GetChatCompletionUseCase newInstance(LlmRepository llmRepository) {
    return new GetChatCompletionUseCase(llmRepository);
  }
}
