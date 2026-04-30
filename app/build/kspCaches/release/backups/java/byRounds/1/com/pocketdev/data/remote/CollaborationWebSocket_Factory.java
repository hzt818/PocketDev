package com.pocketdev.data.remote;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class CollaborationWebSocket_Factory implements Factory<CollaborationWebSocket> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  public CollaborationWebSocket_Factory(Provider<OkHttpClient> okHttpClientProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public CollaborationWebSocket get() {
    return newInstance(okHttpClientProvider.get());
  }

  public static CollaborationWebSocket_Factory create(Provider<OkHttpClient> okHttpClientProvider) {
    return new CollaborationWebSocket_Factory(okHttpClientProvider);
  }

  public static CollaborationWebSocket newInstance(OkHttpClient okHttpClient) {
    return new CollaborationWebSocket(okHttpClient);
  }
}
