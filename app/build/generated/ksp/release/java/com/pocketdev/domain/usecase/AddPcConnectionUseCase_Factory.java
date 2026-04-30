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
public final class AddPcConnectionUseCase_Factory implements Factory<AddPcConnectionUseCase> {
  private final Provider<PcConnectionRepository> repositoryProvider;

  public AddPcConnectionUseCase_Factory(Provider<PcConnectionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AddPcConnectionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static AddPcConnectionUseCase_Factory create(
      Provider<PcConnectionRepository> repositoryProvider) {
    return new AddPcConnectionUseCase_Factory(repositoryProvider);
  }

  public static AddPcConnectionUseCase newInstance(PcConnectionRepository repository) {
    return new AddPcConnectionUseCase(repository);
  }
}
