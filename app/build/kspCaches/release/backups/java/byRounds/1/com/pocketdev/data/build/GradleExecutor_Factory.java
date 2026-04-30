package com.pocketdev.data.build;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class GradleExecutor_Factory implements Factory<GradleExecutor> {
  @Override
  public GradleExecutor get() {
    return newInstance();
  }

  public static GradleExecutor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GradleExecutor newInstance() {
    return new GradleExecutor();
  }

  private static final class InstanceHolder {
    private static final GradleExecutor_Factory INSTANCE = new GradleExecutor_Factory();
  }
}
