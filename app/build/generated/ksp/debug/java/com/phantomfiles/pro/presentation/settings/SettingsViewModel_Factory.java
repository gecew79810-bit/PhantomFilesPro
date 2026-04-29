package com.phantomfiles.pro.presentation.settings;

import com.phantomfiles.pro.data.repository.SettingsRepository;
import com.phantomfiles.pro.data.repository.ShizukuRepository;
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
    "KotlinInternalInJava",
    "cast"
})
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<ShizukuRepository> shizukuRepositoryProvider;

  public SettingsViewModel_Factory(Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<ShizukuRepository> shizukuRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.shizukuRepositoryProvider = shizukuRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(settingsRepositoryProvider.get(), shizukuRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<ShizukuRepository> shizukuRepositoryProvider) {
    return new SettingsViewModel_Factory(settingsRepositoryProvider, shizukuRepositoryProvider);
  }

  public static SettingsViewModel newInstance(SettingsRepository settingsRepository,
      ShizukuRepository shizukuRepository) {
    return new SettingsViewModel(settingsRepository, shizukuRepository);
  }
}
