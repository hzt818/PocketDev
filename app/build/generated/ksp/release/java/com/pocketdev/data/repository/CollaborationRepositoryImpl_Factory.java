package com.pocketdev.data.repository;

import com.pocketdev.data.remote.CollaborationWebSocket;
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

  public CollaborationRepositoryImpl_Factory(
      Provider<CollaborationWebSocket> collaborationWebSocketProvider) {
    this.collaborationWebSocketProvider = collaborationWebSocketProvider;
  }

  @Override
  public CollaborationRepositoryImpl get() {
    return newInstance(collaborationWebSocketProvider.get());
  }

  public static CollaborationRepositoryImpl_Factory create(
      Provider<CollaborationWebSocket> collaborationWebSocketProvider) {
    return new CollaborationRepositoryImpl_Factory(collaborationWebSocketProvider);
  }

  public static CollaborationRepositoryImpl newInstance(
      CollaborationWebSocket collaborationWebSocket) {
    return new CollaborationRepositoryImpl(collaborationWebSocket);
  }
}
