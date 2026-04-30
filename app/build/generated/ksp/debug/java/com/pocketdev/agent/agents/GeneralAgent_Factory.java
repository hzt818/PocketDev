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
public final class GeneralAgent_Factory implements Factory<GeneralAgent> {
  @Override
  public GeneralAgent get() {
    return newInstance();
  }

  public static GeneralAgent_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GeneralAgent newInstance() {
    return new GeneralAgent();
  }

  private static final class InstanceHolder {
    private static final GeneralAgent_Factory INSTANCE = new GeneralAgent_Factory();
  }
}
