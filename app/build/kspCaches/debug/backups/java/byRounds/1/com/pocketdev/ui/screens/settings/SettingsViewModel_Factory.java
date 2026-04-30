package com.pocketdev.ui.screens.settings;

import android.content.Context;
import com.pocketdev.domain.repository.UserSettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UserSettingsRepository> userSettingsRepositoryProvider;

  private final Provider<Context> contextProvider;

  public SettingsViewModel_Factory(Provider<UserSettingsRepository> userSettingsRepositoryProvider,
      Provider<Context> contextProvider) {
    this.userSettingsRepositoryProvider = userSettingsRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(userSettingsRepositoryProvider.get(), contextProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<UserSettingsRepository> userSettingsRepositoryProvider,
      Provider<Context> contextProvider) {
    return new SettingsViewModel_Factory(userSettingsRepositoryProvider, contextProvider);
  }

  public static SettingsViewModel newInstance(UserSettingsRepository userSettingsRepository,
      Context context) {
    return new SettingsViewModel(userSettingsRepository, context);
  }
}
