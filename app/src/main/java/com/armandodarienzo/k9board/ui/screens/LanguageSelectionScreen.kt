package com.armandodarienzo.k9board.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE
import com.armandodarienzo.k9board.dataStore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun LanguageSelectionScreen(navController: NavController) {
    LanguagesList()
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LanguagesList(){
    var context = LocalContext.current
    var scope = rememberCoroutineScope()

    val languageSetKey = stringPreferencesKey(SHARED_PREFS_SET_LANGUAGE)
    var languageSetState = flow{
        context.dataStore.data.map {
            it[languageSetKey]
        }.collect(collector = {
            if (it!=null){
                this.emit(it)
            }
        })
    }.collectAsState(initial = "us-US")

    val radioOptions = listOf("us-US", "ru-RU")
//    val (selectedOption, onOptionSelected) = remember { mutableStateOf( languageSetState.value) }
//    var selectedOption by remember {
//        mutableStateOf( radioOptions[1])
//    }
    val onOptionSelected = { text: String ->
//        selectedOption = text
        scope.launch {
            changeLanguage(context, text)
        }
    }

    Column {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == languageSetState.value),
                        onClick = {
                            onOptionSelected(text)
                        }
                    )
                    .padding(horizontal = 16.dp)
            ) {
                RadioButton(
                    selected = (text == languageSetState.value),
                    onClick = {
                        onOptionSelected(text)

                    }
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall.merge(),
                    modifier = Modifier.padding(start = 8.dp, top = 16.dp)
                )
            }
        }

        TextField(value = "", onValueChange = {})
    }
}

suspend fun changeLanguage(context: Context, value: String){
    val languageSetKey = stringPreferencesKey(SHARED_PREFS_SET_LANGUAGE)
    context.dataStore.edit {
        it[languageSetKey] = value
    }
}

