package com.pocketdev.data.remote.interceptor;

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
public final class DynamicHostInterceptor_Factory implements Factory<DynamicHostInterceptor> {
  private final Provider<UserSettingsDataStore> userSettingsDataStoreProvider;

  public DynamicHostInterceptor_Factory(
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider) {
    this.userSettingsDataStoreProvider = userSettingsDataStoreProvider;
  }

  @Override
  public DynamicHostInterceptor get() {
    return newInstance(userSettingsDataStoreProvider.get());
  }

  public static DynamicHostInterceptor_Factory create(
      Provider<UserSettingsDataStore> userSettingsDataStoreProvider) {
    return new DynamicHostInterceptor_Factory(userSettingsDataStoreProvider);
  }

  public static DynamicHostInterceptor newInstance(UserSettingsDataStore userSettingsDataStore) {
    return new DynamicHostInterceptor(userSettingsDataStore);
  }
}
