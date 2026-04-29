package com.phantomfiles.pro.presentation.scanner;

import com.phantomfiles.pro.data.repository.FileRepository;
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
public final class ScannerViewModel_Factory implements Factory<ScannerViewModel> {
  private final Provider<FileRepository> fileRepositoryProvider;

  private final Provider<ScanRepository> scanRepositoryProvider;

  public ScannerViewModel_Factory(Provider<FileRepository> fileRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider) {
    this.fileRepositoryProvider = fileRepositoryProvider;
    this.scanRepositoryProvider = scanRepositoryProvider;
  }

  @Override
  public ScannerViewModel get() {
    return newInstance(fileRepositoryProvider.get(), scanRepositoryProvider.get());
  }

  public static ScannerViewModel_Factory create(Provider<FileRepository> fileRepositoryProvider,
      Provider<ScanRepository> scanRepositoryProvider) {
    return new ScannerViewModel_Factory(fileRepositoryProvider, scanRepositoryProvider);
  }

  public static ScannerViewModel newInstance(FileRepository fileRepository,
      ScanRepository scanRepository) {
    return new ScannerViewModel(fileRepository, scanRepository);
  }
}
