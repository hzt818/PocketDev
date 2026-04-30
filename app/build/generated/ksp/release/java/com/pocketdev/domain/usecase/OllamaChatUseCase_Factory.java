package com.pocketdev.domain.usecase;

import com.pocketdev.domain.repository.OllamaRepository;
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
public final class OllamaChatUseCase_Factory implements Factory<OllamaChatUseCase> {
  private final Provider<OllamaRepository> repositoryProvider;

  public OllamaChatUseCase_Factory(Provider<OllamaRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public OllamaChatUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static OllamaChatUseCase_Factory create(Provider<OllamaRepository> repositoryProvider) {
    return new OllamaChatUseCase_Factory(repositoryProvider);
  }

  public static OllamaChatUseCase newInstance(OllamaRepository repository) {
    return new OllamaChatUseCase(repository);
  }
}
