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
public final class E2ERunnerAgent_Factory implements Factory<E2ERunnerAgent> {
  @Override
  public E2ERunnerAgent get() {
    return newInstance();
  }

  public static E2ERunnerAgent_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static E2ERunnerAgent newInstance() {
    return new E2ERunnerAgent();
  }

  private static final class InstanceHolder {
    private static final E2ERunnerAgent_Factory INSTANCE = new E2ERunnerAgent_Factory();
  }
}
