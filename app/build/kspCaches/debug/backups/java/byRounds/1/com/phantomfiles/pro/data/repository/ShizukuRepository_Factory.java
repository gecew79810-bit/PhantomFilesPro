package com.phantomfiles.pro.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ShizukuRepository_Factory implements Factory<ShizukuRepository> {
  @Override
  public ShizukuRepository get() {
    return newInstance();
  }

  public static ShizukuRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ShizukuRepository newInstance() {
    return new ShizukuRepository();
  }

  private static final class InstanceHolder {
    private static final ShizukuRepository_Factory INSTANCE = new ShizukuRepository_Factory();
  }
}
