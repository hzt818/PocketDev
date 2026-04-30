package com.pocketdev.data.di;

import com.pocketdev.data.remote.api.OllamaApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class NetworkModule_ProvideOllamaApiFactory implements Factory<OllamaApi> {
  @Override
  public OllamaApi get() {
    return provideOllamaApi();
  }

  public static NetworkModule_ProvideOllamaApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OllamaApi provideOllamaApi() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOllamaApi());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideOllamaApiFactory INSTANCE = new NetworkModule_ProvideOllamaApiFactory();
  }
}
