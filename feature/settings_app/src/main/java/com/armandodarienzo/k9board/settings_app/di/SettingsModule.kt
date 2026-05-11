package com.armandodarienzo.k9board.settings_app.di

import com.armandodarienzo.k9board.repository.UserPreferencesRepository
import com.armandodarienzo.k9board.settings_app.ui.screens.home.HomeScreenReducer
import com.armandodarienzo.k9board.settings_app.ui.screens.language.LanguageSelectionReducer
import com.armandodarienzo.k9board.settings_app.ui.screens.preferences.PreferencesReducer
import com.armandodarienzo.k9board.settings_app.ui.base.StandardEffectDelegate
import com.armandodarienzo.k9board.usecase.settings.GetDoubleSpaceCharUseCase
import com.armandodarienzo.k9board.usecase.settings.GetKeyboardSizeUseCase
import com.armandodarienzo.k9board.usecase.settings.GetLanguageUseCase
import com.armandodarienzo.k9board.usecase.settings.IsAutoCapsEnabledUseCase
import com.armandodarienzo.k9board.usecase.settings.IsHapticFeedbackEnabledUseCase
import com.armandodarienzo.k9board.usecase.settings.IsStartWithManualEnabledUseCase
import com.armandodarienzo.k9board.usecase.settings.SetAutoCapsUseCase
import com.armandodarienzo.k9board.usecase.settings.SetDoubleSpaceCharUseCase
import com.armandodarienzo.k9board.usecase.settings.SetHapticFeedbackUseCase
import com.armandodarienzo.k9board.usecase.settings.SetKeyboardSizeUseCase
import com.armandodarienzo.k9board.usecase.settings.SetLanguageUseCase
import com.armandodarienzo.k9board.usecase.settings.SetStartWithManualUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ViewModelComponent::class)
object SettingsModule {

    @Provides
    @ViewModelScoped
    fun provideGetKeyboardSizeUseCase(repo: UserPreferencesRepository) =
        GetKeyboardSizeUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideSetKeyboardSizeUseCase(repo: UserPreferencesRepository) =
        SetKeyboardSizeUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideGetDoubleSpaceCharUseCase(repo: UserPreferencesRepository) =
        GetDoubleSpaceCharUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideSetDoubleSpaceCharUseCase(repo: UserPreferencesRepository) =
        SetDoubleSpaceCharUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideGetLanguageUseCase(repo: UserPreferencesRepository) =
        GetLanguageUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideSetLanguageUseCase(repo: UserPreferencesRepository) =
        SetLanguageUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideIsStartWithManualEnabledUseCase(repo: UserPreferencesRepository) =
        IsStartWithManualEnabledUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideSetStartWithManualUseCase(repo: UserPreferencesRepository) =
        SetStartWithManualUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideIsAutoCapsEnabledUseCase(repo: UserPreferencesRepository) =
        IsAutoCapsEnabledUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideSetAutoCapsUseCase(repo: UserPreferencesRepository) =
        SetAutoCapsUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideIsHapticFeedbackEnabledUseCase(repo: UserPreferencesRepository) =
        IsHapticFeedbackEnabledUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun provideSetHapticFeedbackUseCase(repo: UserPreferencesRepository) =
        SetHapticFeedbackUseCase(repo, Dispatchers.IO)

    @Provides
    @ViewModelScoped
    fun providePreferencesEffectDelegate(): StandardEffectDelegate<PreferencesReducer.Effect> =
        StandardEffectDelegate()

    @Provides
    @ViewModelScoped
    fun provideHomeScreenEffectDelegate(): StandardEffectDelegate<HomeScreenReducer.Effect> =
        StandardEffectDelegate()

    @Provides
    @ViewModelScoped
    fun provideLanguageSelectionEffectDelegate(): StandardEffectDelegate<LanguageSelectionReducer.Effect> =
        StandardEffectDelegate()
}