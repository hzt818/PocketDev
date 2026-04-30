package com.pocketdev.data.repository;

import com.pocketdev.domain.repository.GitHubRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class RemoteRepositoryImpl_Factory implements Factory<RemoteRepositoryImpl> {
  private final Provider<GitHubRepository> gitHubRepositoryProvider;

  public RemoteRepositoryImpl_Factory(Provider<GitHubRepository> gitHubRepositoryProvider) {
    this.gitHubRepositoryProvider = gitHubRepositoryProvider;
  }

  @Override
  public RemoteRepositoryImpl get() {
    return newInstance(gitHubRepositoryProvider.get());
  }

  public static RemoteRepositoryImpl_Factory create(
      Provider<GitHubRepository> gitHubRepositoryProvider) {
    return new RemoteRepositoryImpl_Factory(gitHubRepositoryProvider);
  }

  public static RemoteRepositoryImpl newInstance(GitHubRepository gitHubRepository) {
    return new RemoteRepositoryImpl(gitHubRepository);
  }
}
