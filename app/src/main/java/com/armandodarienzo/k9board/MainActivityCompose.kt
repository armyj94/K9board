package com.armandodarienzo.k9board

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.armandodarienzo.k9board.ui.navigation.Navigation
import com.armandodarienzo.k9board.ui.theme.T9KeyboardTheme

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

//@Composable
//fun Greeting(name: String) {
//    Text(text = "Hello $name!")
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    T9KeyboardTheme {
//        Greeting("Android")
//    }
//}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    T9KeyboardTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
//                    HomeScreen()
            content()
        }
    }
}

