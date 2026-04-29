package com.phantomfiles.pro.di;

import com.phantomfiles.pro.data.local.PhantomDatabase;
import com.phantomfiles.pro.data.local.VaultDao;
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
public final class AppModule_ProvideVaultDaoFactory implements Factory<VaultDao> {
  private final Provider<PhantomDatabase> dbProvider;

  public AppModule_ProvideVaultDaoFactory(Provider<PhantomDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public VaultDao get() {
    return provideVaultDao(dbProvider.get());
  }

  public static AppModule_ProvideVaultDaoFactory create(Provider<PhantomDatabase> dbProvider) {
    return new AppModule_ProvideVaultDaoFactory(dbProvider);
  }

  public static VaultDao provideVaultDao(PhantomDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideVaultDao(db));
  }
}
