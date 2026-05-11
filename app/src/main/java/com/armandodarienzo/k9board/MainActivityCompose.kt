package com.armandodarienzo.k9board

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.armandodarienzo.k9board.shared.THEME_MATERIAL_YOU
import com.armandodarienzo.k9board.ui.navigation.Navigation
import com.armandodarienzo.k9board.shared.ui.theme.T9KeyboardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApp {
                Navigation()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    T9KeyboardTheme (themePreference = THEME_MATERIAL_YOU) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}


