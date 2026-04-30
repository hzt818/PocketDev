package com.pocketdev.ui.screens.terminal;

import com.pocketdev.domain.repository.TerminalRepository;
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
public final class TerminalViewModel_Factory implements Factory<TerminalViewModel> {
  private final Provider<TerminalRepository> terminalRepositoryProvider;

  public TerminalViewModel_Factory(Provider<TerminalRepository> terminalRepositoryProvider) {
    this.terminalRepositoryProvider = terminalRepositoryProvider;
  }

  @Override
  public TerminalViewModel get() {
    return newInstance(terminalRepositoryProvider.get());
  }

  public static TerminalViewModel_Factory create(
      Provider<TerminalRepository> terminalRepositoryProvider) {
    return new TerminalViewModel_Factory(terminalRepositoryProvider);
  }

  public static TerminalViewModel newInstance(TerminalRepository terminalRepository) {
    return new TerminalViewModel(terminalRepository);
  }
}
