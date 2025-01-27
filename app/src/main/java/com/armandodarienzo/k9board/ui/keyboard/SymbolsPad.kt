package com.armandodarienzo.k9board.ui.keyboard
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import com.armandodarienzo.k9board.model.KeyboardCurrentView
import com.armandodarienzo.k9board.shared.model.KeyPopupProperties
import com.armandodarienzo.k9board.shared.KEY10_ID
import com.armandodarienzo.k9board.shared.KEY10_TEXT_SYMBOLS
import com.armandodarienzo.k9board.shared.KEY1_ID
import com.armandodarienzo.k9board.shared.KEY1_TEXT_SYMBOLS
import com.armandodarienzo.k9board.shared.KEY2_ID
import com.armandodarienzo.k9board.shared.KEY2_TEXT_SYMBOLS
import com.armandodarienzo.k9board.shared.KEY3_ID
import com.armandodarienzo.k9board.shared.KEY3_TEXT_SYMBOLS
import com.armandodarienzo.k9board.shared.KEY4_ID
import com.armandodarienzo.k9board.shared.KEY4_TEXT_SYMBOLS
import com.armandodarienzo.k9board.shared.KEY5_ID
import com.armandodarienzo.k9board.shared.KEY5_TEXT_SYMBOLS
import com.armandodarienzo.k9board.shared.KEY6_ID
import com.armandodarienzo.k9board.shared.KEY6_TEXT_SYMBOLS
import com.armandodarienzo.k9board.shared.KEY7_ID
import com.armandodarienzo.k9board.shared.KEY7_TEXT_SYMBOLS
import com.armandodarienzo.k9board.shared.KEY8_ID
import com.armandodarienzo.k9board.shared.KEY8_TEXT_SYMBOLS
import com.armandodarienzo.k9board.shared.KEY9_ID
import com.armandodarienzo.k9board.shared.KEY9_TEXT_SYMBOLS
import com.armandodarienzo.k9board.shared.NumpadKey10SpecialChars
import com.armandodarienzo.k9board.shared.service.Key9Service
import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardKey
import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardTextKey

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun SymbolspadPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Symbolspad(
            this,
            keyboardSize = 280
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Symbolspad(
    columnScope: ColumnScope,
    service: Key9Service? = null,
    keyboardSize: Int,
    keyboardCurrentView: MutableState<KeyboardCurrentView>? = null
) {

    val key1text = KEY1_TEXT_SYMBOLS
    val key2text = KEY2_TEXT_SYMBOLS
    val key3text = KEY3_TEXT_SYMBOLS
    val key4text = KEY4_TEXT_SYMBOLS
    val key5text = KEY5_TEXT_SYMBOLS
    val key6text = KEY6_TEXT_SYMBOLS
    val key7text = KEY7_TEXT_SYMBOLS
    val key8text = KEY8_TEXT_SYMBOLS
    val key9text = KEY9_TEXT_SYMBOLS
    val key10text = KEY10_TEXT_SYMBOLS

    columnScope.apply {
        /*First row*/
        Row(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            KeyboardTextKey(
                id = KEY1_ID,
                modifier = Modifier
                    .weight(1f),
                text = key1text,
                capsStatus = null,
                service = service,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.BottomEnd,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )
            KeyboardTextKey(
                id = KEY2_ID,
                modifier = Modifier
                    .weight(1f),
                text = key2text,
                capsStatus = null,
                service = service,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.BottomCenter,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )

            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY3_ID,
                text = key3text,
                service = service,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.BottomStart,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )
        }

        /*Second row*/
        Row(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY4_ID,
                text = key4text,
                service = service,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.CenterEnd,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )
            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY5_ID,
                text = key5text,
                service = service,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.Center,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )
            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY6_ID,
                text = key6text,
                service = service,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.CenterStart,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )

        }

        /*Third row*/
        Row(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY7_ID,
                text = key7text,
                service = service,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.TopEnd,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )
            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY8_ID,
                text = key8text,
                service = service,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.TopCenter,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )
            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY9_ID,
                text = key9text,
                service = service,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.TopStart,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )

        }

        /*4th row*/
        Row(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY10_ID,
                text = key10text,
                service = service,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.TopEnd,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = {
                            service?.spaceClick()
                        },
                        onDoubleClick = {
                            service?.doubleSpaceClick()
                        }
                    ),
                text = "⎵",
            )
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = {
                            keyboardCurrentView!!.value = KeyboardCurrentView.TEXT_VIEW
                            service?.exitManualMode()
                        },
                    ),
                text = "abc",
                color = MaterialTheme.colorScheme.secondaryContainer
            )

        }


    }
    


}