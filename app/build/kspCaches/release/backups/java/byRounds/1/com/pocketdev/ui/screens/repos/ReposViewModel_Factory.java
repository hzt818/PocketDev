package com.pocketdev.ui.screens.repos;

import com.pocketdev.domain.usecase.AuthenticateGitHubUseCase;
import com.pocketdev.domain.usecase.GetReposUseCase;
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
public final class ReposViewModel_Factory implements Factory<ReposViewModel> {
  private final Provider<GetReposUseCase> getReposUseCaseProvider;

  private final Provider<AuthenticateGitHubUseCase> authenticateGitHubUseCaseProvider;

  public ReposViewModel_Factory(Provider<GetReposUseCase> getReposUseCaseProvider,
      Provider<AuthenticateGitHubUseCase> authenticateGitHubUseCaseProvider) {
    this.getReposUseCaseProvider = getReposUseCaseProvider;
    this.authenticateGitHubUseCaseProvider = authenticateGitHubUseCaseProvider;
  }

  @Override
  public ReposViewModel get() {
    return newInstance(getReposUseCaseProvider.get(), authenticateGitHubUseCaseProvider.get());
  }

  public static ReposViewModel_Factory create(Provider<GetReposUseCase> getReposUseCaseProvider,
      Provider<AuthenticateGitHubUseCase> authenticateGitHubUseCaseProvider) {
    return new ReposViewModel_Factory(getReposUseCaseProvider, authenticateGitHubUseCaseProvider);
  }

  public static ReposViewModel newInstance(GetReposUseCase getReposUseCase,
      AuthenticateGitHubUseCase authenticateGitHubUseCase) {
    return new ReposViewModel(getReposUseCase, authenticateGitHubUseCase);
  }
}
