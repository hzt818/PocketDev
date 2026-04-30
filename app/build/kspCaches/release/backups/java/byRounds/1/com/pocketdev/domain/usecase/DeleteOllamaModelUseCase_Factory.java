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
public final class DeleteOllamaModelUseCase_Factory implements Factory<DeleteOllamaModelUseCase> {
  private final Provider<OllamaRepository> repositoryProvider;

  public DeleteOllamaModelUseCase_Factory(Provider<OllamaRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DeleteOllamaModelUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static DeleteOllamaModelUseCase_Factory create(
      Provider<OllamaRepository> repositoryProvider) {
    return new DeleteOllamaModelUseCase_Factory(repositoryProvider);
  }

  public static DeleteOllamaModelUseCase newInstance(OllamaRepository repository) {
    return new DeleteOllamaModelUseCase(repository);
  }
}
