package com.pocketdev.data.local;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class GitHubAuthManager_Factory implements Factory<GitHubAuthManager> {
  private final Provider<Context> contextProvider;

  public GitHubAuthManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public GitHubAuthManager get() {
    return newInstance(contextProvider.get());
  }

  public static GitHubAuthManager_Factory create(Provider<Context> contextProvider) {
    return new GitHubAuthManager_Factory(contextProvider);
  }

  public static GitHubAuthManager newInstance(Context context) {
    return new GitHubAuthManager(context);
  }
}
