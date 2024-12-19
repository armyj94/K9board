package com.armandodarienzo.k9board.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.armandodarienzo.k9board.shared.ui.navigation.Screens
import com.armandodarienzo.k9board.ui.screens.HomeScreen
import com.armandodarienzo.k9board.ui.screens.LanguageSelectionScreen
import com.armandodarienzo.k9board.ui.screens.PreferencesScreen


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
    }
}