package com.phantomfiles.pro.data.repository;

import com.phantomfiles.pro.data.local.ScanResultDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ScanRepository_Factory implements Factory<ScanRepository> {
  private final Provider<ScanResultDao> scanResultDaoProvider;

  public ScanRepository_Factory(Provider<ScanResultDao> scanResultDaoProvider) {
    this.scanResultDaoProvider = scanResultDaoProvider;
  }

  @Override
  public ScanRepository get() {
    return newInstance(scanResultDaoProvider.get());
  }

  public static ScanRepository_Factory create(Provider<ScanResultDao> scanResultDaoProvider) {
    return new ScanRepository_Factory(scanResultDaoProvider);
  }

  public static ScanRepository newInstance(ScanResultDao scanResultDao) {
    return new ScanRepository(scanResultDao);
  }
}
