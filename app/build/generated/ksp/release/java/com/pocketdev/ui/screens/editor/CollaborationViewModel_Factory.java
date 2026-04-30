package com.pocketdev.ui.screens.editor;

import com.pocketdev.domain.repository.CollaborationRepository;
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
public final class CollaborationViewModel_Factory implements Factory<CollaborationViewModel> {
  private final Provider<CollaborationRepository> collaborationRepositoryProvider;

  public CollaborationViewModel_Factory(
      Provider<CollaborationRepository> collaborationRepositoryProvider) {
    this.collaborationRepositoryProvider = collaborationRepositoryProvider;
  }

  @Override
  public CollaborationViewModel get() {
    return newInstance(collaborationRepositoryProvider.get());
  }

  public static CollaborationViewModel_Factory create(
      Provider<CollaborationRepository> collaborationRepositoryProvider) {
    return new CollaborationViewModel_Factory(collaborationRepositoryProvider);
  }

  public static CollaborationViewModel newInstance(
      CollaborationRepository collaborationRepository) {
    return new CollaborationViewModel(collaborationRepository);
  }
}
