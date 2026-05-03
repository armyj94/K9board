package com.armandodarienzo.k9board.di

import com.armandodarienzo.k9board.keyboard.KeyboardFactory
import com.armandodarienzo.k9board.ui.keyboard.MobileKeyboardFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
object KeyboardModule {

    @Provides
    fun provideKeyboardProvider(): KeyboardFactory {
        return MobileKeyboardFactory()
    }
}