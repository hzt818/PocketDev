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
public final class SetActivePcConnectionUseCase_Factory implements Factory<SetActivePcConnectionUseCase> {
  private final Provider<PcConnectionRepository> repositoryProvider;

  public SetActivePcConnectionUseCase_Factory(Provider<PcConnectionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SetActivePcConnectionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SetActivePcConnectionUseCase_Factory create(
      Provider<PcConnectionRepository> repositoryProvider) {
    return new SetActivePcConnectionUseCase_Factory(repositoryProvider);
  }

  public static SetActivePcConnectionUseCase newInstance(PcConnectionRepository repository) {
    return new SetActivePcConnectionUseCase(repository);
  }
}
