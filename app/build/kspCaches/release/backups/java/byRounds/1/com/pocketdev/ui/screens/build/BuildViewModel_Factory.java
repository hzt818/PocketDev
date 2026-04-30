package com.pocketdev.ui.screens.build;

import com.pocketdev.domain.repository.BuildRepository;
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
public final class BuildViewModel_Factory implements Factory<BuildViewModel> {
  private final Provider<BuildRepository> buildRepositoryProvider;

  public BuildViewModel_Factory(Provider<BuildRepository> buildRepositoryProvider) {
    this.buildRepositoryProvider = buildRepositoryProvider;
  }

  @Override
  public BuildViewModel get() {
    return newInstance(buildRepositoryProvider.get());
  }

  public static BuildViewModel_Factory create(Provider<BuildRepository> buildRepositoryProvider) {
    return new BuildViewModel_Factory(buildRepositoryProvider);
  }

  public static BuildViewModel newInstance(BuildRepository buildRepository) {
    return new BuildViewModel(buildRepository);
  }
}
