package com.armandodarienzo.k9board.settings_app.ui.screens.keyboard_test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.ui.elements.AppBarIcon
import com.armandodarienzo.k9board.shared.ui.elements.K9BoardTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardTestScreen(navController: NavController) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            K9BoardTopAppBar(
                title = stringResource(id = R.string.main_activity_test_keyboard),
                icon = AppBarIcon(Icons.Default.ArrowBack) { navController.navigateUp() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Type here…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}