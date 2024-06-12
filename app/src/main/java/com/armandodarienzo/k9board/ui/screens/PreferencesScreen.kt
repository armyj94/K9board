package com.armandodarienzo.k9board.ui.screens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.model.PreferencesOption
import com.armandodarienzo.k9board.shared.viewmodel.PreferencesViewModel
import com.armandodarienzo.k9board.ui.elements.AppBarIcon
import com.armandodarienzo.k9board.ui.elements.K9BoardTopAppBar
import com.armandodarienzo.k9board.ui.elements.RadioDialog
import com.armandodarienzo.k9board.ui.elements.RadioOption

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
    val keyboardSizeOptionName = stringResource(id = R.string.keyboard_size)
    val openKeyboardSizeDialog = remember { mutableStateOf(false) }
    val keyboardSizeRadioOptions = KeyboardSize.values().map {
        RadioOption(it, keyboardSize == it)
    }.toTypedArray()
    when {
        openKeyboardSizeDialog.value -> {
            RadioDialog(
                title = keyboardSizeOptionName ,
                options = keyboardSizeRadioOptions,
                onDismissRequest = {
                    openKeyboardSizeDialog.value = false
                }
            ) {
                onKeyboardSizeSelected(it.value as KeyboardSize)
            }
        }
    }

    val doubleSpaceOptionName = stringResource(id = R.string.double_space_character)
    val openDoubleSpaceDialog = remember { mutableStateOf(false) }
    val doubleSpaceRadioOptions = DoubleSpaceCharacter.values().map {
        RadioOption(it, doubleSpaceCharacter == it)
    }.toTypedArray()
    when {
        openDoubleSpaceDialog.value -> {
            RadioDialog(
                title = doubleSpaceOptionName ,
                options = doubleSpaceRadioOptions,
                onDismissRequest = {
                    openDoubleSpaceDialog.value = false
                }
            ) {
                onDoubleSpaceCharacterSelected(it.value as DoubleSpaceCharacter)
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
            SectionRow(text = stringResource(id = R.string.layout_and_aspect_section))

            OptionRow(
                optionName = keyboardSizeOptionName,
                onClick = { openKeyboardSizeDialog.value = true },
                option = keyboardSize
            )

            SectionSpacer()

            SectionRow(text = stringResource(id = R.string.functionalities_section))

            OptionRow(
                optionName = doubleSpaceOptionName,
                onClick = { openDoubleSpaceDialog.value = true },
                option = doubleSpaceCharacter
            )

            OptionRow(
                optionName = stringResource(id = R.string.start_with_manual),
                onClick = { onStartWithManualSelected(!startWithManual) },
                option = startWithManual
            )

        }
    }
}


@Composable
fun SectionRow(
    text: String
) {
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
fun SectionSpacer() {
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun OptionNameText (
    text: String
) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp),
        fontSize = 18.sp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OptionRow(
    optionName: String,
    onClick: () -> Unit,
    option: T
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
//            optionDisplay()
            if (option is Boolean) {
                Switch(
                    modifier =
                        Modifier
                            .padding(2.dp),
                    checked = option,
                    onCheckedChange = {
                        onClick()
                    }
                )
            } else if (option is PreferencesOption<*>) {
                InputChip(
                    selected = true,
                    onClick = { onClick() },
                    label = {
                        Text(
                            text = stringResource(id = option.getLabelId()),
                            fontSize = 12.sp)
                    }
                )
            } else throw IllegalArgumentException("Option type not supported.")

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionValueChip(
    text: String,
    onClick: () -> Unit
) {
    InputChip(
        selected = true,
        onClick = { onClick() },
        label = {
            Text(
                text = text,
                fontSize = 12.sp)
        }
    )
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