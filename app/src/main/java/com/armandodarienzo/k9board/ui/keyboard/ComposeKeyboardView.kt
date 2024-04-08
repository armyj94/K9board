package com.armandodarienzo.k9board.ui.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.armandodarienzo.k9board.shared.Key9Service
import com.armandodarienzo.k9board.shared.SHARED_PREFS_HAPTIC_FEEDBACK
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE
import com.armandodarienzo.k9board.dataStore
import com.armandodarienzo.k9board.ui.theme.T9KeyboardTheme
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ComposeKeyboardView(var service: Key9Service, private var backgroundColorId: Int) : AbstractComposeView(service) {

    @Composable
    override fun Content() {
        var context = LocalContext.current
        val languageSetKey = stringPreferencesKey(SHARED_PREFS_SET_LANGUAGE)
        var languageSetState = flow{
            context.dataStore.data.map {
                it[languageSetKey]
            }.collect(collector = {
                if (it!=null){
                    this.emit(it)
                }
            })
        }.collectAsState(initial = "us-US")

        val hapticFeedbackKey = booleanPreferencesKey(SHARED_PREFS_HAPTIC_FEEDBACK)
        var hapticFeedback = flow {
            context.dataStore.data.map {
                it[hapticFeedbackKey]
            }.collect(collector = {
                if (it!=null){
                    this.emit(it)
                }
            })
        }.collectAsState(initial = false)

        T9KeyboardTheme() {
            CustomKeyboard(backGroundColorId = backgroundColorId, service = service, languageSetState = languageSetState, hapticFeedback = hapticFeedback)
        }

    }
}