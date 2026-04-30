package com.pocketdev.data.di;

import com.pocketdev.data.remote.interceptor.DynamicHostInterceptor;
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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<DynamicHostInterceptor> dynamicHostInterceptorProvider;

  public NetworkModule_ProvideOkHttpClientFactory(
      Provider<DynamicHostInterceptor> dynamicHostInterceptorProvider) {
    this.dynamicHostInterceptorProvider = dynamicHostInterceptorProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(dynamicHostInterceptorProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<DynamicHostInterceptor> dynamicHostInterceptorProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(dynamicHostInterceptorProvider);
  }

  public static OkHttpClient provideOkHttpClient(DynamicHostInterceptor dynamicHostInterceptor) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(dynamicHostInterceptor));
  }
}
