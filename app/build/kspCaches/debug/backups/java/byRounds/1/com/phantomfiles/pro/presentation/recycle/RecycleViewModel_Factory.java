package com.phantomfiles.pro.presentation.recycle;

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
public final class RecycleViewModel_Factory implements Factory<RecycleViewModel> {
  private final Provider<RecycleBinRepository> recycleBinRepositoryProvider;

  public RecycleViewModel_Factory(Provider<RecycleBinRepository> recycleBinRepositoryProvider) {
    this.recycleBinRepositoryProvider = recycleBinRepositoryProvider;
  }

  @Override
  public RecycleViewModel get() {
    return newInstance(recycleBinRepositoryProvider.get());
  }

  public static RecycleViewModel_Factory create(
      Provider<RecycleBinRepository> recycleBinRepositoryProvider) {
    return new RecycleViewModel_Factory(recycleBinRepositoryProvider);
  }

  public static RecycleViewModel newInstance(RecycleBinRepository recycleBinRepository) {
    return new RecycleViewModel(recycleBinRepository);
  }
}
