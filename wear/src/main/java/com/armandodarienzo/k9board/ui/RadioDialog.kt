package com.armandodarienzo.k9board.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.wear.compose.material.RadioButton
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.model.RadioOption

val options = listOf(
    RadioOption(KeyboardSize.SMALL, true),
    RadioOption(KeyboardSize.MEDIUM, false),
    RadioOption(KeyboardSize.LARGE, false)
)

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun RadioDialogPreview() {
    RadioDialog(
        options = options.toTypedArray(),
        onDismissRequest = {},
        onOptionSelected = {}
    )
}


@Composable
fun <T> RadioDialog(
    options: Array<RadioOption<T>>,
    onDismissRequest: () -> Unit,
    onOptionSelected: (RadioOption<T>) -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Scaffold(
            modifier = Modifier
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 30.dp,
                        end = 30.dp
                    ),
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                options.forEach { it.selected = false }
                                option.selected = true
                                onOptionSelected(option)
                                onDismissRequest()
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = option.selected,
                            onClick = {
                                options.forEach { it.selected = false }
                                option.selected = true
                                onOptionSelected(option)
                                onDismissRequest()
                            }
                        )
                        Text(text = stringResource(id = option.labelId), modifier = Modifier.padding(start = 8.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

    }
}