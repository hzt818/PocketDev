package com.pocketdev.data.di;

import com.pocketdev.data.remote.api.GeminiApi;
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
public final class NetworkModule_ProvideGeminiApiFactory implements Factory<GeminiApi> {
  @Override
  public GeminiApi get() {
    return provideGeminiApi();
  }

  public static NetworkModule_ProvideGeminiApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GeminiApi provideGeminiApi() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideGeminiApi());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideGeminiApiFactory INSTANCE = new NetworkModule_ProvideGeminiApiFactory();
  }
}
