package com.armandodarienzo.k9board

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.armandodarienzo.k9board.presentation.keyboard.CustomKeyboard
import com.armandodarienzo.k9board.shared.service.Key9Service
import com.armandodarienzo.k9board.shared.ui.KeyboardProvider


class WearKeyboardProvider() : KeyboardProvider {

    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    override fun provideKeyboard(
        service: Key9Service?,
        backgroundColorId: Int,
        languageSet: String,
        keyboardSize: Int,
        hapticFeedback: Boolean
    ) {
        CustomKeyboard(
            backGroundColorId = backgroundColorId,
            service = service,
            languageSet = languageSet,
            keyboardSize = keyboardSize,
            hapticFeedback = hapticFeedback)
    }

}