package com.pocketdev.ui.screens.ollama;

import com.pocketdev.domain.repository.OllamaRepository;
import com.pocketdev.domain.usecase.DeleteOllamaModelUseCase;
import com.pocketdev.domain.usecase.GetOllamaModelsUseCase;
import com.pocketdev.domain.usecase.PullOllamaModelUseCase;
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
public final class OllamaViewModel_Factory implements Factory<OllamaViewModel> {
  private final Provider<GetOllamaModelsUseCase> getOllamaModelsProvider;

  private final Provider<PullOllamaModelUseCase> pullOllamaModelProvider;

  private final Provider<DeleteOllamaModelUseCase> deleteOllamaModelProvider;

  private final Provider<OllamaRepository> ollamaRepositoryProvider;

  public OllamaViewModel_Factory(Provider<GetOllamaModelsUseCase> getOllamaModelsProvider,
      Provider<PullOllamaModelUseCase> pullOllamaModelProvider,
      Provider<DeleteOllamaModelUseCase> deleteOllamaModelProvider,
      Provider<OllamaRepository> ollamaRepositoryProvider) {
    this.getOllamaModelsProvider = getOllamaModelsProvider;
    this.pullOllamaModelProvider = pullOllamaModelProvider;
    this.deleteOllamaModelProvider = deleteOllamaModelProvider;
    this.ollamaRepositoryProvider = ollamaRepositoryProvider;
  }

  @Override
  public OllamaViewModel get() {
    return newInstance(getOllamaModelsProvider.get(), pullOllamaModelProvider.get(), deleteOllamaModelProvider.get(), ollamaRepositoryProvider.get());
  }

  public static OllamaViewModel_Factory create(
      Provider<GetOllamaModelsUseCase> getOllamaModelsProvider,
      Provider<PullOllamaModelUseCase> pullOllamaModelProvider,
      Provider<DeleteOllamaModelUseCase> deleteOllamaModelProvider,
      Provider<OllamaRepository> ollamaRepositoryProvider) {
    return new OllamaViewModel_Factory(getOllamaModelsProvider, pullOllamaModelProvider, deleteOllamaModelProvider, ollamaRepositoryProvider);
  }

  public static OllamaViewModel newInstance(GetOllamaModelsUseCase getOllamaModels,
      PullOllamaModelUseCase pullOllamaModel, DeleteOllamaModelUseCase deleteOllamaModel,
      OllamaRepository ollamaRepository) {
    return new OllamaViewModel(getOllamaModels, pullOllamaModel, deleteOllamaModel, ollamaRepository);
  }
}
