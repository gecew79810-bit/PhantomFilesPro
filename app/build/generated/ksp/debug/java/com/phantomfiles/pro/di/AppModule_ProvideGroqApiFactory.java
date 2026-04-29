package com.phantomfiles.pro.di;

import com.phantomfiles.pro.data.remote.GroqApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class AppModule_ProvideGroqApiFactory implements Factory<GroqApi> {
  private final Provider<OkHttpClient> clientProvider;

  public AppModule_ProvideGroqApiFactory(Provider<OkHttpClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public GroqApi get() {
    return provideGroqApi(clientProvider.get());
  }

  public static AppModule_ProvideGroqApiFactory create(Provider<OkHttpClient> clientProvider) {
    return new AppModule_ProvideGroqApiFactory(clientProvider);
  }

  public static GroqApi provideGroqApi(OkHttpClient client) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideGroqApi(client));
  }
}
