package com.armandodarienzo.k9board

import com.armandodarienzo.k9board.shared.ui.KeyboardProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object MobileComposableModule {
    @Provides
    @ServiceScoped
    fun provideKeyboardProvider(): KeyboardProvider {
        return WearKeyboardProvider()
    }
}