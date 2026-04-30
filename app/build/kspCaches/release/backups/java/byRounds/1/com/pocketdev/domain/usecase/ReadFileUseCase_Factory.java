package com.pocketdev.domain.usecase;

import com.pocketdev.domain.repository.FileRepository;
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
public final class ReadFileUseCase_Factory implements Factory<ReadFileUseCase> {
  private final Provider<FileRepository> fileRepositoryProvider;

  public ReadFileUseCase_Factory(Provider<FileRepository> fileRepositoryProvider) {
    this.fileRepositoryProvider = fileRepositoryProvider;
  }

  @Override
  public ReadFileUseCase get() {
    return newInstance(fileRepositoryProvider.get());
  }

  public static ReadFileUseCase_Factory create(Provider<FileRepository> fileRepositoryProvider) {
    return new ReadFileUseCase_Factory(fileRepositoryProvider);
  }

  public static ReadFileUseCase newInstance(FileRepository fileRepository) {
    return new ReadFileUseCase(fileRepository);
  }
}
