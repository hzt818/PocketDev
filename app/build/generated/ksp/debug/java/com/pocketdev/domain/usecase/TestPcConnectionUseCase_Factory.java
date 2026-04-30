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
public final class TestPcConnectionUseCase_Factory implements Factory<TestPcConnectionUseCase> {
  private final Provider<PcConnectionRepository> repositoryProvider;

  public TestPcConnectionUseCase_Factory(Provider<PcConnectionRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public TestPcConnectionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static TestPcConnectionUseCase_Factory create(
      Provider<PcConnectionRepository> repositoryProvider) {
    return new TestPcConnectionUseCase_Factory(repositoryProvider);
  }

  public static TestPcConnectionUseCase newInstance(PcConnectionRepository repository) {
    return new TestPcConnectionUseCase(repository);
  }
}
