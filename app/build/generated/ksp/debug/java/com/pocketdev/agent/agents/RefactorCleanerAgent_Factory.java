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
public final class RefactorCleanerAgent_Factory implements Factory<RefactorCleanerAgent> {
  @Override
  public RefactorCleanerAgent get() {
    return newInstance();
  }

  public static RefactorCleanerAgent_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RefactorCleanerAgent newInstance() {
    return new RefactorCleanerAgent();
  }

  private static final class InstanceHolder {
    private static final RefactorCleanerAgent_Factory INSTANCE = new RefactorCleanerAgent_Factory();
  }
}
