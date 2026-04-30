package com.pocketdev.ui.screens.pc;

import com.pocketdev.domain.usecase.AddPcConnectionUseCase;
import com.pocketdev.domain.usecase.GetPcConnectionsUseCase;
import com.pocketdev.domain.usecase.RemovePcConnectionUseCase;
import com.pocketdev.domain.usecase.SetActivePcConnectionUseCase;
import com.pocketdev.domain.usecase.TestPcConnectionUseCase;
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
public final class PcConnectionViewModel_Factory implements Factory<PcConnectionViewModel> {
  private final Provider<GetPcConnectionsUseCase> getPcConnectionsProvider;

  private final Provider<AddPcConnectionUseCase> addPcConnectionProvider;

  private final Provider<RemovePcConnectionUseCase> removePcConnectionProvider;

  private final Provider<SetActivePcConnectionUseCase> setActivePcConnectionProvider;

  private final Provider<TestPcConnectionUseCase> testPcConnectionProvider;

  public PcConnectionViewModel_Factory(Provider<GetPcConnectionsUseCase> getPcConnectionsProvider,
      Provider<AddPcConnectionUseCase> addPcConnectionProvider,
      Provider<RemovePcConnectionUseCase> removePcConnectionProvider,
      Provider<SetActivePcConnectionUseCase> setActivePcConnectionProvider,
      Provider<TestPcConnectionUseCase> testPcConnectionProvider) {
    this.getPcConnectionsProvider = getPcConnectionsProvider;
    this.addPcConnectionProvider = addPcConnectionProvider;
    this.removePcConnectionProvider = removePcConnectionProvider;
    this.setActivePcConnectionProvider = setActivePcConnectionProvider;
    this.testPcConnectionProvider = testPcConnectionProvider;
  }

  @Override
  public PcConnectionViewModel get() {
    return newInstance(getPcConnectionsProvider.get(), addPcConnectionProvider.get(), removePcConnectionProvider.get(), setActivePcConnectionProvider.get(), testPcConnectionProvider.get());
  }

  public static PcConnectionViewModel_Factory create(
      Provider<GetPcConnectionsUseCase> getPcConnectionsProvider,
      Provider<AddPcConnectionUseCase> addPcConnectionProvider,
      Provider<RemovePcConnectionUseCase> removePcConnectionProvider,
      Provider<SetActivePcConnectionUseCase> setActivePcConnectionProvider,
      Provider<TestPcConnectionUseCase> testPcConnectionProvider) {
    return new PcConnectionViewModel_Factory(getPcConnectionsProvider, addPcConnectionProvider, removePcConnectionProvider, setActivePcConnectionProvider, testPcConnectionProvider);
  }

  public static PcConnectionViewModel newInstance(GetPcConnectionsUseCase getPcConnections,
      AddPcConnectionUseCase addPcConnection, RemovePcConnectionUseCase removePcConnection,
      SetActivePcConnectionUseCase setActivePcConnection,
      TestPcConnectionUseCase testPcConnection) {
    return new PcConnectionViewModel(getPcConnections, addPcConnection, removePcConnection, setActivePcConnection, testPcConnection);
  }
}
