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
public final class PullOllamaModelUseCase_Factory implements Factory<PullOllamaModelUseCase> {
  private final Provider<OllamaRepository> repositoryProvider;

  public PullOllamaModelUseCase_Factory(Provider<OllamaRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public PullOllamaModelUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static PullOllamaModelUseCase_Factory create(
      Provider<OllamaRepository> repositoryProvider) {
    return new PullOllamaModelUseCase_Factory(repositoryProvider);
  }

  public static PullOllamaModelUseCase newInstance(OllamaRepository repository) {
    return new PullOllamaModelUseCase(repository);
  }
}
