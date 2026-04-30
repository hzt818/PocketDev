package com.pocketdev.ui.screens.editor;

import com.pocketdev.domain.repository.FileRepository;
import com.pocketdev.domain.usecase.ListFilesUseCase;
import com.pocketdev.domain.usecase.OpenFolderUseCase;
import com.pocketdev.domain.usecase.ReadFileUseCase;
import com.pocketdev.domain.usecase.SaveFileUseCase;
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
public final class EditorViewModel_Factory implements Factory<EditorViewModel> {
  private final Provider<OpenFolderUseCase> openFolderUseCaseProvider;

  private final Provider<ListFilesUseCase> listFilesUseCaseProvider;

  private final Provider<ReadFileUseCase> readFileUseCaseProvider;

  private final Provider<SaveFileUseCase> saveFileUseCaseProvider;

  private final Provider<FileRepository> fileRepositoryProvider;

  public EditorViewModel_Factory(Provider<OpenFolderUseCase> openFolderUseCaseProvider,
      Provider<ListFilesUseCase> listFilesUseCaseProvider,
      Provider<ReadFileUseCase> readFileUseCaseProvider,
      Provider<SaveFileUseCase> saveFileUseCaseProvider,
      Provider<FileRepository> fileRepositoryProvider) {
    this.openFolderUseCaseProvider = openFolderUseCaseProvider;
    this.listFilesUseCaseProvider = listFilesUseCaseProvider;
    this.readFileUseCaseProvider = readFileUseCaseProvider;
    this.saveFileUseCaseProvider = saveFileUseCaseProvider;
    this.fileRepositoryProvider = fileRepositoryProvider;
  }

  @Override
  public EditorViewModel get() {
    return newInstance(openFolderUseCaseProvider.get(), listFilesUseCaseProvider.get(), readFileUseCaseProvider.get(), saveFileUseCaseProvider.get(), fileRepositoryProvider.get());
  }

  public static EditorViewModel_Factory create(
      Provider<OpenFolderUseCase> openFolderUseCaseProvider,
      Provider<ListFilesUseCase> listFilesUseCaseProvider,
      Provider<ReadFileUseCase> readFileUseCaseProvider,
      Provider<SaveFileUseCase> saveFileUseCaseProvider,
      Provider<FileRepository> fileRepositoryProvider) {
    return new EditorViewModel_Factory(openFolderUseCaseProvider, listFilesUseCaseProvider, readFileUseCaseProvider, saveFileUseCaseProvider, fileRepositoryProvider);
  }

  public static EditorViewModel newInstance(OpenFolderUseCase openFolderUseCase,
      ListFilesUseCase listFilesUseCase, ReadFileUseCase readFileUseCase,
      SaveFileUseCase saveFileUseCase, FileRepository fileRepository) {
    return new EditorViewModel(openFolderUseCase, listFilesUseCase, readFileUseCase, saveFileUseCase, fileRepository);
  }
}
