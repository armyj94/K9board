package com.armandodarienzo.k9board.shared.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import com.armandodarienzo.k9board.shared.service.Key9Service

interface KeyboardProvider {

    @SuppressLint("ComposableNaming")
    //if function name is not lowercase, hilt will not work properly
    @Composable
    fun provideKeyboard(
        service: Key9Service?,
        backgroundColorId: Int,
        languageSet: String,
        keyboardSize: Int,
        hapticFeedback: Boolean
    )
}