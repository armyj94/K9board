package com.armandodarienzo.k9board.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.model.KeyboardSize

val options = listOf(
    RadioOption(KeyboardSize.SMALL, true),
    RadioOption(KeyboardSize.MEDIUM, false),
    RadioOption(KeyboardSize.LARGE, false)
)

@Preview
@Composable
fun RadioDialogPreview() {
    RadioDialog(
        title = "Select an option",
        options = options.toTypedArray(),
        onDismissRequest = {},
        onOptionSelected = {}
    )
}


@Composable
fun <T> RadioDialog(
    title: String,
    confirmButtonText: String = stringResource(id = R.string.confirm_text),
    options: Array<RadioOption<T>>,
    onDismissRequest: () -> Unit,
    onOptionSelected: (RadioOption<T>) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = option.selected,
                            onClick = {
                                options.forEach { it.selected = false }
                                option.selected = true
                                onOptionSelected(option)
                            }
                        )
                        Text(text = stringResource(id = option.labelId), modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text(text = confirmButtonText)
            }
        }
    )
}