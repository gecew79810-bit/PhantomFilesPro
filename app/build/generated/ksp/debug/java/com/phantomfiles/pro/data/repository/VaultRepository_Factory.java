package com.phantomfiles.pro.data.repository;

import android.content.Context;
import com.phantomfiles.pro.data.local.VaultDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class VaultRepository_Factory implements Factory<VaultRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<VaultDao> vaultDaoProvider;

  public VaultRepository_Factory(Provider<Context> contextProvider,
      Provider<VaultDao> vaultDaoProvider) {
    this.contextProvider = contextProvider;
    this.vaultDaoProvider = vaultDaoProvider;
  }

  @Override
  public VaultRepository get() {
    return newInstance(contextProvider.get(), vaultDaoProvider.get());
  }

  public static VaultRepository_Factory create(Provider<Context> contextProvider,
      Provider<VaultDao> vaultDaoProvider) {
    return new VaultRepository_Factory(contextProvider, vaultDaoProvider);
  }

  public static VaultRepository newInstance(Context context, VaultDao vaultDao) {
    return new VaultRepository(context, vaultDao);
  }
}
