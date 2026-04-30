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
public final class DocUpdaterAgent_Factory implements Factory<DocUpdaterAgent> {
  @Override
  public DocUpdaterAgent get() {
    return newInstance();
  }

  public static DocUpdaterAgent_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DocUpdaterAgent newInstance() {
    return new DocUpdaterAgent();
  }

  private static final class InstanceHolder {
    private static final DocUpdaterAgent_Factory INSTANCE = new DocUpdaterAgent_Factory();
  }
}
