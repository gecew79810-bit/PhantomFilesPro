package com.phantomfiles.pro.data.repository;

import android.content.Context;
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
public final class AppManagerRepository_Factory implements Factory<AppManagerRepository> {
  private final Provider<Context> contextProvider;

  public AppManagerRepository_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppManagerRepository get() {
    return newInstance(contextProvider.get());
  }

  public static AppManagerRepository_Factory create(Provider<Context> contextProvider) {
    return new AppManagerRepository_Factory(contextProvider);
  }

  public static AppManagerRepository newInstance(Context context) {
    return new AppManagerRepository(context);
  }
}
