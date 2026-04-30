package com.pocketdev.data.repository;

import com.pocketdev.data.build.GradleExecutor;
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
public final class BuildRepositoryImpl_Factory implements Factory<BuildRepositoryImpl> {
  private final Provider<GradleExecutor> gradleExecutorProvider;

  public BuildRepositoryImpl_Factory(Provider<GradleExecutor> gradleExecutorProvider) {
    this.gradleExecutorProvider = gradleExecutorProvider;
  }

  @Override
  public BuildRepositoryImpl get() {
    return newInstance(gradleExecutorProvider.get());
  }

  public static BuildRepositoryImpl_Factory create(
      Provider<GradleExecutor> gradleExecutorProvider) {
    return new BuildRepositoryImpl_Factory(gradleExecutorProvider);
  }

  public static BuildRepositoryImpl newInstance(GradleExecutor gradleExecutor) {
    return new BuildRepositoryImpl(gradleExecutor);
  }
}
