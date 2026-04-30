package com.pocketdev.data.di;

import com.pocketdev.data.remote.api.AnthropicApi;
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
public final class NetworkModule_ProvideAnthropicApiFactory implements Factory<AnthropicApi> {
  @Override
  public AnthropicApi get() {
    return provideAnthropicApi();
  }

  public static NetworkModule_ProvideAnthropicApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AnthropicApi provideAnthropicApi() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAnthropicApi());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideAnthropicApiFactory INSTANCE = new NetworkModule_ProvideAnthropicApiFactory();
  }
}
