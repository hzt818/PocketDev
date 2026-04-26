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
public final class RemovePcConnectionUseCase_Factory implements Factory<RemovePcConnectionUseCase> {
  private final Provider<PcConnectionRepository> repositoryProvider;

  public RemovePcConnectionUseCase_Factory(Provider<PcConnectionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RemovePcConnectionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static RemovePcConnectionUseCase_Factory create(
      Provider<PcConnectionRepository> repositoryProvider) {
    return new RemovePcConnectionUseCase_Factory(repositoryProvider);
  }

  public static RemovePcConnectionUseCase newInstance(PcConnectionRepository repository) {
    return new RemovePcConnectionUseCase(repository);
  }
}
