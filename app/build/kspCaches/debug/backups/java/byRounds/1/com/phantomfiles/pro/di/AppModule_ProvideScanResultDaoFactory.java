package com.phantomfiles.pro.di;

import com.phantomfiles.pro.data.local.PhantomDatabase;
import com.phantomfiles.pro.data.local.ScanResultDao;
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
public final class AppModule_ProvideScanResultDaoFactory implements Factory<ScanResultDao> {
  private final Provider<PhantomDatabase> dbProvider;

  public AppModule_ProvideScanResultDaoFactory(Provider<PhantomDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ScanResultDao get() {
    return provideScanResultDao(dbProvider.get());
  }

  public static AppModule_ProvideScanResultDaoFactory create(Provider<PhantomDatabase> dbProvider) {
    return new AppModule_ProvideScanResultDaoFactory(dbProvider);
  }

  public static ScanResultDao provideScanResultDao(PhantomDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideScanResultDao(db));
  }
}
