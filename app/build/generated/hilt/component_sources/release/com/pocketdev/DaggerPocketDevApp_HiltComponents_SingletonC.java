package com.pocketdev;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.pocketdev.data.build.GradleExecutor;
import com.pocketdev.data.di.BuildModule_Companion_ProvideGradleExecutorFactory;
import com.pocketdev.data.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.pocketdev.data.di.DatabaseModule_ProvideConversationDaoFactory;
import com.pocketdev.data.di.DatabaseModule_ProvideMessageDaoFactory;
import com.pocketdev.data.di.NetworkModule_ProvideAnthropicApiFactory;
import com.pocketdev.data.di.NetworkModule_ProvideGeminiApiFactory;
import com.pocketdev.data.di.NetworkModule_ProvideGitHubApiFactory;
import com.pocketdev.data.di.NetworkModule_ProvideGitHubTokenProviderFactory;
import com.pocketdev.data.di.NetworkModule_ProvideLlmApiFactory;
import com.pocketdev.data.di.NetworkModule_ProvideOkHttpClientFactory;
import com.pocketdev.data.di.NetworkModule_ProvideOkHttpClientForCollaborationFactory;
import com.pocketdev.data.di.NetworkModule_ProvideOllamaApiFactory;
import com.pocketdev.data.local.UserSettingsDataStore;
import com.pocketdev.data.local.database.AppDatabase;
import com.pocketdev.data.local.database.ConversationDao;
import com.pocketdev.data.local.database.MessageDao;
import com.pocketdev.data.remote.CollaborationWebSocket;
import com.pocketdev.data.remote.api.AnthropicApi;
import com.pocketdev.data.remote.api.GeminiApi;
import com.pocketdev.data.remote.api.GitHubApi;
import com.pocketdev.data.remote.api.LlmApi;
import com.pocketdev.data.remote.api.OllamaApi;
import com.pocketdev.data.remote.interceptor.DynamicHostInterceptor;
import com.pocketdev.data.repository.AiRepositoryImpl;
import com.pocketdev.data.repository.BuildRepositoryImpl;
import com.pocketdev.data.repository.CollaborationRepositoryImpl;
import com.pocketdev.data.repository.ConversationRepositoryImpl;
import com.pocketdev.data.repository.FileRepositoryImpl;
import com.pocketdev.data.repository.GitHubRepositoryImpl;
import com.pocketdev.data.repository.LlmRepositoryImpl;
import com.pocketdev.data.repository.OllamaRepositoryImpl;
import com.pocketdev.data.repository.PcConnectionRepositoryImpl;
import com.pocketdev.data.repository.RemoteRepositoryImpl;
import com.pocketdev.data.repository.TerminalRepositoryImpl;
import com.pocketdev.data.repository.UserSettingsRepositoryImpl;
import com.pocketdev.domain.usecase.AddPcConnectionUseCase;
import com.pocketdev.domain.usecase.AuthenticateGitHubUseCase;
import com.pocketdev.domain.usecase.CommitFileUseCase;
import com.pocketdev.domain.usecase.DeleteOllamaModelUseCase;
import com.pocketdev.domain.usecase.GetChatCompletionUseCase;
import com.pocketdev.domain.usecase.GetOllamaModelsUseCase;
import com.pocketdev.domain.usecase.GetPcConnectionsUseCase;
import com.pocketdev.domain.usecase.GetReposUseCase;
import com.pocketdev.domain.usecase.ListFilesUseCase;
import com.pocketdev.domain.usecase.OpenFolderUseCase;
import com.pocketdev.domain.usecase.PullOllamaModelUseCase;
import com.pocketdev.domain.usecase.ReadFileUseCase;
import com.pocketdev.domain.usecase.RemovePcConnectionUseCase;
import com.pocketdev.domain.usecase.SaveFileUseCase;
import com.pocketdev.domain.usecase.SetActivePcConnectionUseCase;
import com.pocketdev.domain.usecase.TestPcConnectionUseCase;
import com.pocketdev.ui.screens.build.BuildViewModel;
import com.pocketdev.ui.screens.build.BuildViewModel_HiltModules_KeyModule_ProvideFactory;
import com.pocketdev.ui.screens.chat.ChatViewModel;
import com.pocketdev.ui.screens.chat.ChatViewModel_HiltModules_KeyModule_ProvideFactory;
import com.pocketdev.ui.screens.editor.CollaborationViewModel;
import com.pocketdev.ui.screens.editor.CollaborationViewModel_HiltModules_KeyModule_ProvideFactory;
import com.pocketdev.ui.screens.editor.EditorViewModel;
import com.pocketdev.ui.screens.editor.EditorViewModel_HiltModules_KeyModule_ProvideFactory;
import com.pocketdev.ui.screens.editor.RemoteEditorViewModel;
import com.pocketdev.ui.screens.editor.RemoteEditorViewModel_HiltModules_KeyModule_ProvideFactory;
import com.pocketdev.ui.screens.ollama.OllamaViewModel;
import com.pocketdev.ui.screens.ollama.OllamaViewModel_HiltModules_KeyModule_ProvideFactory;
import com.pocketdev.ui.screens.pc.PcConnectionViewModel;
import com.pocketdev.ui.screens.pc.PcConnectionViewModel_HiltModules_KeyModule_ProvideFactory;
import com.pocketdev.ui.screens.repos.RepoDetailViewModel;
import com.pocketdev.ui.screens.repos.RepoDetailViewModel_HiltModules_KeyModule_ProvideFactory;
import com.pocketdev.ui.screens.repos.ReposViewModel;
import com.pocketdev.ui.screens.repos.ReposViewModel_HiltModules_KeyModule_ProvideFactory;
import com.pocketdev.ui.screens.settings.SettingsViewModel;
import com.pocketdev.ui.screens.settings.SettingsViewModel_HiltModules_KeyModule_ProvideFactory;
import com.pocketdev.ui.screens.terminal.TerminalViewModel;
import com.pocketdev.ui.screens.terminal.TerminalViewModel_HiltModules_KeyModule_ProvideFactory;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SetBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlin.jvm.functions.Function0;
import okhttp3.OkHttpClient;

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
public final class DaggerPocketDevApp_HiltComponents_SingletonC {
  private DaggerPocketDevApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public PocketDevApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements PocketDevApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public PocketDevApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements PocketDevApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public PocketDevApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements PocketDevApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public PocketDevApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements PocketDevApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PocketDevApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements PocketDevApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PocketDevApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements PocketDevApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public PocketDevApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements PocketDevApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public PocketDevApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends PocketDevApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends PocketDevApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends PocketDevApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends PocketDevApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Set<String> getViewModelKeys() {
      return SetBuilder.<String>newSetBuilder(11).add(BuildViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(ChatViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(CollaborationViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(EditorViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(OllamaViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(PcConnectionViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(RemoteEditorViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(RepoDetailViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(ReposViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(SettingsViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(TerminalViewModel_HiltModules_KeyModule_ProvideFactory.provide()).build();
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends PocketDevApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<BuildViewModel> buildViewModelProvider;

    private Provider<ChatViewModel> chatViewModelProvider;

    private Provider<CollaborationViewModel> collaborationViewModelProvider;

    private Provider<EditorViewModel> editorViewModelProvider;

    private Provider<OllamaViewModel> ollamaViewModelProvider;

    private Provider<PcConnectionViewModel> pcConnectionViewModelProvider;

    private Provider<RemoteEditorViewModel> remoteEditorViewModelProvider;

    private Provider<RepoDetailViewModel> repoDetailViewModelProvider;

    private Provider<ReposViewModel> reposViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<TerminalViewModel> terminalViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private GetChatCompletionUseCase getChatCompletionUseCase() {
      return new GetChatCompletionUseCase(singletonCImpl.llmRepositoryImplProvider.get());
    }

    private CommitFileUseCase commitFileUseCase() {
      return new CommitFileUseCase(singletonCImpl.gitHubRepositoryImplProvider.get());
    }

    private OpenFolderUseCase openFolderUseCase() {
      return new OpenFolderUseCase(singletonCImpl.fileRepositoryImplProvider.get());
    }

    private ListFilesUseCase listFilesUseCase() {
      return new ListFilesUseCase(singletonCImpl.fileRepositoryImplProvider.get());
    }

    private ReadFileUseCase readFileUseCase() {
      return new ReadFileUseCase(singletonCImpl.fileRepositoryImplProvider.get());
    }

    private SaveFileUseCase saveFileUseCase() {
      return new SaveFileUseCase(singletonCImpl.fileRepositoryImplProvider.get());
    }

    private GetOllamaModelsUseCase getOllamaModelsUseCase() {
      return new GetOllamaModelsUseCase(singletonCImpl.ollamaRepositoryImplProvider.get());
    }

    private PullOllamaModelUseCase pullOllamaModelUseCase() {
      return new PullOllamaModelUseCase(singletonCImpl.ollamaRepositoryImplProvider.get());
    }

    private DeleteOllamaModelUseCase deleteOllamaModelUseCase() {
      return new DeleteOllamaModelUseCase(singletonCImpl.ollamaRepositoryImplProvider.get());
    }

    private GetPcConnectionsUseCase getPcConnectionsUseCase() {
      return new GetPcConnectionsUseCase(singletonCImpl.pcConnectionRepositoryImplProvider.get());
    }

    private AddPcConnectionUseCase addPcConnectionUseCase() {
      return new AddPcConnectionUseCase(singletonCImpl.pcConnectionRepositoryImplProvider.get());
    }

    private RemovePcConnectionUseCase removePcConnectionUseCase() {
      return new RemovePcConnectionUseCase(singletonCImpl.pcConnectionRepositoryImplProvider.get());
    }

    private SetActivePcConnectionUseCase setActivePcConnectionUseCase() {
      return new SetActivePcConnectionUseCase(singletonCImpl.pcConnectionRepositoryImplProvider.get());
    }

    private TestPcConnectionUseCase testPcConnectionUseCase() {
      return new TestPcConnectionUseCase(singletonCImpl.pcConnectionRepositoryImplProvider.get());
    }

    private GetReposUseCase getReposUseCase() {
      return new GetReposUseCase(singletonCImpl.gitHubRepositoryImplProvider.get());
    }

    private AuthenticateGitHubUseCase authenticateGitHubUseCase() {
      return new AuthenticateGitHubUseCase(singletonCImpl.gitHubRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.buildViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.chatViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.collaborationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.editorViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.ollamaViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.pcConnectionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.remoteEditorViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.repoDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.reposViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.terminalViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
    }

    @Override
    public Map<String, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(11).put("com.pocketdev.ui.screens.build.BuildViewModel", ((Provider) buildViewModelProvider)).put("com.pocketdev.ui.screens.chat.ChatViewModel", ((Provider) chatViewModelProvider)).put("com.pocketdev.ui.screens.editor.CollaborationViewModel", ((Provider) collaborationViewModelProvider)).put("com.pocketdev.ui.screens.editor.EditorViewModel", ((Provider) editorViewModelProvider)).put("com.pocketdev.ui.screens.ollama.OllamaViewModel", ((Provider) ollamaViewModelProvider)).put("com.pocketdev.ui.screens.pc.PcConnectionViewModel", ((Provider) pcConnectionViewModelProvider)).put("com.pocketdev.ui.screens.editor.RemoteEditorViewModel", ((Provider) remoteEditorViewModelProvider)).put("com.pocketdev.ui.screens.repos.RepoDetailViewModel", ((Provider) repoDetailViewModelProvider)).put("com.pocketdev.ui.screens.repos.ReposViewModel", ((Provider) reposViewModelProvider)).put("com.pocketdev.ui.screens.settings.SettingsViewModel", ((Provider) settingsViewModelProvider)).put("com.pocketdev.ui.screens.terminal.TerminalViewModel", ((Provider) terminalViewModelProvider)).build();
    }

    @Override
    public Map<String, Object> getHiltViewModelAssistedMap() {
      return Collections.<String, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.pocketdev.ui.screens.build.BuildViewModel 
          return (T) new BuildViewModel(singletonCImpl.buildRepositoryImplProvider.get());

          case 1: // com.pocketdev.ui.screens.chat.ChatViewModel 
          return (T) new ChatViewModel(viewModelCImpl.getChatCompletionUseCase(), viewModelCImpl.commitFileUseCase(), singletonCImpl.userSettingsRepositoryImplProvider.get(), singletonCImpl.aiRepositoryImplProvider.get(), singletonCImpl.conversationRepositoryImplProvider.get());

          case 2: // com.pocketdev.ui.screens.editor.CollaborationViewModel 
          return (T) new CollaborationViewModel(singletonCImpl.collaborationRepositoryImplProvider.get());

          case 3: // com.pocketdev.ui.screens.editor.EditorViewModel 
          return (T) new EditorViewModel(viewModelCImpl.openFolderUseCase(), viewModelCImpl.listFilesUseCase(), viewModelCImpl.readFileUseCase(), viewModelCImpl.saveFileUseCase(), singletonCImpl.fileRepositoryImplProvider.get());

          case 4: // com.pocketdev.ui.screens.ollama.OllamaViewModel 
          return (T) new OllamaViewModel(viewModelCImpl.getOllamaModelsUseCase(), viewModelCImpl.pullOllamaModelUseCase(), viewModelCImpl.deleteOllamaModelUseCase(), singletonCImpl.ollamaRepositoryImplProvider.get());

          case 5: // com.pocketdev.ui.screens.pc.PcConnectionViewModel 
          return (T) new PcConnectionViewModel(viewModelCImpl.getPcConnectionsUseCase(), viewModelCImpl.addPcConnectionUseCase(), viewModelCImpl.removePcConnectionUseCase(), viewModelCImpl.setActivePcConnectionUseCase(), viewModelCImpl.testPcConnectionUseCase());

          case 6: // com.pocketdev.ui.screens.editor.RemoteEditorViewModel 
          return (T) new RemoteEditorViewModel(singletonCImpl.remoteRepositoryImplProvider.get(), viewModelCImpl.savedStateHandle);

          case 7: // com.pocketdev.ui.screens.repos.RepoDetailViewModel 
          return (T) new RepoDetailViewModel(singletonCImpl.remoteRepositoryImplProvider.get(), viewModelCImpl.savedStateHandle);

          case 8: // com.pocketdev.ui.screens.repos.ReposViewModel 
          return (T) new ReposViewModel(viewModelCImpl.getReposUseCase(), viewModelCImpl.authenticateGitHubUseCase());

          case 9: // com.pocketdev.ui.screens.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.userSettingsRepositoryImplProvider.get());

          case 10: // com.pocketdev.ui.screens.terminal.TerminalViewModel 
          return (T) new TerminalViewModel(singletonCImpl.terminalRepositoryImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends PocketDevApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends PocketDevApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends PocketDevApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<GradleExecutor> provideGradleExecutorProvider;

    private Provider<BuildRepositoryImpl> buildRepositoryImplProvider;

    private Provider<UserSettingsDataStore> userSettingsDataStoreProvider;

    private Provider<DynamicHostInterceptor> dynamicHostInterceptorProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<LlmApi> provideLlmApiProvider;

    private Provider<LlmRepositoryImpl> llmRepositoryImplProvider;

    private Provider<Function0<String>> provideGitHubTokenProvider;

    private Provider<GitHubApi> provideGitHubApiProvider;

    private Provider<GitHubRepositoryImpl> gitHubRepositoryImplProvider;

    private Provider<UserSettingsRepositoryImpl> userSettingsRepositoryImplProvider;

    private Provider<AnthropicApi> provideAnthropicApiProvider;

    private Provider<GeminiApi> provideGeminiApiProvider;

    private Provider<OllamaApi> provideOllamaApiProvider;

    private Provider<AiRepositoryImpl> aiRepositoryImplProvider;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<ConversationDao> provideConversationDaoProvider;

    private Provider<MessageDao> provideMessageDaoProvider;

    private Provider<ConversationRepositoryImpl> conversationRepositoryImplProvider;

    private Provider<OkHttpClient> provideOkHttpClientForCollaborationProvider;

    private Provider<CollaborationWebSocket> collaborationWebSocketProvider;

    private Provider<CollaborationRepositoryImpl> collaborationRepositoryImplProvider;

    private Provider<FileRepositoryImpl> fileRepositoryImplProvider;

    private Provider<OllamaRepositoryImpl> ollamaRepositoryImplProvider;

    private Provider<PcConnectionRepositoryImpl> pcConnectionRepositoryImplProvider;

    private Provider<RemoteRepositoryImpl> remoteRepositoryImplProvider;

    private Provider<TerminalRepositoryImpl> terminalRepositoryImplProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideGradleExecutorProvider = DoubleCheck.provider(new SwitchingProvider<GradleExecutor>(singletonCImpl, 1));
      this.buildRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<BuildRepositoryImpl>(singletonCImpl, 0));
      this.userSettingsDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<UserSettingsDataStore>(singletonCImpl, 6));
      this.dynamicHostInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<DynamicHostInterceptor>(singletonCImpl, 5));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 4));
      this.provideLlmApiProvider = DoubleCheck.provider(new SwitchingProvider<LlmApi>(singletonCImpl, 3));
      this.llmRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<LlmRepositoryImpl>(singletonCImpl, 2));
      this.provideGitHubTokenProvider = DoubleCheck.provider(new SwitchingProvider<Function0<String>>(singletonCImpl, 9));
      this.provideGitHubApiProvider = DoubleCheck.provider(new SwitchingProvider<GitHubApi>(singletonCImpl, 8));
      this.gitHubRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<GitHubRepositoryImpl>(singletonCImpl, 7));
      this.userSettingsRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<UserSettingsRepositoryImpl>(singletonCImpl, 10));
      this.provideAnthropicApiProvider = DoubleCheck.provider(new SwitchingProvider<AnthropicApi>(singletonCImpl, 12));
      this.provideGeminiApiProvider = DoubleCheck.provider(new SwitchingProvider<GeminiApi>(singletonCImpl, 13));
      this.provideOllamaApiProvider = DoubleCheck.provider(new SwitchingProvider<OllamaApi>(singletonCImpl, 14));
      this.aiRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AiRepositoryImpl>(singletonCImpl, 11));
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 17));
      this.provideConversationDaoProvider = DoubleCheck.provider(new SwitchingProvider<ConversationDao>(singletonCImpl, 16));
      this.provideMessageDaoProvider = DoubleCheck.provider(new SwitchingProvider<MessageDao>(singletonCImpl, 18));
      this.conversationRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ConversationRepositoryImpl>(singletonCImpl, 15));
      this.provideOkHttpClientForCollaborationProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 21));
      this.collaborationWebSocketProvider = DoubleCheck.provider(new SwitchingProvider<CollaborationWebSocket>(singletonCImpl, 20));
      this.collaborationRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<CollaborationRepositoryImpl>(singletonCImpl, 19));
      this.fileRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<FileRepositoryImpl>(singletonCImpl, 22));
      this.ollamaRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<OllamaRepositoryImpl>(singletonCImpl, 23));
      this.pcConnectionRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<PcConnectionRepositoryImpl>(singletonCImpl, 24));
      this.remoteRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<RemoteRepositoryImpl>(singletonCImpl, 25));
      this.terminalRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<TerminalRepositoryImpl>(singletonCImpl, 26));
    }

    @Override
    public void injectPocketDevApp(PocketDevApp pocketDevApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.pocketdev.data.repository.BuildRepositoryImpl 
          return (T) new BuildRepositoryImpl(singletonCImpl.provideGradleExecutorProvider.get());

          case 1: // com.pocketdev.data.build.GradleExecutor 
          return (T) BuildModule_Companion_ProvideGradleExecutorFactory.provideGradleExecutor();

          case 2: // com.pocketdev.data.repository.LlmRepositoryImpl 
          return (T) new LlmRepositoryImpl(singletonCImpl.provideLlmApiProvider.get());

          case 3: // com.pocketdev.data.remote.api.LlmApi 
          return (T) NetworkModule_ProvideLlmApiFactory.provideLlmApi(singletonCImpl.provideOkHttpClientProvider.get());

          case 4: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.dynamicHostInterceptorProvider.get());

          case 5: // com.pocketdev.data.remote.interceptor.DynamicHostInterceptor 
          return (T) new DynamicHostInterceptor(singletonCImpl.userSettingsDataStoreProvider.get());

          case 6: // com.pocketdev.data.local.UserSettingsDataStore 
          return (T) new UserSettingsDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.pocketdev.data.repository.GitHubRepositoryImpl 
          return (T) new GitHubRepositoryImpl(singletonCImpl.provideGitHubApiProvider.get(), singletonCImpl.userSettingsDataStoreProvider.get(), singletonCImpl.provideGitHubTokenProvider.get());

          case 8: // com.pocketdev.data.remote.api.GitHubApi 
          return (T) NetworkModule_ProvideGitHubApiFactory.provideGitHubApi(singletonCImpl.userSettingsDataStoreProvider.get(), singletonCImpl.provideGitHubTokenProvider.get());

          case 9: // @javax.inject.Named("github_token_provider") kotlin.jvm.functions.Function0<java.lang.String> 
          return (T) NetworkModule_ProvideGitHubTokenProviderFactory.provideGitHubTokenProvider(singletonCImpl.userSettingsDataStoreProvider.get());

          case 10: // com.pocketdev.data.repository.UserSettingsRepositoryImpl 
          return (T) new UserSettingsRepositoryImpl(singletonCImpl.userSettingsDataStoreProvider.get());

          case 11: // com.pocketdev.data.repository.AiRepositoryImpl 
          return (T) new AiRepositoryImpl(singletonCImpl.provideLlmApiProvider.get(), singletonCImpl.provideAnthropicApiProvider.get(), singletonCImpl.provideGeminiApiProvider.get(), singletonCImpl.provideOllamaApiProvider.get());

          case 12: // com.pocketdev.data.remote.api.AnthropicApi 
          return (T) NetworkModule_ProvideAnthropicApiFactory.provideAnthropicApi();

          case 13: // com.pocketdev.data.remote.api.GeminiApi 
          return (T) NetworkModule_ProvideGeminiApiFactory.provideGeminiApi();

          case 14: // com.pocketdev.data.remote.api.OllamaApi 
          return (T) NetworkModule_ProvideOllamaApiFactory.provideOllamaApi();

          case 15: // com.pocketdev.data.repository.ConversationRepositoryImpl 
          return (T) new ConversationRepositoryImpl(singletonCImpl.provideConversationDaoProvider.get(), singletonCImpl.provideMessageDaoProvider.get());

          case 16: // com.pocketdev.data.local.database.ConversationDao 
          return (T) DatabaseModule_ProvideConversationDaoFactory.provideConversationDao(singletonCImpl.provideAppDatabaseProvider.get());

          case 17: // com.pocketdev.data.local.database.AppDatabase 
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 18: // com.pocketdev.data.local.database.MessageDao 
          return (T) DatabaseModule_ProvideMessageDaoFactory.provideMessageDao(singletonCImpl.provideAppDatabaseProvider.get());

          case 19: // com.pocketdev.data.repository.CollaborationRepositoryImpl 
          return (T) new CollaborationRepositoryImpl(singletonCImpl.collaborationWebSocketProvider.get());

          case 20: // com.pocketdev.data.remote.CollaborationWebSocket 
          return (T) new CollaborationWebSocket(singletonCImpl.provideOkHttpClientForCollaborationProvider.get());

          case 21: // @javax.inject.Named("collaboration") okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientForCollaborationFactory.provideOkHttpClientForCollaboration();

          case 22: // com.pocketdev.data.repository.FileRepositoryImpl 
          return (T) new FileRepositoryImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 23: // com.pocketdev.data.repository.OllamaRepositoryImpl 
          return (T) new OllamaRepositoryImpl(singletonCImpl.userSettingsDataStoreProvider.get());

          case 24: // com.pocketdev.data.repository.PcConnectionRepositoryImpl 
          return (T) new PcConnectionRepositoryImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 25: // com.pocketdev.data.repository.RemoteRepositoryImpl 
          return (T) new RemoteRepositoryImpl(singletonCImpl.gitHubRepositoryImplProvider.get());

          case 26: // com.pocketdev.data.repository.TerminalRepositoryImpl 
          return (T) new TerminalRepositoryImpl(singletonCImpl.pcConnectionRepositoryImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
