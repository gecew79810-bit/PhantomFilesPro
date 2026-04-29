package com.phantomfiles.pro;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.phantomfiles.pro.data.local.BookmarkDao;
import com.phantomfiles.pro.data.local.OperationLogDao;
import com.phantomfiles.pro.data.local.PhantomDatabase;
import com.phantomfiles.pro.data.local.RecycleBinDao;
import com.phantomfiles.pro.data.local.ScanResultDao;
import com.phantomfiles.pro.data.local.VaultDao;
import com.phantomfiles.pro.data.remote.GroqApi;
import com.phantomfiles.pro.data.repository.AppManagerRepository;
import com.phantomfiles.pro.data.repository.FileRepository;
import com.phantomfiles.pro.data.repository.RecycleBinRepository;
import com.phantomfiles.pro.data.repository.ScanRepository;
import com.phantomfiles.pro.data.repository.SettingsRepository;
import com.phantomfiles.pro.data.repository.ShizukuRepository;
import com.phantomfiles.pro.data.repository.VaultRepository;
import com.phantomfiles.pro.di.AppModule_ProvideBookmarkDaoFactory;
import com.phantomfiles.pro.di.AppModule_ProvideDatabaseFactory;
import com.phantomfiles.pro.di.AppModule_ProvideGroqApiFactory;
import com.phantomfiles.pro.di.AppModule_ProvideOkHttpClientFactory;
import com.phantomfiles.pro.di.AppModule_ProvideOperationLogDaoFactory;
import com.phantomfiles.pro.di.AppModule_ProvideRecycleBinDaoFactory;
import com.phantomfiles.pro.di.AppModule_ProvideScanResultDaoFactory;
import com.phantomfiles.pro.di.AppModule_ProvideVaultDaoFactory;
import com.phantomfiles.pro.domain.usecase.AICommandUseCase;
import com.phantomfiles.pro.presentation.ai.AIViewModel;
import com.phantomfiles.pro.presentation.ai.AIViewModel_HiltModules;
import com.phantomfiles.pro.presentation.appmanager.AppManagerViewModel;
import com.phantomfiles.pro.presentation.appmanager.AppManagerViewModel_HiltModules;
import com.phantomfiles.pro.presentation.files.FilesViewModel;
import com.phantomfiles.pro.presentation.files.FilesViewModel_HiltModules;
import com.phantomfiles.pro.presentation.home.HomeViewModel;
import com.phantomfiles.pro.presentation.home.HomeViewModel_HiltModules;
import com.phantomfiles.pro.presentation.recycle.RecycleViewModel;
import com.phantomfiles.pro.presentation.recycle.RecycleViewModel_HiltModules;
import com.phantomfiles.pro.presentation.scanner.ScannerViewModel;
import com.phantomfiles.pro.presentation.scanner.ScannerViewModel_HiltModules;
import com.phantomfiles.pro.presentation.settings.SettingsViewModel;
import com.phantomfiles.pro.presentation.settings.SettingsViewModel_HiltModules;
import com.phantomfiles.pro.presentation.vault.VaultViewModel;
import com.phantomfiles.pro.presentation.vault.VaultViewModel_HiltModules;
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
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
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
    "KotlinInternalInJava",
    "cast"
})
public final class DaggerPhantomApp_HiltComponents_SingletonC {
  private DaggerPhantomApp_HiltComponents_SingletonC() {
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

    public PhantomApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements PhantomApp_HiltComponents.ActivityRetainedC.Builder {
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
    public PhantomApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements PhantomApp_HiltComponents.ActivityC.Builder {
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
    public PhantomApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements PhantomApp_HiltComponents.FragmentC.Builder {
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
    public PhantomApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements PhantomApp_HiltComponents.ViewWithFragmentC.Builder {
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
    public PhantomApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements PhantomApp_HiltComponents.ViewC.Builder {
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
    public PhantomApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements PhantomApp_HiltComponents.ViewModelC.Builder {
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
    public PhantomApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements PhantomApp_HiltComponents.ServiceC.Builder {
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
    public PhantomApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends PhantomApp_HiltComponents.ViewWithFragmentC {
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

  private static final class FragmentCImpl extends PhantomApp_HiltComponents.FragmentC {
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

  private static final class ViewCImpl extends PhantomApp_HiltComponents.ViewC {
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

  private static final class ActivityCImpl extends PhantomApp_HiltComponents.ActivityC {
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
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(8).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_ai_AIViewModel, AIViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_appmanager_AppManagerViewModel, AppManagerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_files_FilesViewModel, FilesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_recycle_RecycleViewModel, RecycleViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_scanner_ScannerViewModel, ScannerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_vault_VaultViewModel, VaultViewModel_HiltModules.KeyModule.provide()).build());
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

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_phantomfiles_pro_presentation_home_HomeViewModel = "com.phantomfiles.pro.presentation.home.HomeViewModel";

      static String com_phantomfiles_pro_presentation_recycle_RecycleViewModel = "com.phantomfiles.pro.presentation.recycle.RecycleViewModel";

      static String com_phantomfiles_pro_presentation_appmanager_AppManagerViewModel = "com.phantomfiles.pro.presentation.appmanager.AppManagerViewModel";

      static String com_phantomfiles_pro_presentation_vault_VaultViewModel = "com.phantomfiles.pro.presentation.vault.VaultViewModel";

      static String com_phantomfiles_pro_presentation_ai_AIViewModel = "com.phantomfiles.pro.presentation.ai.AIViewModel";

      static String com_phantomfiles_pro_presentation_files_FilesViewModel = "com.phantomfiles.pro.presentation.files.FilesViewModel";

      static String com_phantomfiles_pro_presentation_scanner_ScannerViewModel = "com.phantomfiles.pro.presentation.scanner.ScannerViewModel";

      static String com_phantomfiles_pro_presentation_settings_SettingsViewModel = "com.phantomfiles.pro.presentation.settings.SettingsViewModel";

      @KeepFieldType
      HomeViewModel com_phantomfiles_pro_presentation_home_HomeViewModel2;

      @KeepFieldType
      RecycleViewModel com_phantomfiles_pro_presentation_recycle_RecycleViewModel2;

      @KeepFieldType
      AppManagerViewModel com_phantomfiles_pro_presentation_appmanager_AppManagerViewModel2;

      @KeepFieldType
      VaultViewModel com_phantomfiles_pro_presentation_vault_VaultViewModel2;

      @KeepFieldType
      AIViewModel com_phantomfiles_pro_presentation_ai_AIViewModel2;

      @KeepFieldType
      FilesViewModel com_phantomfiles_pro_presentation_files_FilesViewModel2;

      @KeepFieldType
      ScannerViewModel com_phantomfiles_pro_presentation_scanner_ScannerViewModel2;

      @KeepFieldType
      SettingsViewModel com_phantomfiles_pro_presentation_settings_SettingsViewModel2;
    }
  }

  private static final class ViewModelCImpl extends PhantomApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AIViewModel> aIViewModelProvider;

    private Provider<AppManagerViewModel> appManagerViewModelProvider;

    private Provider<FilesViewModel> filesViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<RecycleViewModel> recycleViewModelProvider;

    private Provider<ScannerViewModel> scannerViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<VaultViewModel> vaultViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private AICommandUseCase aICommandUseCase() {
      return new AICommandUseCase(singletonCImpl.fileRepositoryProvider.get(), singletonCImpl.scanRepositoryProvider.get(), singletonCImpl.settingsRepositoryProvider.get(), singletonCImpl.provideGroqApiProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.aIViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.appManagerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.filesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.recycleViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.scannerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.vaultViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(8).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_ai_AIViewModel, ((Provider) aIViewModelProvider)).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_appmanager_AppManagerViewModel, ((Provider) appManagerViewModelProvider)).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_files_FilesViewModel, ((Provider) filesViewModelProvider)).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_recycle_RecycleViewModel, ((Provider) recycleViewModelProvider)).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_scanner_ScannerViewModel, ((Provider) scannerViewModelProvider)).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_phantomfiles_pro_presentation_vault_VaultViewModel, ((Provider) vaultViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_phantomfiles_pro_presentation_home_HomeViewModel = "com.phantomfiles.pro.presentation.home.HomeViewModel";

      static String com_phantomfiles_pro_presentation_recycle_RecycleViewModel = "com.phantomfiles.pro.presentation.recycle.RecycleViewModel";

      static String com_phantomfiles_pro_presentation_files_FilesViewModel = "com.phantomfiles.pro.presentation.files.FilesViewModel";

      static String com_phantomfiles_pro_presentation_vault_VaultViewModel = "com.phantomfiles.pro.presentation.vault.VaultViewModel";

      static String com_phantomfiles_pro_presentation_ai_AIViewModel = "com.phantomfiles.pro.presentation.ai.AIViewModel";

      static String com_phantomfiles_pro_presentation_settings_SettingsViewModel = "com.phantomfiles.pro.presentation.settings.SettingsViewModel";

      static String com_phantomfiles_pro_presentation_scanner_ScannerViewModel = "com.phantomfiles.pro.presentation.scanner.ScannerViewModel";

      static String com_phantomfiles_pro_presentation_appmanager_AppManagerViewModel = "com.phantomfiles.pro.presentation.appmanager.AppManagerViewModel";

      @KeepFieldType
      HomeViewModel com_phantomfiles_pro_presentation_home_HomeViewModel2;

      @KeepFieldType
      RecycleViewModel com_phantomfiles_pro_presentation_recycle_RecycleViewModel2;

      @KeepFieldType
      FilesViewModel com_phantomfiles_pro_presentation_files_FilesViewModel2;

      @KeepFieldType
      VaultViewModel com_phantomfiles_pro_presentation_vault_VaultViewModel2;

      @KeepFieldType
      AIViewModel com_phantomfiles_pro_presentation_ai_AIViewModel2;

      @KeepFieldType
      SettingsViewModel com_phantomfiles_pro_presentation_settings_SettingsViewModel2;

      @KeepFieldType
      ScannerViewModel com_phantomfiles_pro_presentation_scanner_ScannerViewModel2;

      @KeepFieldType
      AppManagerViewModel com_phantomfiles_pro_presentation_appmanager_AppManagerViewModel2;
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
          case 0: // com.phantomfiles.pro.presentation.ai.AIViewModel 
          return (T) new AIViewModel(viewModelCImpl.aICommandUseCase());

          case 1: // com.phantomfiles.pro.presentation.appmanager.AppManagerViewModel 
          return (T) new AppManagerViewModel(singletonCImpl.appManagerRepositoryProvider.get());

          case 2: // com.phantomfiles.pro.presentation.files.FilesViewModel 
          return (T) new FilesViewModel(singletonCImpl.fileRepositoryProvider.get(), singletonCImpl.recycleBinRepositoryProvider.get(), singletonCImpl.bookmarkDao(), singletonCImpl.operationLogDao());

          case 3: // com.phantomfiles.pro.presentation.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.fileRepositoryProvider.get(), singletonCImpl.recycleBinRepositoryProvider.get(), singletonCImpl.scanRepositoryProvider.get());

          case 4: // com.phantomfiles.pro.presentation.recycle.RecycleViewModel 
          return (T) new RecycleViewModel(singletonCImpl.recycleBinRepositoryProvider.get());

          case 5: // com.phantomfiles.pro.presentation.scanner.ScannerViewModel 
          return (T) new ScannerViewModel(singletonCImpl.fileRepositoryProvider.get(), singletonCImpl.scanRepositoryProvider.get());

          case 6: // com.phantomfiles.pro.presentation.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.settingsRepositoryProvider.get(), singletonCImpl.shizukuRepositoryProvider.get());

          case 7: // com.phantomfiles.pro.presentation.vault.VaultViewModel 
          return (T) new VaultViewModel(singletonCImpl.vaultRepositoryProvider.get(), singletonCImpl.settingsRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends PhantomApp_HiltComponents.ActivityRetainedC {
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

  private static final class ServiceCImpl extends PhantomApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends PhantomApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<FileRepository> fileRepositoryProvider;

    private Provider<PhantomDatabase> provideDatabaseProvider;

    private Provider<ScanRepository> scanRepositoryProvider;

    private Provider<SettingsRepository> settingsRepositoryProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<GroqApi> provideGroqApiProvider;

    private Provider<AppManagerRepository> appManagerRepositoryProvider;

    private Provider<RecycleBinRepository> recycleBinRepositoryProvider;

    private Provider<ShizukuRepository> shizukuRepositoryProvider;

    private Provider<VaultRepository> vaultRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private ScanResultDao scanResultDao() {
      return AppModule_ProvideScanResultDaoFactory.provideScanResultDao(provideDatabaseProvider.get());
    }

    private RecycleBinDao recycleBinDao() {
      return AppModule_ProvideRecycleBinDaoFactory.provideRecycleBinDao(provideDatabaseProvider.get());
    }

    private BookmarkDao bookmarkDao() {
      return AppModule_ProvideBookmarkDaoFactory.provideBookmarkDao(provideDatabaseProvider.get());
    }

    private OperationLogDao operationLogDao() {
      return AppModule_ProvideOperationLogDaoFactory.provideOperationLogDao(provideDatabaseProvider.get());
    }

    private VaultDao vaultDao() {
      return AppModule_ProvideVaultDaoFactory.provideVaultDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.fileRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FileRepository>(singletonCImpl, 0));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<PhantomDatabase>(singletonCImpl, 2));
      this.scanRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ScanRepository>(singletonCImpl, 1));
      this.settingsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SettingsRepository>(singletonCImpl, 3));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 5));
      this.provideGroqApiProvider = DoubleCheck.provider(new SwitchingProvider<GroqApi>(singletonCImpl, 4));
      this.appManagerRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AppManagerRepository>(singletonCImpl, 6));
      this.recycleBinRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RecycleBinRepository>(singletonCImpl, 7));
      this.shizukuRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ShizukuRepository>(singletonCImpl, 8));
      this.vaultRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<VaultRepository>(singletonCImpl, 9));
    }

    @Override
    public void injectPhantomApp(PhantomApp phantomApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
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
          case 0: // com.phantomfiles.pro.data.repository.FileRepository 
          return (T) new FileRepository();

          case 1: // com.phantomfiles.pro.data.repository.ScanRepository 
          return (T) new ScanRepository(singletonCImpl.scanResultDao());

          case 2: // com.phantomfiles.pro.data.local.PhantomDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.phantomfiles.pro.data.repository.SettingsRepository 
          return (T) new SettingsRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.phantomfiles.pro.data.remote.GroqApi 
          return (T) AppModule_ProvideGroqApiFactory.provideGroqApi(singletonCImpl.provideOkHttpClientProvider.get());

          case 5: // okhttp3.OkHttpClient 
          return (T) AppModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 6: // com.phantomfiles.pro.data.repository.AppManagerRepository 
          return (T) new AppManagerRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.phantomfiles.pro.data.repository.RecycleBinRepository 
          return (T) new RecycleBinRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.recycleBinDao());

          case 8: // com.phantomfiles.pro.data.repository.ShizukuRepository 
          return (T) new ShizukuRepository();

          case 9: // com.phantomfiles.pro.data.repository.VaultRepository 
          return (T) new VaultRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.vaultDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
