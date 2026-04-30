package com.pocketdev.data.repository;

import com.pocketdev.domain.repository.PcConnectionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class TerminalRepositoryImpl_Factory implements Factory<TerminalRepositoryImpl> {
  private final Provider<PcConnectionRepository> pcConnectionRepositoryProvider;

  public TerminalRepositoryImpl_Factory(
      Provider<PcConnectionRepository> pcConnectionRepositoryProvider) {
    this.pcConnectionRepositoryProvider = pcConnectionRepositoryProvider;
  }

  @Override
  public TerminalRepositoryImpl get() {
    return newInstance(pcConnectionRepositoryProvider.get());
  }

  public static TerminalRepositoryImpl_Factory create(
      Provider<PcConnectionRepository> pcConnectionRepositoryProvider) {
    return new TerminalRepositoryImpl_Factory(pcConnectionRepositoryProvider);
  }

  public static TerminalRepositoryImpl newInstance(PcConnectionRepository pcConnectionRepository) {
    return new TerminalRepositoryImpl(pcConnectionRepository);
  }
}
