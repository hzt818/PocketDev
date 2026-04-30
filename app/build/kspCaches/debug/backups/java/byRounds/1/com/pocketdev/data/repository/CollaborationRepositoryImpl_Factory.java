package com.pocketdev.data.repository;

import com.pocketdev.data.remote.CollaborationWebSocket;
import com.pocketdev.domain.repository.UserSettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class CollaborationRepositoryImpl_Factory implements Factory<CollaborationRepositoryImpl> {
  private final Provider<CollaborationWebSocket> collaborationWebSocketProvider;

  private final Provider<UserSettingsRepository> userSettingsRepositoryProvider;

  public CollaborationRepositoryImpl_Factory(
      Provider<CollaborationWebSocket> collaborationWebSocketProvider,
      Provider<UserSettingsRepository> userSettingsRepositoryProvider) {
    this.collaborationWebSocketProvider = collaborationWebSocketProvider;
    this.userSettingsRepositoryProvider = userSettingsRepositoryProvider;
  }

  @Override
  public CollaborationRepositoryImpl get() {
    return newInstance(collaborationWebSocketProvider.get(), userSettingsRepositoryProvider.get());
  }

  public static CollaborationRepositoryImpl_Factory create(
      Provider<CollaborationWebSocket> collaborationWebSocketProvider,
      Provider<UserSettingsRepository> userSettingsRepositoryProvider) {
    return new CollaborationRepositoryImpl_Factory(collaborationWebSocketProvider, userSettingsRepositoryProvider);
  }

  public static CollaborationRepositoryImpl newInstance(
      CollaborationWebSocket collaborationWebSocket,
      UserSettingsRepository userSettingsRepository) {
    return new CollaborationRepositoryImpl(collaborationWebSocket, userSettingsRepository);
  }
}
