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
public final class GetOllamaModelsUseCase_Factory implements Factory<GetOllamaModelsUseCase> {
  private final Provider<OllamaRepository> repositoryProvider;

  public GetOllamaModelsUseCase_Factory(Provider<OllamaRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetOllamaModelsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetOllamaModelsUseCase_Factory create(
      Provider<OllamaRepository> repositoryProvider) {
    return new GetOllamaModelsUseCase_Factory(repositoryProvider);
  }

  public static GetOllamaModelsUseCase newInstance(OllamaRepository repository) {
    return new GetOllamaModelsUseCase(repository);
  }
}
