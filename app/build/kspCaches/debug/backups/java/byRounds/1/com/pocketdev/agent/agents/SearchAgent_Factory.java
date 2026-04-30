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
public final class SearchAgent_Factory implements Factory<SearchAgent> {
  @Override
  public SearchAgent get() {
    return newInstance();
  }

  public static SearchAgent_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SearchAgent newInstance() {
    return new SearchAgent();
  }

  private static final class InstanceHolder {
    private static final SearchAgent_Factory INSTANCE = new SearchAgent_Factory();
  }
}
