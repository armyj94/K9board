package com.armandodarienzo.k9board.ui.keyboard
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastJoinToString
import com.armandodarienzo.k9board.shared.model.KeyPopupProperties
import com.armandodarienzo.k9board.shared.KEY10_ID
import com.armandodarienzo.k9board.shared.NumpadKey10SpecialChars
import com.armandodarienzo.k9board.shared.service.Key9Service

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun NumpadPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Numpad(
            this,
            keyboardSize = 280
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Numpad(
    columnScope: ColumnScope,
    service: Key9Service? = null,
    keyboardSize: Int,
) {

    val key10_text = remember { NumpadKey10SpecialChars.VALUES.fastJoinToString("") }

    columnScope.apply {
        /*First row*/
        Row(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        service?.writeSpecificChar("1")
                    },
                text = "1",
            )
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        service?.writeSpecificChar("2")
                    },
                text = "2",
            )
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        service?.writeSpecificChar("3")
                    },
                text = "3",
            )
        }

        /*Second row*/
        Row(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        service?.writeSpecificChar("4")
                    },
                text = "4",
            )
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        service?.writeSpecificChar("5")
                    },
                text = "5",
            )
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        service?.writeSpecificChar("6")
                    },
                text = "6",
            )

        }

        /*Third row*/
        Row(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        service?.writeSpecificChar("7")
                    },
                text = "7",
            )
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        service?.writeSpecificChar("8")
                    },
                text = "8",
            )
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        service?.writeSpecificChar("9")
                    },
                text = "9",
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
                text = key10_text,
                isManual = service?.isManual?.value ?: false,
                keyboardHeight = keyboardSize,
                service = service,
                keyPopupProperties =
                KeyPopupProperties(
                    alignment = Alignment.TopEnd,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        service?.writeSpecificChar("0")
                    },
                text = "0",
            )

        }


    }
    


}