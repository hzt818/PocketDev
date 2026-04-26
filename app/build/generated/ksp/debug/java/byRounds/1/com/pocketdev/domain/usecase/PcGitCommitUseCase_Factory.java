package com.pocketdev.domain.usecase;

import com.pocketdev.domain.repository.PcConnectionRepository;
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
public final class PcGitCommitUseCase_Factory implements Factory<PcGitCommitUseCase> {
  private final Provider<PcConnectionRepository> repositoryProvider;

  public PcGitCommitUseCase_Factory(Provider<PcConnectionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public PcGitCommitUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static PcGitCommitUseCase_Factory create(
      Provider<PcConnectionRepository> repositoryProvider) {
    return new PcGitCommitUseCase_Factory(repositoryProvider);
  }

  public static PcGitCommitUseCase newInstance(PcConnectionRepository repository) {
    return new PcGitCommitUseCase(repository);
  }
}
