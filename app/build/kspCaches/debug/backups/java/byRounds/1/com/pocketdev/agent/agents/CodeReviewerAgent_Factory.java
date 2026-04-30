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
public final class CodeReviewerAgent_Factory implements Factory<CodeReviewerAgent> {
  @Override
  public CodeReviewerAgent get() {
    return newInstance();
  }

  public static CodeReviewerAgent_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CodeReviewerAgent newInstance() {
    return new CodeReviewerAgent();
  }

  private static final class InstanceHolder {
    private static final CodeReviewerAgent_Factory INSTANCE = new CodeReviewerAgent_Factory();
  }
}
