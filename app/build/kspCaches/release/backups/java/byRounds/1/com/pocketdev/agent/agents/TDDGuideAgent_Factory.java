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
public final class TDDGuideAgent_Factory implements Factory<TDDGuideAgent> {
  @Override
  public TDDGuideAgent get() {
    return newInstance();
  }

  public static TDDGuideAgent_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TDDGuideAgent newInstance() {
    return new TDDGuideAgent();
  }

  private static final class InstanceHolder {
    private static final TDDGuideAgent_Factory INSTANCE = new TDDGuideAgent_Factory();
  }
}
