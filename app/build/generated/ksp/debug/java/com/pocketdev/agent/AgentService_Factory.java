package com.pocketdev.agent;

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
public final class AgentService_Factory implements Factory<AgentService> {
  @Override
  public AgentService get() {
    return newInstance();
  }

  public static AgentService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AgentService newInstance() {
    return new AgentService();
  }

  private static final class InstanceHolder {
    private static final AgentService_Factory INSTANCE = new AgentService_Factory();
  }
}
