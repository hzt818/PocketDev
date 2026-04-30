package com.pocketdev.domain.usecase;

import com.pocketdev.domain.repository.GitHubRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AuthenticateGitHubUseCase_Factory implements Factory<AuthenticateGitHubUseCase> {
  private final Provider<GitHubRepository> gitHubRepositoryProvider;

  public AuthenticateGitHubUseCase_Factory(Provider<GitHubRepository> gitHubRepositoryProvider) {
    this.gitHubRepositoryProvider = gitHubRepositoryProvider;
  }

  @Override
  public AuthenticateGitHubUseCase get() {
    return newInstance(gitHubRepositoryProvider.get());
  }

  public static AuthenticateGitHubUseCase_Factory create(
      Provider<GitHubRepository> gitHubRepositoryProvider) {
    return new AuthenticateGitHubUseCase_Factory(gitHubRepositoryProvider);
  }

  public static AuthenticateGitHubUseCase newInstance(GitHubRepository gitHubRepository) {
    return new AuthenticateGitHubUseCase(gitHubRepository);
  }
}
