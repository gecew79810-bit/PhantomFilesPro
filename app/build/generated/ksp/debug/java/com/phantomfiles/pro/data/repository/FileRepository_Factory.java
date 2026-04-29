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
public final class FileRepository_Factory implements Factory<FileRepository> {
  @Override
  public FileRepository get() {
    return newInstance();
  }

  public static FileRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FileRepository newInstance() {
    return new FileRepository();
  }

  private static final class InstanceHolder {
    private static final FileRepository_Factory INSTANCE = new FileRepository_Factory();
  }
}
