package com.phantomfiles.pro.data.repository;

import android.content.Context;
import com.phantomfiles.pro.data.local.RecycleBinDao;
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
public final class RecycleBinRepository_Factory implements Factory<RecycleBinRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<RecycleBinDao> recycleBinDaoProvider;

  public RecycleBinRepository_Factory(Provider<Context> contextProvider,
      Provider<RecycleBinDao> recycleBinDaoProvider) {
    this.contextProvider = contextProvider;
    this.recycleBinDaoProvider = recycleBinDaoProvider;
  }

  @Override
  public RecycleBinRepository get() {
    return newInstance(contextProvider.get(), recycleBinDaoProvider.get());
  }

  public static RecycleBinRepository_Factory create(Provider<Context> contextProvider,
      Provider<RecycleBinDao> recycleBinDaoProvider) {
    return new RecycleBinRepository_Factory(contextProvider, recycleBinDaoProvider);
  }

  public static RecycleBinRepository newInstance(Context context, RecycleBinDao recycleBinDao) {
    return new RecycleBinRepository(context, recycleBinDao);
  }
}
