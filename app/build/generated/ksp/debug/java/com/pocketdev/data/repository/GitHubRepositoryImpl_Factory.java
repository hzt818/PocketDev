package com.pocketdev.data.repository;

import com.pocketdev.data.local.UserSettingsDataStore;
import com.pocketdev.data.remote.api.GitHubApi;
import com.pocketdev.data.remote.api.GitHubAuthApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlin.jvm.functions.Function0;

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
public final class GitHubRepositoryImpl_Factory implements Factory<GitHubRepositoryImpl> {
  private final Provider<GitHubApi> gitHubApiProvider;

  private final Provider<GitHubAuthApi> gitHubAuthApiProvider;

  private final Provider<UserSettingsDataStore> userSettingsDataStoreProvider;

  private final Provider<Function0<String>> tokenProvider;

  public GitHubRepositoryImpl_Factory(Provider<GitHubApi> gitHubApiProvider,
      Provider<GitHubAuthApi> gitHubAuthApiProvider,
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider,
      Provider<Function0<String>> tokenProvider) {
    this.gitHubApiProvider = gitHubApiProvider;
    this.gitHubAuthApiProvider = gitHubAuthApiProvider;
    this.userSettingsDataStoreProvider = userSettingsDataStoreProvider;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public GitHubRepositoryImpl get() {
    return newInstance(gitHubApiProvider.get(), gitHubAuthApiProvider.get(), userSettingsDataStoreProvider.get(), tokenProvider.get());
  }

  public static GitHubRepositoryImpl_Factory create(Provider<GitHubApi> gitHubApiProvider,
      Provider<GitHubAuthApi> gitHubAuthApiProvider,
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider,
      Provider<Function0<String>> tokenProvider) {
    return new GitHubRepositoryImpl_Factory(gitHubApiProvider, gitHubAuthApiProvider, userSettingsDataStoreProvider, tokenProvider);
  }

  public static GitHubRepositoryImpl newInstance(GitHubApi gitHubApi, GitHubAuthApi gitHubAuthApi,
      UserSettingsDataStore userSettingsDataStore, Function0<String> tokenProvider) {
    return new GitHubRepositoryImpl(gitHubApi, gitHubAuthApi, userSettingsDataStore, tokenProvider);
  }
}
