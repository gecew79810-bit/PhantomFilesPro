package com.phantomfiles.pro.presentation.appmanager;

import com.phantomfiles.pro.data.repository.AppManagerRepository;
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
public final class AppManagerViewModel_Factory implements Factory<AppManagerViewModel> {
  private final Provider<AppManagerRepository> appManagerRepositoryProvider;

  public AppManagerViewModel_Factory(Provider<AppManagerRepository> appManagerRepositoryProvider) {
    this.appManagerRepositoryProvider = appManagerRepositoryProvider;
  }

  @Override
  public AppManagerViewModel get() {
    return newInstance(appManagerRepositoryProvider.get());
  }

  public static AppManagerViewModel_Factory create(
      Provider<AppManagerRepository> appManagerRepositoryProvider) {
    return new AppManagerViewModel_Factory(appManagerRepositoryProvider);
  }

  public static AppManagerViewModel newInstance(AppManagerRepository appManagerRepository) {
    return new AppManagerViewModel(appManagerRepository);
  }
}
