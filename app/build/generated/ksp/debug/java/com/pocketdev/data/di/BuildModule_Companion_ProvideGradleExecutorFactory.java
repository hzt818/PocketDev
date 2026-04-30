package com.pocketdev.data.di;

import com.pocketdev.data.build.GradleExecutor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class BuildModule_Companion_ProvideGradleExecutorFactory implements Factory<GradleExecutor> {
  @Override
  public GradleExecutor get() {
    return provideGradleExecutor();
  }

  public static BuildModule_Companion_ProvideGradleExecutorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GradleExecutor provideGradleExecutor() {
    return Preconditions.checkNotNullFromProvides(BuildModule.Companion.provideGradleExecutor());
  }

  private static final class InstanceHolder {
    private static final BuildModule_Companion_ProvideGradleExecutorFactory INSTANCE = new BuildModule_Companion_ProvideGradleExecutorFactory();
  }
}
