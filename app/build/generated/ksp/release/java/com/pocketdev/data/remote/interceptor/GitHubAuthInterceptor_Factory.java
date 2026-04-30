package com.pocketdev.data.remote.interceptor;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlin.jvm.functions.Function0;

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
public final class GitHubAuthInterceptor_Factory implements Factory<GitHubAuthInterceptor> {
  private final Provider<Function0<String>> tokenProvider;

  public GitHubAuthInterceptor_Factory(Provider<Function0<String>> tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  public GitHubAuthInterceptor get() {
    return newInstance(tokenProvider.get());
  }

  public static GitHubAuthInterceptor_Factory create(Provider<Function0<String>> tokenProvider) {
    return new GitHubAuthInterceptor_Factory(tokenProvider);
  }

  public static GitHubAuthInterceptor newInstance(Function0<String> tokenProvider) {
    return new GitHubAuthInterceptor(tokenProvider);
  }
}
