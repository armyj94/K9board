package com.armandodarienzo.k9board.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.viewmodel.PreferencesViewModel
import com.armandodarienzo.k9board.ui.elements.AppBarIcon
import com.armandodarienzo.k9board.ui.elements.K9BoardTopAppBar
import com.armandodarienzo.k9board.ui.elements.RadioDialog
import com.armandodarienzo.k9board.ui.elements.RadioOption
import com.armandodarienzo.k9board.ui.elements.ResizableText

@Preview
@Composable
fun PreferencesScreenPreview() {
    PreferencesScreenContent(
        onBackIconClicked = {},
        keyboardSize = KeyboardSize.MEDIUM,
        onKeyboardSizeSelected = {},
        doubleSpaceCharacter = DoubleSpaceCharacter.COMMA,
        onDoubleSpaceCharacterSelected = {},
        startWithManual = false,
        onStartWithManualSelected = {}
    )
}

@Composable
fun PreferencesScreen(
    navController: NavController,
    viewModel : PreferencesViewModel = hiltViewModel()
){

    val onBackIconClicked : () -> Unit = {
        navController.popBackStack()
    }

    val keyboardSizeSelected = viewModel.keyboardSizeState.value
    val onKeyboardSizeSelected : (KeyboardSize) -> Unit = {
        viewModel.setKeyboardSize(it)
    }

    val doubleSpaceCharacter = viewModel.doubleSpaceCharState.value
    val onDoubleSpaceCharacterSelected : (DoubleSpaceCharacter) -> Unit = {
        viewModel.setDoubleSpaceChar(it)
    }

    val startWithManual = viewModel.startWithManualState.value
    val onStartWithManualSelected : (Boolean) -> Unit = {
        viewModel.setStartWithManual(it)
    }

    PreferencesScreenContent(
        onBackIconClicked,
        keyboardSizeSelected,
        onKeyboardSizeSelected,
        doubleSpaceCharacter,
        onDoubleSpaceCharacterSelected,
        startWithManual,
        onStartWithManualSelected
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreenContent(
    onBackIconClicked : () -> Unit,
    keyboardSize : KeyboardSize,
    onKeyboardSizeSelected : (KeyboardSize) -> Unit,
    doubleSpaceCharacter: DoubleSpaceCharacter,
    onDoubleSpaceCharacterSelected : (DoubleSpaceCharacter) -> Unit,
    startWithManual : Boolean,
    onStartWithManualSelected : (Boolean) -> Unit
) {
    val openKeyboardSizeDialog = remember { mutableStateOf(false) }
    val keyboardSizeRadioOptions = KeyboardSize.values().map {
        val label = when (it) {
            KeyboardSize.SMALL -> "Small"
            KeyboardSize.MEDIUM -> "Medium"
            KeyboardSize.LARGE -> "Large"
            KeyboardSize.VERY_SMALL -> "Very small"
            KeyboardSize.VERY_LARGE -> "Very Large"
        }
        RadioOption(label, keyboardSize == it, it)
    }.toTypedArray()
    when {
        openKeyboardSizeDialog.value -> {
            RadioDialog(
                title = "Keyboard Size" ,
                options = keyboardSizeRadioOptions,
                onDismissRequest = {
                    openKeyboardSizeDialog.value = false
                }
            ) {
                onKeyboardSizeSelected(it.value)
            }
        }
    }

    val openDoubleSpaceDialog = remember { mutableStateOf(false) }
    val doubleSpaceRadioOptions = DoubleSpaceCharacter.values().map {
        val label = when (it) {
            DoubleSpaceCharacter.NONE -> "None"
            DoubleSpaceCharacter.DOT -> "Dot"
            DoubleSpaceCharacter.COMMA -> "Comma"
        }
        RadioOption(label, doubleSpaceCharacter == it, it)
    }.toTypedArray()
    when {
        openDoubleSpaceDialog.value -> {
            RadioDialog(
                title = "Double space character" ,
                options = doubleSpaceRadioOptions,
                onDismissRequest = {
                    openDoubleSpaceDialog.value = false
                }
            ) {
                onDoubleSpaceCharacterSelected(it.value)
            }
        }
    }


    Scaffold (
        topBar = {
            K9BoardTopAppBar(
                title = stringResource(id = R.string.main_activity_settings),
                icon = AppBarIcon(
                    imageVector = Icons.Default.ArrowBack,
                ) {
                    onBackIconClicked()
                }
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                SectionText(text = "Layout and aspect")
            }

            OptionRow(
                optionName = "Keyboard size",
                onClick = { openKeyboardSizeDialog.value = true }
            ) {
                InputChip(
                    selected = true,
                    onClick = { openKeyboardSizeDialog.value = true },
                    label = { Text(text = keyboardSizeRadioOptions.first { it.selected }.label) }
                )
            }

            OptionRow(
                optionName = "Double space character",
                onClick = { openDoubleSpaceDialog.value = true }
            ) {
                if (doubleSpaceCharacter != DoubleSpaceCharacter.NONE)
                    InputChip(
                        selected = true,
                        onClick = { openDoubleSpaceDialog.value = true },
                        label = {
                            Text(text = doubleSpaceRadioOptions.first { it.selected }.value.value)
                        }
                    )
            }

            OptionRow(
                optionName = "Start with manual",
                onClick = { onStartWithManualSelected(!startWithManual) }
            ) {
                Switch(
                    checked = startWithManual,
                    onCheckedChange = {
                        onStartWithManualSelected(it)
                    }
                )
            }

        }
    }
}

@Composable
fun SectionText(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp),
        style = MaterialTheme.typography.headlineMedium.plus(
            TextStyle(
                color = MaterialTheme.colorScheme.primary
            )
        )
    )
}

@Composable
fun OptionNameText (
    text: String
) {
    ResizableText(
        text = text
    )
}

@Composable
fun OptionRow (
    optionName: String,
    onClick: () -> Unit,
    optionDisplay: @Composable () -> Unit
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
            OptionNameText(text = optionName)
        }
        Column(
            Modifier
                .weight(2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            optionDisplay()
        }

    }
}

@Composable
fun OptionValueText (
    text: String
) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
}