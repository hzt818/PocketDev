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
public final class GetReposUseCase_Factory implements Factory<GetReposUseCase> {
  private final Provider<GitHubRepository> gitHubRepositoryProvider;

  public GetReposUseCase_Factory(Provider<GitHubRepository> gitHubRepositoryProvider) {
    this.gitHubRepositoryProvider = gitHubRepositoryProvider;
  }

  @Override
  public GetReposUseCase get() {
    return newInstance(gitHubRepositoryProvider.get());
  }

  public static GetReposUseCase_Factory create(
      Provider<GitHubRepository> gitHubRepositoryProvider) {
    return new GetReposUseCase_Factory(gitHubRepositoryProvider);
  }

  public static GetReposUseCase newInstance(GitHubRepository gitHubRepository) {
    return new GetReposUseCase(gitHubRepository);
  }
}
