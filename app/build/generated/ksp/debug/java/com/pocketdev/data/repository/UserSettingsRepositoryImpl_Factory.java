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
public final class UserSettingsRepositoryImpl_Factory implements Factory<UserSettingsRepositoryImpl> {
  private final Provider<UserSettingsDataStore> userSettingsDataStoreProvider;

  public UserSettingsRepositoryImpl_Factory(
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider) {
    this.userSettingsDataStoreProvider = userSettingsDataStoreProvider;
  }

  @Override
  public UserSettingsRepositoryImpl get() {
    return newInstance(userSettingsDataStoreProvider.get());
  }

  public static UserSettingsRepositoryImpl_Factory create(
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider) {
    return new UserSettingsRepositoryImpl_Factory(userSettingsDataStoreProvider);
  }

  public static UserSettingsRepositoryImpl newInstance(
      UserSettingsDataStore userSettingsDataStore) {
    return new UserSettingsRepositoryImpl(userSettingsDataStore);
  }
}
