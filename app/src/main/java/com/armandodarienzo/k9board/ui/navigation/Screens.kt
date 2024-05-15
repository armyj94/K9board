package com.armandodarienzo.k9board.ui.navigation

enum class Screens {
    HomeScreen,
    LanguageSelectionScreen,
    PreferencesScreen;


    companion object {
        fun fromRoute(route: String?) : Screens =
            when (route?.substringBefore("/")) {
                HomeScreen.name -> HomeScreen
                LanguageSelectionScreen.name -> LanguageSelectionScreen
                PreferencesScreen.name -> PreferencesScreen
                null -> HomeScreen
                else -> throw java.lang.IllegalArgumentException("Route $route is not recognized")
            }
    }
}