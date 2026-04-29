package com.phantomfiles.pro.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.phantomfiles.pro.data.repository.RecycleBinRepository;
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
public final class RecycleBinWorker_Factory {
  private final Provider<RecycleBinRepository> recycleBinRepositoryProvider;

  public RecycleBinWorker_Factory(Provider<RecycleBinRepository> recycleBinRepositoryProvider) {
    this.recycleBinRepositoryProvider = recycleBinRepositoryProvider;
  }

  public RecycleBinWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, recycleBinRepositoryProvider.get());
  }

  public static RecycleBinWorker_Factory create(
      Provider<RecycleBinRepository> recycleBinRepositoryProvider) {
    return new RecycleBinWorker_Factory(recycleBinRepositoryProvider);
  }

  public static RecycleBinWorker newInstance(Context context, WorkerParameters params,
      RecycleBinRepository recycleBinRepository) {
    return new RecycleBinWorker(context, params, recycleBinRepository);
  }
}
