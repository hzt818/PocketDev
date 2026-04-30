package com.pocketdev.data.di;

import com.pocketdev.data.remote.api.LlmApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class NetworkModule_ProvideLlmApiFactory implements Factory<LlmApi> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  public NetworkModule_ProvideLlmApiFactory(Provider<OkHttpClient> okHttpClientProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public LlmApi get() {
    return provideLlmApi(okHttpClientProvider.get());
  }

  public static NetworkModule_ProvideLlmApiFactory create(
      Provider<OkHttpClient> okHttpClientProvider) {
    return new NetworkModule_ProvideLlmApiFactory(okHttpClientProvider);
  }

  public static LlmApi provideLlmApi(OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideLlmApi(okHttpClient));
  }
}
