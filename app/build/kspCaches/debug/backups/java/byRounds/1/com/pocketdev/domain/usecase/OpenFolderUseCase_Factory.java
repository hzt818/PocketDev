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
public final class OpenFolderUseCase_Factory implements Factory<OpenFolderUseCase> {
  private final Provider<FileRepository> fileRepositoryProvider;

  public OpenFolderUseCase_Factory(Provider<FileRepository> fileRepositoryProvider) {
    this.fileRepositoryProvider = fileRepositoryProvider;
  }

  @Override
  public OpenFolderUseCase get() {
    return newInstance(fileRepositoryProvider.get());
  }

  public static OpenFolderUseCase_Factory create(Provider<FileRepository> fileRepositoryProvider) {
    return new OpenFolderUseCase_Factory(fileRepositoryProvider);
  }

  public static OpenFolderUseCase newInstance(FileRepository fileRepository) {
    return new OpenFolderUseCase(fileRepository);
  }
}
