package com.armandodarienzo.k9board.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Switch

import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.items
import androidx.wear.compose.material.scrollAway
import androidx.wear.tooling.preview.devices.WearDevices
import com.armandodarienzo.k9board.model.MainMenuItem
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE
import com.armandodarienzo.k9board.shared.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.model.PreferencesMenuItem
import com.armandodarienzo.k9board.shared.model.RadioOption
import com.armandodarienzo.k9board.shared.ui.navigation.Screens
import com.armandodarienzo.k9board.shared.viewmodel.HomeScreenViewModel
import com.armandodarienzo.k9board.shared.viewmodel.PreferencesViewModel
import com.armandodarienzo.k9board.ui.RadioDialog

@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Composable
fun PreferencesContentPreview() {
    PreferenceScreenContentWear(
        doubleSpaceCharacter = DoubleSpaceCharacter.COMMA,
        onDoubleSpaceCharacterSelected = {},
        startWithManual = false,
        onStartWithManualSelected = {}
    )
}

@Composable
fun PreferencesScreen(
    navController: NavController,
    viewModel: PreferencesViewModel = hiltViewModel()
) {
    val doubleSpaceCharacter = viewModel.doubleSpaceCharState.value
    val onDoubleSpaceCharacterSelected : (DoubleSpaceCharacter) -> Unit = {
        viewModel.setDoubleSpaceChar(it)
    }

    val startWithManual = viewModel.startWithManualState.value
    val onStartWithManualSelected : (Boolean) -> Unit = {
        viewModel.setStartWithManual(it)
    }

    PreferenceScreenContentWear(
        doubleSpaceCharacter,
        onDoubleSpaceCharacterSelected,
        startWithManual,
        onStartWithManualSelected
    )
}

@Composable
fun PreferenceScreenContentWear(
    doubleSpaceCharacter: DoubleSpaceCharacter,
    onDoubleSpaceCharacterSelected : (DoubleSpaceCharacter) -> Unit,
    startWithManual : Boolean,
    onStartWithManualSelected : (Boolean) -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)

    val preferencesItems = mutableListOf<PreferencesMenuItem<Any>>()

    val doubleSpaceOptionName = stringResource(id = R.string.double_space_character)
    val openDoubleSpaceDialog = remember { mutableStateOf(false) }
    val doubleSpaceRadioOptions = DoubleSpaceCharacter.entries.map {
        RadioOption(it, doubleSpaceCharacter == it)
    }.toTypedArray()
    when {
        openDoubleSpaceDialog.value -> {
            RadioDialog(
                options = doubleSpaceRadioOptions,
                onDismissRequest = {
                    openDoubleSpaceDialog.value = false
                }
            ) {
                onDoubleSpaceCharacterSelected(it.value as DoubleSpaceCharacter)
            }
        }
    }

    preferencesItems.add(
        PreferencesMenuItem(
            name = doubleSpaceOptionName,
            value = stringResource(doubleSpaceCharacter.getLabelId()),
            onClick = { openDoubleSpaceDialog.value = true }
        )
    )


    val autoCapsOptionName = stringResource(id = R.string.auto_caps)

    preferencesItems.add(
        PreferencesMenuItem(
            name = autoCapsOptionName,
            value = false,
            onClick = { }
        )
    )


    Scaffold(
        modifier = Modifier
            .background(Color.Black),
        timeText = { TimeText(Modifier.scrollAway(listState)) }, // Display current time
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }, // Add vignette effect
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = listState)
        } // Show position indicator
    ) {
        ScalingLazyColumn(
            contentPadding = PaddingValues(
                top = (screenHeight * 0.21).dp,
                bottom = (screenHeight * 0.36).dp,
                start = (screenHeight * 0.05).dp,
                end = (screenHeight * 0.05).dp
            ),
            state = listState
        ) {
            items(preferencesItems) { preferenceItem ->
                PreferencesElementWear(
                    modifier = Modifier.height(80.dp),
                    onClick = { preferenceItem.onClick() },
                    elementName = preferenceItem.name,
                    elementValue = preferenceItem.value
                )
            }


        }
    }
}

@Composable
fun <T> PreferencesElementWear(
    modifier: Modifier = Modifier,
    onClick : () -> Unit,
    elementName: String,
    elementValue: T? = null
) {
    if (elementValue is String) {
        Chip(
            onClick = { onClick() },
            modifier = modifier
                .fillMaxWidth()
                .padding(),
            label = {
                Text(
                    text = elementName,
                    style = MaterialTheme.typography.body1, // Use a suitable text style for Wear OS
                    color = MaterialTheme.colors.onSurface
                )
            },
            secondaryLabel = if(elementValue.isNotBlank()){
                {
                    Text(
                        text = elementValue,
                        style = MaterialTheme.typography.caption2, // Use a suitable text style for Wear OS
                        color = MaterialTheme.colors.onSurface
                    )
                }
            } else {
                null
            },
            colors = ChipDefaults.secondaryChipColors()
        )
    } else if (elementValue is Boolean) {
        ToggleChip(
            checked = elementValue,
            onCheckedChange = { onClick() },
            modifier = modifier
                .fillMaxWidth()
                .padding(),
            label = {
                Text(
                    text = elementName,
                    style = MaterialTheme.typography.body1, // Use a suitable text style for Wear OS
                    color = MaterialTheme.colors.onSurface
                )
            },
            toggleControl = {
                Switch(
                    checked = elementValue,
                    onCheckedChange = { onClick() }
                )
            }
        )
    }


}