package com.armandodarienzo.k9board.keyboard

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable

interface KeyboardFactory {

    @SuppressLint("ComposableNaming")
    @Composable
    fun createKeyboard(
        state: KeyboardState,
        onIntent: (KeyboardIntent) -> Unit,
    )
}
