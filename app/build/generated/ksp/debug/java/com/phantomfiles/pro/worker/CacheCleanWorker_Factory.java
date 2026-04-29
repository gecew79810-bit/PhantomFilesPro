package com.phantomfiles.pro.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.phantomfiles.pro.data.repository.ScanRepository;
import dagger.internal.DaggerGenerated;
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
public final class CacheCleanWorker_Factory {
  private final Provider<ScanRepository> scanRepositoryProvider;

  public CacheCleanWorker_Factory(Provider<ScanRepository> scanRepositoryProvider) {
    this.scanRepositoryProvider = scanRepositoryProvider;
  }

  public CacheCleanWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, scanRepositoryProvider.get());
  }

  public static CacheCleanWorker_Factory create(Provider<ScanRepository> scanRepositoryProvider) {
    return new CacheCleanWorker_Factory(scanRepositoryProvider);
  }

  public static CacheCleanWorker newInstance(Context context, WorkerParameters params,
      ScanRepository scanRepository) {
    return new CacheCleanWorker(context, params, scanRepository);
  }
}
