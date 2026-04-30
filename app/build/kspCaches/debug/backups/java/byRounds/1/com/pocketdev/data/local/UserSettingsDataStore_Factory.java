package com.pocketdev.data.local;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class UserSettingsDataStore_Factory implements Factory<UserSettingsDataStore> {
  private final Provider<Context> contextProvider;

  public UserSettingsDataStore_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public UserSettingsDataStore get() {
    return newInstance(contextProvider.get());
  }

  public static UserSettingsDataStore_Factory create(Provider<Context> contextProvider) {
    return new UserSettingsDataStore_Factory(contextProvider);
  }

  public static UserSettingsDataStore newInstance(Context context) {
    return new UserSettingsDataStore(context);
  }
}
