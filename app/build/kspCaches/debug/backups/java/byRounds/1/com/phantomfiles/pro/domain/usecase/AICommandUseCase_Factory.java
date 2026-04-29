package com.phantomfiles.pro.domain.usecase;

import com.phantomfiles.pro.data.remote.GroqApi;
import com.phantomfiles.pro.data.repository.FileRepository;
import com.phantomfiles.pro.data.repository.ScanRepository;
import com.phantomfiles.pro.data.repository.SettingsRepository;
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
public final class AICommandUseCase_Factory implements Factory<AICommandUseCase> {
  private final Provider<FileRepository> fileRepositoryProvider;

  private final Provider<ScanRepository> scanRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<GroqApi> groqApiProvider;

  public AICommandUseCase_Factory(Provider<FileRepository> fileRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<GroqApi> groqApiProvider) {
    this.fileRepositoryProvider = fileRepositoryProvider;
    this.scanRepositoryProvider = scanRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.groqApiProvider = groqApiProvider;
  }

  @Override
  public AICommandUseCase get() {
    return newInstance(fileRepositoryProvider.get(), scanRepositoryProvider.get(), settingsRepositoryProvider.get(), groqApiProvider.get());
  }

  public static AICommandUseCase_Factory create(Provider<FileRepository> fileRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<GroqApi> groqApiProvider) {
    return new AICommandUseCase_Factory(fileRepositoryProvider, scanRepositoryProvider, settingsRepositoryProvider, groqApiProvider);
  }

  public static AICommandUseCase newInstance(FileRepository fileRepository,
      ScanRepository scanRepository, SettingsRepository settingsRepository, GroqApi groqApi) {
    return new AICommandUseCase(fileRepository, scanRepository, settingsRepository, groqApi);
  }
}
