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
public final class CommitFileUseCase_Factory implements Factory<CommitFileUseCase> {
  private final Provider<GitHubRepository> gitHubRepositoryProvider;

  public CommitFileUseCase_Factory(Provider<GitHubRepository> gitHubRepositoryProvider) {
    this.gitHubRepositoryProvider = gitHubRepositoryProvider;
  }

  @Override
  public CommitFileUseCase get() {
    return newInstance(gitHubRepositoryProvider.get());
  }

  public static CommitFileUseCase_Factory create(
      Provider<GitHubRepository> gitHubRepositoryProvider) {
    return new CommitFileUseCase_Factory(gitHubRepositoryProvider);
  }

  public static CommitFileUseCase newInstance(GitHubRepository gitHubRepository) {
    return new CommitFileUseCase(gitHubRepository);
  }
}
