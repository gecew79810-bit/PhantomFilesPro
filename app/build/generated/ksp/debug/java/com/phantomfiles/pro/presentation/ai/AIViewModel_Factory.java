package com.phantomfiles.pro.presentation.ai;

import com.phantomfiles.pro.domain.usecase.AICommandUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AIViewModel_Factory implements Factory<AIViewModel> {
  private final Provider<AICommandUseCase> aiCommandUseCaseProvider;

  public AIViewModel_Factory(Provider<AICommandUseCase> aiCommandUseCaseProvider) {
    this.aiCommandUseCaseProvider = aiCommandUseCaseProvider;
  }

  @Override
  public AIViewModel get() {
    return newInstance(aiCommandUseCaseProvider.get());
  }

  public static AIViewModel_Factory create(Provider<AICommandUseCase> aiCommandUseCaseProvider) {
    return new AIViewModel_Factory(aiCommandUseCaseProvider);
  }

  public static AIViewModel newInstance(AICommandUseCase aiCommandUseCase) {
    return new AIViewModel(aiCommandUseCase);
  }
}
