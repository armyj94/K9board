package com.armandodarienzo.k9board.service

import android.os.Build
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import com.armandodarienzo.k9board.shared.service.Key9Service

import com.armandodarienzo.k9board.ui.keyboard.ComposeKeyboardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Key9ServiceMobile(): Key9Service() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateInputView(): View {

        setBackgroundColorId()
        view = ComposeKeyboardView(this, backgroundColorId)

        return super.onCreateInputView()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {

        /* This is needed because otherwise recomposition wont be triggered
        *  when user preferences are changed. Won't be needed anymore when
        * and if AbstractComposeView can work with HiltViewModel */
        (view as ComposeKeyboardView).disposeComposition()

        super.onStartInputView(info, restarting)
    }

}