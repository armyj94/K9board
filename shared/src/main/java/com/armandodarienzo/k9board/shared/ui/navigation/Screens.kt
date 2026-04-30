package com.armandodarienzo.k9board.shared.ui.navigation

enum class Screens {
    HomeScreen,
    LanguageSelectionScreen,
    PreferencesScreen,
    KeyboardTestScreen;


    companion object {
        fun fromRoute(route: String?) : Screens =
            when (route?.substringBefore("/")) {
                HomeScreen.name -> HomeScreen
                LanguageSelectionScreen.name -> LanguageSelectionScreen
                PreferencesScreen.name -> PreferencesScreen
                KeyboardTestScreen.name -> KeyboardTestScreen
                null -> HomeScreen
                else -> throw java.lang.IllegalArgumentException("Route $route is not recognized")
            }
    }
}