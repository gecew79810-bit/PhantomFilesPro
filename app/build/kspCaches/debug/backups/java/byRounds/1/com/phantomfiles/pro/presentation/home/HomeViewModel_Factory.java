package com.phantomfiles.pro.presentation.home;

import com.phantomfiles.pro.data.repository.FileRepository;
import com.phantomfiles.pro.data.repository.RecycleBinRepository;
import com.phantomfiles.pro.data.repository.ScanRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<FileRepository> fileRepositoryProvider;

  private final Provider<RecycleBinRepository> recycleBinRepositoryProvider;

  private final Provider<ScanRepository> scanRepositoryProvider;

  public HomeViewModel_Factory(Provider<FileRepository> fileRepositoryProvider,
      Provider<RecycleBinRepository> recycleBinRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider) {
    this.fileRepositoryProvider = fileRepositoryProvider;
    this.recycleBinRepositoryProvider = recycleBinRepositoryProvider;
    this.scanRepositoryProvider = scanRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(fileRepositoryProvider.get(), recycleBinRepositoryProvider.get(), scanRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<FileRepository> fileRepositoryProvider,
      Provider<RecycleBinRepository> recycleBinRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider) {
    return new HomeViewModel_Factory(fileRepositoryProvider, recycleBinRepositoryProvider, scanRepositoryProvider);
  }

  public static HomeViewModel newInstance(FileRepository fileRepository,
      RecycleBinRepository recycleBinRepository, ScanRepository scanRepository) {
    return new HomeViewModel(fileRepository, recycleBinRepository, scanRepository);
  }
}
