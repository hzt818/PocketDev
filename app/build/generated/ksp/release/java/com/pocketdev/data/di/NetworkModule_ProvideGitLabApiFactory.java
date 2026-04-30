package com.pocketdev.data.di;

import com.pocketdev.data.local.UserSettingsDataStore;
import com.pocketdev.data.remote.api.GitLabApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class NetworkModule_ProvideGitLabApiFactory implements Factory<GitLabApi> {
  private final Provider<UserSettingsDataStore> userSettingsDataStoreProvider;

  private final Provider<Function0<String>> tokenProvider;

  public NetworkModule_ProvideGitLabApiFactory(
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider,
      Provider<Function0<String>> tokenProvider) {
    this.userSettingsDataStoreProvider = userSettingsDataStoreProvider;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public GitLabApi get() {
    return provideGitLabApi(userSettingsDataStoreProvider.get(), tokenProvider.get());
  }

  public static NetworkModule_ProvideGitLabApiFactory create(
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider,
      Provider<Function0<String>> tokenProvider) {
    return new NetworkModule_ProvideGitLabApiFactory(userSettingsDataStoreProvider, tokenProvider);
  }

  public static GitLabApi provideGitLabApi(UserSettingsDataStore userSettingsDataStore,
      Function0<String> tokenProvider) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideGitLabApi(userSettingsDataStore, tokenProvider));
  }
}
