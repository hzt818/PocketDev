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
public final class SaveFileUseCase_Factory implements Factory<SaveFileUseCase> {
  private final Provider<FileRepository> fileRepositoryProvider;

  public SaveFileUseCase_Factory(Provider<FileRepository> fileRepositoryProvider) {
    this.fileRepositoryProvider = fileRepositoryProvider;
  }

  @Override
  public SaveFileUseCase get() {
    return newInstance(fileRepositoryProvider.get());
  }

  public static SaveFileUseCase_Factory create(Provider<FileRepository> fileRepositoryProvider) {
    return new SaveFileUseCase_Factory(fileRepositoryProvider);
  }

  public static SaveFileUseCase newInstance(FileRepository fileRepository) {
    return new SaveFileUseCase(fileRepository);
  }
}
