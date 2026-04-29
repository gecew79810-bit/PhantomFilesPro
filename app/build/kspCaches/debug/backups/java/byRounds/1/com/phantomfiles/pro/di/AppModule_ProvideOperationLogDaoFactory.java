package com.phantomfiles.pro.di;

import com.phantomfiles.pro.data.local.OperationLogDao;
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
public final class AppModule_ProvideOperationLogDaoFactory implements Factory<OperationLogDao> {
  private final Provider<PhantomDatabase> dbProvider;

  public AppModule_ProvideOperationLogDaoFactory(Provider<PhantomDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public OperationLogDao get() {
    return provideOperationLogDao(dbProvider.get());
  }

  public static AppModule_ProvideOperationLogDaoFactory create(
      Provider<PhantomDatabase> dbProvider) {
    return new AppModule_ProvideOperationLogDaoFactory(dbProvider);
  }

  public static OperationLogDao provideOperationLogDao(PhantomDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideOperationLogDao(db));
  }
}
