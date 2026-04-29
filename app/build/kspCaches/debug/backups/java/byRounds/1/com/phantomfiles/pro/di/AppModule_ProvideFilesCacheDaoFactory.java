package com.phantomfiles.pro.di;

import com.phantomfiles.pro.data.local.FilesCacheDao;
import com.phantomfiles.pro.data.local.PhantomDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideFilesCacheDaoFactory implements Factory<FilesCacheDao> {
  private final Provider<PhantomDatabase> dbProvider;

  public AppModule_ProvideFilesCacheDaoFactory(Provider<PhantomDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FilesCacheDao get() {
    return provideFilesCacheDao(dbProvider.get());
  }

  public static AppModule_ProvideFilesCacheDaoFactory create(Provider<PhantomDatabase> dbProvider) {
    return new AppModule_ProvideFilesCacheDaoFactory(dbProvider);
  }

  public static FilesCacheDao provideFilesCacheDao(PhantomDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFilesCacheDao(db));
  }
}
