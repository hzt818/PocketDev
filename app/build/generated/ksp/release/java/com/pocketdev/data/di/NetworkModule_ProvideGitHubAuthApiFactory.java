package com.pocketdev.data.di;

import com.pocketdev.data.remote.api.GitHubAuthApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class NetworkModule_ProvideGitHubAuthApiFactory implements Factory<GitHubAuthApi> {
  @Override
  public GitHubAuthApi get() {
    return provideGitHubAuthApi();
  }

  public static NetworkModule_ProvideGitHubAuthApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GitHubAuthApi provideGitHubAuthApi() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideGitHubAuthApi());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideGitHubAuthApiFactory INSTANCE = new NetworkModule_ProvideGitHubAuthApiFactory();
  }
}
