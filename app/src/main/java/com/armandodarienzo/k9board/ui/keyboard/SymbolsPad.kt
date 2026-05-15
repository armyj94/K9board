package com.armandodarienzo.k9board.ui.keyboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.armandodarienzo.k9board.keyboard.KeyboardAction
import com.armandodarienzo.k9board.model.KeyboardCurrentView
import com.armandodarienzo.k9board.shared.model.KeyPopupProperties
import com.armandodarienzo.k9board.shared.*
import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardKey
import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardTextKey

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun SymbolspadPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Symbolspad(this, keyboardSize = 280)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Symbolspad(
    columnScope: ColumnScope,
    keyboardSize: Int,
    keyboardCurrentView: MutableState<KeyboardCurrentView>? = null,
    onAction: (KeyboardAction) -> Unit = {},
) {
    columnScope.apply {
        Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            KeyboardTextKey(id = KEY1_ID, modifier = Modifier.weight(1f), text = KEY1_TEXT_SYMBOLS, capsStatus = null, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(alignment = Alignment.BottomEnd, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.WriteSpecificChar(it.firstOrNull()?.toChar()?.toString() ?: "")) })
            KeyboardTextKey(id = KEY2_ID, modifier = Modifier.weight(1f), text = KEY2_TEXT_SYMBOLS, capsStatus = null, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(alignment = Alignment.BottomCenter, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.WriteSpecificChar(it.firstOrNull()?.toChar()?.toString() ?: "")) })
            KeyboardTextKey(id = KEY3_ID, modifier = Modifier.weight(1f), text = KEY3_TEXT_SYMBOLS, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(alignment = Alignment.BottomStart, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.WriteSpecificChar(it.firstOrNull()?.toChar()?.toString() ?: "")) })
        }

        Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            KeyboardTextKey(id = KEY4_ID, modifier = Modifier.weight(1f), text = KEY4_TEXT_SYMBOLS, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(alignment = Alignment.CenterEnd, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.WriteSpecificChar(it.firstOrNull()?.toChar()?.toString() ?: "")) })
            KeyboardTextKey(id = KEY5_ID, modifier = Modifier.weight(1f), text = KEY5_TEXT_SYMBOLS, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(alignment = Alignment.Center, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.WriteSpecificChar(it.firstOrNull()?.toChar()?.toString() ?: "")) })
            KeyboardTextKey(id = KEY6_ID, modifier = Modifier.weight(1f), text = KEY6_TEXT_SYMBOLS, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(alignment = Alignment.CenterStart, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.WriteSpecificChar(it.firstOrNull()?.toChar()?.toString() ?: "")) })
        }

        Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            KeyboardTextKey(id = KEY7_ID, modifier = Modifier.weight(1f), text = KEY7_TEXT_SYMBOLS, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(alignment = Alignment.TopEnd, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.WriteSpecificChar(it.firstOrNull()?.toChar()?.toString() ?: "")) })
            KeyboardTextKey(id = KEY8_ID, modifier = Modifier.weight(1f), text = KEY8_TEXT_SYMBOLS, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(alignment = Alignment.TopCenter, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.WriteSpecificChar(it.firstOrNull()?.toChar()?.toString() ?: "")) })
            KeyboardTextKey(id = KEY9_ID, modifier = Modifier.weight(1f), text = KEY9_TEXT_SYMBOLS, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(alignment = Alignment.TopStart, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.WriteSpecificChar(it.firstOrNull()?.toChar()?.toString() ?: "")) })
        }

        Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            KeyboardKey(
                modifier = Modifier.weight(1f).combinedClickable(onClick = {
                    keyboardCurrentView?.value = KeyboardCurrentView.TEXT_VIEW
                    onAction(KeyboardAction.ExitManualMode)
                }),
                text = "abc",
                color = MaterialTheme.colorScheme.secondaryContainer
            )
            KeyboardKey(
                modifier = Modifier.weight(1f).combinedClickable(
                    onClick = { onAction(KeyboardAction.SpacePressed) },
                    onDoubleClick = { onAction(KeyboardAction.DoubleSpacePressed) }
                ),
                text = "⎵",
            )
        }
    }
}
