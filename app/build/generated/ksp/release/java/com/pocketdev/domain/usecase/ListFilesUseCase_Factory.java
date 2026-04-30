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
public final class ListFilesUseCase_Factory implements Factory<ListFilesUseCase> {
  private final Provider<FileRepository> fileRepositoryProvider;

  public ListFilesUseCase_Factory(Provider<FileRepository> fileRepositoryProvider) {
    this.fileRepositoryProvider = fileRepositoryProvider;
  }

  @Override
  public ListFilesUseCase get() {
    return newInstance(fileRepositoryProvider.get());
  }

  public static ListFilesUseCase_Factory create(Provider<FileRepository> fileRepositoryProvider) {
    return new ListFilesUseCase_Factory(fileRepositoryProvider);
  }

  public static ListFilesUseCase newInstance(FileRepository fileRepository) {
    return new ListFilesUseCase(fileRepository);
  }
}
