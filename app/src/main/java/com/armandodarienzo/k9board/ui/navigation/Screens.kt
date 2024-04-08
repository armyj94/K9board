package com.armandodarienzo.k9board.ui.navigation

enum class Screens {
    HomeScreen,
    LanguageSelectionScreen;


    companion object {
        fun fromRoute(route: String?) : Screens =
            when (route?.substringBefore("/")) {
                HomeScreen.name -> HomeScreen
                LanguageSelectionScreen.name -> LanguageSelectionScreen
                null -> HomeScreen
                else -> throw java.lang.IllegalArgumentException("Route $route is not recognized")
            }
    }
}