package com.pocketdev.ui.screens.splash;

import com.pocketdev.domain.repository.UserSettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SplashViewModel_Factory implements Factory<SplashViewModel> {
  private final Provider<UserSettingsRepository> userSettingsRepositoryProvider;

  public SplashViewModel_Factory(Provider<UserSettingsRepository> userSettingsRepositoryProvider) {
    this.userSettingsRepositoryProvider = userSettingsRepositoryProvider;
  }

  @Override
  public SplashViewModel get() {
    return newInstance(userSettingsRepositoryProvider.get());
  }

  public static SplashViewModel_Factory create(
      Provider<UserSettingsRepository> userSettingsRepositoryProvider) {
    return new SplashViewModel_Factory(userSettingsRepositoryProvider);
  }

  public static SplashViewModel newInstance(UserSettingsRepository userSettingsRepository) {
    return new SplashViewModel(userSettingsRepository);
  }
}
