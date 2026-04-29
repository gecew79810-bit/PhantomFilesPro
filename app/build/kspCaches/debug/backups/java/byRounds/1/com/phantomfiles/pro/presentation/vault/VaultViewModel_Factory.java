package com.phantomfiles.pro.presentation.vault;

import com.phantomfiles.pro.data.repository.SettingsRepository;
import com.phantomfiles.pro.data.repository.VaultRepository;
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
public final class VaultViewModel_Factory implements Factory<VaultViewModel> {
  private final Provider<VaultRepository> vaultRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public VaultViewModel_Factory(Provider<VaultRepository> vaultRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.vaultRepositoryProvider = vaultRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public VaultViewModel get() {
    return newInstance(vaultRepositoryProvider.get(), settingsRepositoryProvider.get());
  }

  public static VaultViewModel_Factory create(Provider<VaultRepository> vaultRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new VaultViewModel_Factory(vaultRepositoryProvider, settingsRepositoryProvider);
  }

  public static VaultViewModel newInstance(VaultRepository vaultRepository,
      SettingsRepository settingsRepository) {
    return new VaultViewModel(vaultRepository, settingsRepository);
  }
}
