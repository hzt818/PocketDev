package com.pocketdev.ui.screens.editor;

import androidx.lifecycle.SavedStateHandle;
import com.pocketdev.domain.repository.RemoteRepositoryGateway;
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
public final class RemoteEditorViewModel_Factory implements Factory<RemoteEditorViewModel> {
  private final Provider<RemoteRepositoryGateway> remoteRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public RemoteEditorViewModel_Factory(Provider<RemoteRepositoryGateway> remoteRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.remoteRepositoryProvider = remoteRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public RemoteEditorViewModel get() {
    return newInstance(remoteRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static RemoteEditorViewModel_Factory create(
      Provider<RemoteRepositoryGateway> remoteRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new RemoteEditorViewModel_Factory(remoteRepositoryProvider, savedStateHandleProvider);
  }

  public static RemoteEditorViewModel newInstance(RemoteRepositoryGateway remoteRepository,
      SavedStateHandle savedStateHandle) {
    return new RemoteEditorViewModel(remoteRepository, savedStateHandle);
  }
}
