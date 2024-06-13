package com.armandodarienzo.k9board.ui.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalConfiguration
import com.armandodarienzo.k9board.shared.KEYBOARD_MIN_SIZE
import com.armandodarienzo.k9board.shared.service.Key9Service

import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.repository.UserPreferencesRepositoryLocal
import com.armandodarienzo.k9board.shared.repository.dataStore
import com.armandodarienzo.k9board.ui.theme.T9KeyboardTheme
import kotlinx.coroutines.runBlocking

class ComposeKeyboardView(
    var service: Key9Service,
    private var backgroundColorId: Int,
) : AbstractComposeView(service) {

    @Composable
    override fun Content() {

        /*We access directly the repository because it is not possible to
        * inject a hiltViewModel in an AbstractComposeView at the moment*/
        val userPreferencesRepository = UserPreferencesRepositoryLocal(context.dataStore)

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

        val keyboardSizeFactor = runBlocking{
            var value = KeyboardSize.MEDIUM
            userPreferencesRepository.getKeyboardSize().map {
                value = it
            }
            value
        }


        val hapticFeedback = runBlocking{
            var value = false
            userPreferencesRepository.isHapticFeedbackEnabled().map {
                value = it
            }
            value
        }

        val screenHeight = LocalConfiguration.current.screenHeightDp
        val keyboardSize =
            maxOf(
                KEYBOARD_MIN_SIZE,
                (screenHeight * keyboardSizeFactor.factor).toInt()
            )

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