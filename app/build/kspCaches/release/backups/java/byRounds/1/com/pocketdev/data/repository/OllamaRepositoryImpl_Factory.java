package com.pocketdev.data.repository;

import com.pocketdev.data.local.UserSettingsDataStore;
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
public final class OllamaRepositoryImpl_Factory implements Factory<OllamaRepositoryImpl> {
  private final Provider<UserSettingsDataStore> userSettingsDataStoreProvider;

  public OllamaRepositoryImpl_Factory(
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider) {
    this.userSettingsDataStoreProvider = userSettingsDataStoreProvider;
  }

  @Override
  public OllamaRepositoryImpl get() {
    return newInstance(userSettingsDataStoreProvider.get());
  }

  public static OllamaRepositoryImpl_Factory create(
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider) {
    return new OllamaRepositoryImpl_Factory(userSettingsDataStoreProvider);
  }

  public static OllamaRepositoryImpl newInstance(UserSettingsDataStore userSettingsDataStore) {
    return new OllamaRepositoryImpl(userSettingsDataStore);
  }
}
