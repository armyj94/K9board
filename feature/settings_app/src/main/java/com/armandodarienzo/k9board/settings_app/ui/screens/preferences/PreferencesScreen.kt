package com.armandodarienzo.k9board.settings_app.ui.screens.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.armandodarienzo.k9board.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.model.KeyboardSize
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.model.RadioOption
import com.armandodarienzo.k9board.shared.model.getLabelId
import com.armandodarienzo.k9board.shared.ui.elements.AppBarIcon
import com.armandodarienzo.k9board.shared.ui.elements.K9BoardTopAppBar
import com.armandodarienzo.k9board.shared.ui.elements.RadioDialog
import com.armandodarienzo.k9board.shared.ui.base.rememberFlowWithLifecycle
import com.armandodarienzo.k9board.settings_app.ui.screens.preferences.PreferencesReducer.PreferencesState
import com.armandodarienzo.k9board.settings_app.ui.screens.preferences.PreferencesViewModel.Action

@Composable
fun PreferencesScreen(
    navController: NavController,
    viewModel: PreferencesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val effectFlow = rememberFlowWithLifecycle(viewModel.effect)

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                PreferencesReducer.Effect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    PreferencesContent(state = state, sendAction = viewModel::processAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesContent(
    state: PreferencesState,
    sendAction: (Action) -> Unit,
) {
    val openKeyboardSizeDialog = remember { mutableStateOf(false) }
    val keyboardSizeOptions = KeyboardSize.values().map {
        RadioOption(it, state.keyboardSize == it, it.getLabelId())
    }.toTypedArray()

    if (openKeyboardSizeDialog.value) {
        RadioDialog(
            title = stringResource(id = R.string.keyboard_size),
            options = keyboardSizeOptions,
            onDismissRequest = { openKeyboardSizeDialog.value = false }
        ) { selected ->
            sendAction(Action.SetKeyboardSize(selected.value))
            openKeyboardSizeDialog.value = false
        }
    }

    val openDoubleSpaceDialog = remember { mutableStateOf(false) }
    val doubleSpaceOptions = DoubleSpaceCharacter.values().map {
        RadioOption(it, state.doubleSpaceChar == it, it.getLabelId())
    }.toTypedArray()

    if (openDoubleSpaceDialog.value) {
        RadioDialog(
            title = stringResource(id = R.string.double_space_character),
            options = doubleSpaceOptions,
            onDismissRequest = { openDoubleSpaceDialog.value = false }
        ) { selected ->
            sendAction(Action.SetDoubleSpaceChar(selected.value))
            openDoubleSpaceDialog.value = false
        }
    }

    Scaffold(
        topBar = {
            K9BoardTopAppBar(
                title = stringResource(id = R.string.main_activity_settings),
                icon = AppBarIcon(Icons.Default.ArrowBack) { sendAction(Action.NavigateBack) }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SectionRow(text = stringResource(id = R.string.layout_and_aspect_section))

            SettingsOptionRow(
                optionName = stringResource(id = R.string.keyboard_size),
                onClick = { openKeyboardSizeDialog.value = true }
            ) {
                InputChip(
                    selected = true,
                    onClick = { openKeyboardSizeDialog.value = true },
                    label = {
                        Text(
                            text = stringResource(id = state.keyboardSize.getLabelId()),
                            fontSize = 12.sp
                        )
                    }
                )
            }

            SectionSpacer()
            SectionRow(text = stringResource(id = R.string.functionalities_section))

            SettingsOptionRow(
                optionName = stringResource(id = R.string.double_space_character),
                onClick = { openDoubleSpaceDialog.value = true }
            ) {
                InputChip(
                    selected = true,
                    onClick = { openDoubleSpaceDialog.value = true },
                    label = {
                        Text(
                            text = stringResource(id = state.doubleSpaceChar.getLabelId()),
                            fontSize = 12.sp
                        )
                    }
                )
            }

            SettingsOptionRow(
                optionName = stringResource(id = R.string.start_with_manual),
                onClick = { sendAction(Action.SetStartWithManual(!state.startWithManual)) }
            ) {
                Switch(
                    checked = state.startWithManual,
                    onCheckedChange = { sendAction(Action.SetStartWithManual(it)) }
                )
            }

            SettingsOptionRow(
                optionName = stringResource(id = R.string.auto_caps),
                onClick = { sendAction(Action.SetAutoCaps(!state.autoCaps)) }
            ) {
                Switch(
                    checked = state.autoCaps,
                    onCheckedChange = { sendAction(Action.SetAutoCaps(it)) }
                )
            }
        }
    }
}

@Composable
private fun SectionRow(text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp),
            fontSize = 24.sp,
            style = TextStyle(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun SectionSpacer() {
    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsOptionRow(
    optionName: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .height(40.dp)
            .clickable { onClick() },
    ) {
        Column(
            Modifier
                .weight(5f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = optionName, modifier = Modifier.padding(start = 16.dp), fontSize = 18.sp)
        }
        Column(
            Modifier
                .weight(2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            trailingContent()
        }
    }
}