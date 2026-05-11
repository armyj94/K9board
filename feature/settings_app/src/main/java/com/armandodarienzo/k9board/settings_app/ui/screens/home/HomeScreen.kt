package com.armandodarienzo.k9board.settings_app.ui.screens.home

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.armandodarienzo.k9board.model.MainMenuItem
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE
import com.armandodarienzo.k9board.shared.ui.elements.K9BoardTopAppBar
import com.armandodarienzo.k9board.shared.ui.navigation.Screens
import com.armandodarienzo.k9board.settings_app.ui.base.rememberFlowWithLifecycle
import com.armandodarienzo.k9board.settings_app.ui.screens.home.HomeScreenReducer.Effect
import com.armandodarienzo.k9board.settings_app.ui.screens.home.HomeScreenViewModel.Action

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val effectFlow = rememberFlowWithLifecycle(viewModel.effect)

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is Effect.LaunchActivity -> context.startActivity(effect.intent)
                Effect.ShowImePicker -> {
                    val imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imeManager.showInputMethodPicker()
                }
            }
        }
    }

    HomeScreenContent(
        onLanguageClicked = { navController.navigate(Screens.LanguageSelectionScreen.name) },
        onEnableKeyboardClicked = { viewModel.processAction(Action.EnableKeyboard) },
        onChangeKeyboardClicked = { viewModel.processAction(Action.ChangeKeyboard) },
        onSettingsClicked = { navController.navigate(Screens.PreferencesScreen.name) },
        onTestKeyboardClicked = { navController.navigate(Screens.KeyboardTestScreen.name) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    onLanguageClicked: () -> Unit,
    onEnableKeyboardClicked: () -> Unit,
    onChangeKeyboardClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onTestKeyboardClicked: () -> Unit,
) {
    val menuItems = listOf(
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_languages),
            optionKeyString = SHARED_PREFS_SET_LANGUAGE,
            iconID = R.drawable.ic_language_white_18dp,
            onClick = onLanguageClicked
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_enable_keyboard),
            optionKeyString = null,
            iconID = R.drawable.ic_keyboard_white_24dp,
            onClick = onEnableKeyboardClicked
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_change_keyboard),
            optionKeyString = null,
            iconID = R.drawable.ic_baseline_compare_arrows_18,
            onClick = onChangeKeyboardClicked
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_settings),
            optionKeyString = null,
            iconID = R.drawable.ic_baseline_settings_18,
            onClick = onSettingsClicked
        ),
        MainMenuItem(
            name = stringResource(id = R.string.main_activity_test_keyboard),
            optionKeyString = null,
            iconID = R.drawable.ic_baseline_edit_note_24,
            onClick = onTestKeyboardClicked
        ),
    )

    Scaffold(
        topBar = {
            K9BoardTopAppBar(
                title = stringResource(id = R.string.app_name),
                icon = null,
            )
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            menuItems.forEach { menuItem ->
                HomeMenuItem(modifier = Modifier.height(80.dp), menuItem = menuItem)
            }
        }
    }
}

@Composable
fun HomeMenuItem(
    modifier: Modifier = Modifier,
    menuItem: MainMenuItem
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp)
            .clickable { menuItem.onClick() },
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = modifier.size(40.dp),
                painter = painterResource(menuItem.iconID),
                contentDescription = menuItem.name,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = menuItem.name,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}