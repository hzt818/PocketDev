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
public final class ReadPcFileUseCase_Factory implements Factory<ReadPcFileUseCase> {
  private final Provider<PcConnectionRepository> repositoryProvider;

  public ReadPcFileUseCase_Factory(Provider<PcConnectionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ReadPcFileUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ReadPcFileUseCase_Factory create(
      Provider<PcConnectionRepository> repositoryProvider) {
    return new ReadPcFileUseCase_Factory(repositoryProvider);
  }

  public static ReadPcFileUseCase newInstance(PcConnectionRepository repository) {
    return new ReadPcFileUseCase(repository);
  }
}
