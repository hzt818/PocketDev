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
public final class PcShellExecuteUseCase_Factory implements Factory<PcShellExecuteUseCase> {
  private final Provider<PcConnectionRepository> repositoryProvider;

  public PcShellExecuteUseCase_Factory(Provider<PcConnectionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public PcShellExecuteUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static PcShellExecuteUseCase_Factory create(
      Provider<PcConnectionRepository> repositoryProvider) {
    return new PcShellExecuteUseCase_Factory(repositoryProvider);
  }

  public static PcShellExecuteUseCase newInstance(PcConnectionRepository repository) {
    return new PcShellExecuteUseCase(repository);
  }
}
