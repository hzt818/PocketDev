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
public final class SecurityReviewerAgent_Factory implements Factory<SecurityReviewerAgent> {
  @Override
  public SecurityReviewerAgent get() {
    return newInstance();
  }

  public static SecurityReviewerAgent_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SecurityReviewerAgent newInstance() {
    return new SecurityReviewerAgent();
  }

  private static final class InstanceHolder {
    private static final SecurityReviewerAgent_Factory INSTANCE = new SecurityReviewerAgent_Factory();
  }
}
