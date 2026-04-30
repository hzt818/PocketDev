package com.pocketdev.data.repository;

import com.pocketdev.data.remote.api.LlmApi;
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
public final class LlmRepositoryImpl_Factory implements Factory<LlmRepositoryImpl> {
  private final Provider<LlmApi> llmApiProvider;

  public LlmRepositoryImpl_Factory(Provider<LlmApi> llmApiProvider) {
    this.llmApiProvider = llmApiProvider;
  }

  @Override
  public LlmRepositoryImpl get() {
    return newInstance(llmApiProvider.get());
  }

  public static LlmRepositoryImpl_Factory create(Provider<LlmApi> llmApiProvider) {
    return new LlmRepositoryImpl_Factory(llmApiProvider);
  }

  public static LlmRepositoryImpl newInstance(LlmApi llmApi) {
    return new LlmRepositoryImpl(llmApi);
  }
}
