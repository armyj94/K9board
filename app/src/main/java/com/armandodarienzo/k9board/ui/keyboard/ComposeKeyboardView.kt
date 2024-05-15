package com.armandodarienzo.k9board.ui.keyboard

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.armandodarienzo.k9board.shared.Key9Service
import com.armandodarienzo.k9board.shared.SHARED_PREFS_HAPTIC_FEEDBACK
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE

import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_THEME
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.repository.UserPreferencesRepositoryLocal
import com.armandodarienzo.k9board.shared.repository.dataStore
import com.armandodarienzo.k9board.ui.theme.T9KeyboardTheme
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class ComposeKeyboardView(
    var service: Key9Service,
    private var backgroundColorId: Int,
) : AbstractComposeView(service) {

    @Composable
    override fun Content() {

        //val preferencesViewModel = hiltViewModel<PreferencesViewModel>()
        val userPreferencesRepository = UserPreferencesRepositoryLocal(context.dataStore)

        var context = LocalContext.current



        val languageSet = runBlocking{
            var value = ""
            userPreferencesRepository.getLanguage().map {
                value = it
            }
            value
        }


        val themeSet = runBlocking{
            var value = ""
            userPreferencesRepository.getTheme().map {
                value = it
            }
            value
        }

        val keyboardSize = runBlocking{
            var value = KeyboardSize.MEDIUM
            userPreferencesRepository.getKeyboardSize().map {
                value = it
            }
            value
        }


//        val theme = remember {
//            when (themeSetState.value) {
//                "A" -> { AComposable }
//                "B" -> { BComposable }
//                else -> { DefaultComposable }
//            }
//        }
//        composable()


        var hapticFeedback = runBlocking{
            var value = false
            userPreferencesRepository.isHapticFeedbackEnabled().map {
                value = it
            }
            value
        }

        T9KeyboardTheme(themePreference = themeSet) {
            CustomKeyboard(
                backGroundColorId = backgroundColorId,
                service = service,
                languageSet = languageSet,
                keyboardSize = keyboardSize,
                hapticFeedback = hapticFeedback)
        }

    }

}