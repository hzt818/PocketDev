package com.pocketdev.data.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
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
public final class NetworkModule_ProvideOkHttpClientForCollaborationFactory implements Factory<OkHttpClient> {
  @Override
  public OkHttpClient get() {
    return provideOkHttpClientForCollaboration();
  }

  public static NetworkModule_ProvideOkHttpClientForCollaborationFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OkHttpClient provideOkHttpClientForCollaboration() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClientForCollaboration());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideOkHttpClientForCollaborationFactory INSTANCE = new NetworkModule_ProvideOkHttpClientForCollaborationFactory();
  }
}
