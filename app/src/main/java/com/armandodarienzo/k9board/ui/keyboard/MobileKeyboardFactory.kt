package com.armandodarienzo.k9board.ui.keyboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.armandodarienzo.k9board.keyboard.KeyboardIntent
import com.armandodarienzo.k9board.keyboard.KeyboardFactory
import com.armandodarienzo.k9board.keyboard.KeyboardState

class MobileKeyboardFactory : KeyboardFactory {

    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    override fun createKeyboard(state: KeyboardState, onIntent: (KeyboardIntent) -> Unit) {
        CustomKeyboard(state = state, onIntent = onIntent)
    }
}