package com.phantomfiles.pro.di;

import com.phantomfiles.pro.data.local.PhantomDatabase;
import com.phantomfiles.pro.data.local.RecycleBinDao;
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
public final class AppModule_ProvideRecycleBinDaoFactory implements Factory<RecycleBinDao> {
  private final Provider<PhantomDatabase> dbProvider;

  public AppModule_ProvideRecycleBinDaoFactory(Provider<PhantomDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RecycleBinDao get() {
    return provideRecycleBinDao(dbProvider.get());
  }

  public static AppModule_ProvideRecycleBinDaoFactory create(Provider<PhantomDatabase> dbProvider) {
    return new AppModule_ProvideRecycleBinDaoFactory(dbProvider);
  }

  public static RecycleBinDao provideRecycleBinDao(PhantomDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRecycleBinDao(db));
  }
}
