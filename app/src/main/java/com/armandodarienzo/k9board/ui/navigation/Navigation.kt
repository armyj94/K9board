package com.armandodarienzo.k9board.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.armandodarienzo.k9board.settings_app.ui.screens.home.HomeScreen
import com.armandodarienzo.k9board.settings_app.ui.screens.keyboard_test.KeyboardTestScreen
import com.armandodarienzo.k9board.settings_app.ui.screens.language.LanguageSelectionScreen
import com.armandodarienzo.k9board.settings_app.ui.screens.preferences.PreferencesScreen
import com.armandodarienzo.k9board.shared.ui.navigation.Screens

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.name
    ) {
        composable(Screens.HomeScreen.name) {
            HomeScreen(navController = navController)
        }

        composable(Screens.LanguageSelectionScreen.name) {
            LanguageSelectionScreen(navController = navController)
        }

        composable(Screens.PreferencesScreen.name) {
            PreferencesScreen(navController = navController)
        }

        composable(Screens.KeyboardTestScreen.name) {
            KeyboardTestScreen(navController = navController)
        }
    }
}