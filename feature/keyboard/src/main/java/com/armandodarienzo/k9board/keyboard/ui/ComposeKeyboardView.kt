package com.armandodarienzo.k9board.keyboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.AbstractComposeView
import com.armandodarienzo.k9board.keyboard.KeyboardFactory
import com.armandodarienzo.k9board.keyboard.KeyboardViewModel
import com.armandodarienzo.k9board.keyboard.service.Key9Service
import com.armandodarienzo.k9board.shared.ui.theme.T9KeyboardTheme

class ComposeKeyboardView(
    service: Key9Service,
    private val viewModel: KeyboardViewModel,
    private val keyboardFactory: KeyboardFactory,
) : AbstractComposeView(service) {

    @Composable
    override fun Content() {
        val state by viewModel.state.collectAsState()

        T9KeyboardTheme(themePreference = state.themeSet) {
            keyboardFactory.createKeyboard(
                state = state,
                onAction = viewModel::processAction,
            )
        }
    }
}
