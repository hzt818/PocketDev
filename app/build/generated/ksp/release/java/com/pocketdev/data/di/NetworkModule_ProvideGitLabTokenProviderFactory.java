package com.pocketdev.data.di;

import com.pocketdev.data.local.UserSettingsDataStore;
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
public final class NetworkModule_ProvideGitLabTokenProviderFactory implements Factory<Function0<String>> {
  private final Provider<UserSettingsDataStore> userSettingsDataStoreProvider;

  public NetworkModule_ProvideGitLabTokenProviderFactory(
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider) {
    this.userSettingsDataStoreProvider = userSettingsDataStoreProvider;
  }

  @Override
  public Function0<String> get() {
    return provideGitLabTokenProvider(userSettingsDataStoreProvider.get());
  }

  public static NetworkModule_ProvideGitLabTokenProviderFactory create(
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider) {
    return new NetworkModule_ProvideGitLabTokenProviderFactory(userSettingsDataStoreProvider);
  }

  public static Function0<String> provideGitLabTokenProvider(
      UserSettingsDataStore userSettingsDataStore) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideGitLabTokenProvider(userSettingsDataStore));
  }
}
