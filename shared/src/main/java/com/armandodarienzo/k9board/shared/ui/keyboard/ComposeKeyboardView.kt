package com.armandodarienzo.k9board.shared.ui.keyboard

import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalConfiguration
import com.armandodarienzo.k9board.shared.KEYBOARD_MIN_SIZE
import com.armandodarienzo.k9board.shared.KEYBOARD_SIZE_FACTOR_WATCH
import com.armandodarienzo.k9board.shared.service.Key9Service

import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.repository.UserPreferencesRepositoryLocal
import com.armandodarienzo.k9board.shared.repository.dataStore
import com.armandodarienzo.k9board.shared.ui.KeyboardProvider
import com.armandodarienzo.k9board.shared.ui.theme.T9KeyboardTheme
import kotlinx.coroutines.runBlocking

class ComposeKeyboardView(
    var service: Key9Service,
    var backgroundColorId: Int,
    var keyboardProvider: KeyboardProvider
) : AbstractComposeView(service) {

    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    override fun Content() {

        /*We access directly the repository because it is not possible to
        * inject a hiltViewModel in an AbstractComposeView at the moment*/
        val userPreferencesRepository = UserPreferencesRepositoryLocal(context.dataStore)
        val packageManager = context.packageManager

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

        val keyboardSizeFactor = if(packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)) {
            KEYBOARD_SIZE_FACTOR_WATCH
        } else {
            runBlocking{
                var value = KeyboardSize.MEDIUM.factor
                userPreferencesRepository.getKeyboardSize().map {
                    value = it.factor
                }
                value
            }
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
                (screenHeight * keyboardSizeFactor).toInt()
            )

        T9KeyboardTheme(themePreference = themeSet) {

            keyboardProvider.provideKeyboard(
                service = service,
                backgroundColorId = backgroundColorId,
                languageSet = languageSet,
                keyboardSize = keyboardSize,
                hapticFeedback = hapticFeedback
            )

        }

    }

}