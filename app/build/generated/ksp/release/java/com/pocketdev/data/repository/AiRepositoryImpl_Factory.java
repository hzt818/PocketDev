package com.pocketdev.data.repository;

import com.pocketdev.data.remote.api.AnthropicApi;
import com.pocketdev.data.remote.api.GeminiApi;
import com.pocketdev.data.remote.api.LlmApi;
import com.pocketdev.data.remote.api.OllamaApi;
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
public final class AiRepositoryImpl_Factory implements Factory<AiRepositoryImpl> {
  private final Provider<LlmApi> llmApiProvider;

  private final Provider<AnthropicApi> anthropicApiProvider;

  private final Provider<GeminiApi> geminiApiProvider;

  private final Provider<OllamaApi> ollamaApiProvider;

  public AiRepositoryImpl_Factory(Provider<LlmApi> llmApiProvider,
      Provider<AnthropicApi> anthropicApiProvider, Provider<GeminiApi> geminiApiProvider,
      Provider<OllamaApi> ollamaApiProvider) {
    this.llmApiProvider = llmApiProvider;
    this.anthropicApiProvider = anthropicApiProvider;
    this.geminiApiProvider = geminiApiProvider;
    this.ollamaApiProvider = ollamaApiProvider;
  }

  @Override
  public AiRepositoryImpl get() {
    return newInstance(llmApiProvider.get(), anthropicApiProvider.get(), geminiApiProvider.get(), ollamaApiProvider.get());
  }

  public static AiRepositoryImpl_Factory create(Provider<LlmApi> llmApiProvider,
      Provider<AnthropicApi> anthropicApiProvider, Provider<GeminiApi> geminiApiProvider,
      Provider<OllamaApi> ollamaApiProvider) {
    return new AiRepositoryImpl_Factory(llmApiProvider, anthropicApiProvider, geminiApiProvider, ollamaApiProvider);
  }

  public static AiRepositoryImpl newInstance(LlmApi llmApi, AnthropicApi anthropicApi,
      GeminiApi geminiApi, OllamaApi ollamaApi) {
    return new AiRepositoryImpl(llmApi, anthropicApi, geminiApi, ollamaApi);
  }
}
