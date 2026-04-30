package com.pocketdev.agent.agents;

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
public final class BuildErrorResolverAgent_Factory implements Factory<BuildErrorResolverAgent> {
  @Override
  public BuildErrorResolverAgent get() {
    return newInstance();
  }

  public static BuildErrorResolverAgent_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BuildErrorResolverAgent newInstance() {
    return new BuildErrorResolverAgent();
  }

  private static final class InstanceHolder {
    private static final BuildErrorResolverAgent_Factory INSTANCE = new BuildErrorResolverAgent_Factory();
  }
}
