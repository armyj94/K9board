package com.armandodarienzo.k9board.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

val options = listOf(
    RadioOption<Int>("Option 1", true, 1),
    RadioOption<Int>("Option 2", false, 2),
    RadioOption<Int>("Option 3", false, 3)
)

@Preview
@Composable
fun RadioDialogPreview() {
    RadioDialog<Int>(
        title = "Select an option",
        options = options.toTypedArray(),
        onDismissRequest = {},
        onOptionSelected = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RadioDialog(
    title: String,
    options: Array<RadioOption<T>>,
    onDismissRequest: () -> Unit,
    onOptionSelected: (RadioOption<T>) -> Unit
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
                        Text(text = option.label, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text(text = "OK")
            }
        }
    )
}