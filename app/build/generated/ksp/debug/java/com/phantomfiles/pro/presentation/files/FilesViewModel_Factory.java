package com.phantomfiles.pro.presentation.files;

import com.phantomfiles.pro.data.local.BookmarkDao;
import com.phantomfiles.pro.data.local.OperationLogDao;
import com.phantomfiles.pro.data.repository.FileRepository;
import com.phantomfiles.pro.data.repository.RecycleBinRepository;
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
public final class FilesViewModel_Factory implements Factory<FilesViewModel> {
  private final Provider<FileRepository> fileRepositoryProvider;

  private final Provider<RecycleBinRepository> recycleBinRepositoryProvider;

  private final Provider<BookmarkDao> bookmarkDaoProvider;

  private final Provider<OperationLogDao> operationLogDaoProvider;

  public FilesViewModel_Factory(Provider<FileRepository> fileRepositoryProvider,
      Provider<RecycleBinRepository> recycleBinRepositoryProvider,
      Provider<BookmarkDao> bookmarkDaoProvider,
      Provider<OperationLogDao> operationLogDaoProvider) {
    this.fileRepositoryProvider = fileRepositoryProvider;
    this.recycleBinRepositoryProvider = recycleBinRepositoryProvider;
    this.bookmarkDaoProvider = bookmarkDaoProvider;
    this.operationLogDaoProvider = operationLogDaoProvider;
  }

  @Override
  public FilesViewModel get() {
    return newInstance(fileRepositoryProvider.get(), recycleBinRepositoryProvider.get(), bookmarkDaoProvider.get(), operationLogDaoProvider.get());
  }

  public static FilesViewModel_Factory create(Provider<FileRepository> fileRepositoryProvider,
      Provider<RecycleBinRepository> recycleBinRepositoryProvider,
      Provider<BookmarkDao> bookmarkDaoProvider,
      Provider<OperationLogDao> operationLogDaoProvider) {
    return new FilesViewModel_Factory(fileRepositoryProvider, recycleBinRepositoryProvider, bookmarkDaoProvider, operationLogDaoProvider);
  }

  public static FilesViewModel newInstance(FileRepository fileRepository,
      RecycleBinRepository recycleBinRepository, BookmarkDao bookmarkDao,
      OperationLogDao operationLogDao) {
    return new FilesViewModel(fileRepository, recycleBinRepository, bookmarkDao, operationLogDao);
  }
}
