package com.armandodarienzo.k9board.ui.keyboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.shared.ASCII_CODE_1
import com.armandodarienzo.k9board.shared.ASCII_CODE_2
import com.armandodarienzo.k9board.shared.ASCII_CODE_3
import com.armandodarienzo.k9board.shared.ASCII_CODE_4
import com.armandodarienzo.k9board.shared.ASCII_CODE_5
import com.armandodarienzo.k9board.shared.ASCII_CODE_6
import com.armandodarienzo.k9board.shared.ASCII_CODE_7
import com.armandodarienzo.k9board.shared.ASCII_CODE_8
import com.armandodarienzo.k9board.shared.ASCII_CODE_9
import com.armandodarienzo.k9board.shared.KEY1_ID
import com.armandodarienzo.k9board.shared.KEY1_TEXT
import com.armandodarienzo.k9board.shared.KEY2_ID
import com.armandodarienzo.k9board.shared.KEY2_TEXT_LATIN
import com.armandodarienzo.k9board.shared.KEY3_ID
import com.armandodarienzo.k9board.shared.KEY3_TEXT_LATIN
import com.armandodarienzo.k9board.shared.KEY4_ID
import com.armandodarienzo.k9board.shared.KEY4_TEXT_LATIN
import com.armandodarienzo.k9board.shared.KEY5_ID
import com.armandodarienzo.k9board.shared.KEY5_TEXT_LATIN
import com.armandodarienzo.k9board.shared.KEY6_ID
import com.armandodarienzo.k9board.shared.KEY6_TEXT_LATIN
import com.armandodarienzo.k9board.shared.KEY7_ID
import com.armandodarienzo.k9board.shared.KEY7_TEXT_LATIN
import com.armandodarienzo.k9board.shared.KEY8_ID
import com.armandodarienzo.k9board.shared.KEY8_TEXT_LATIN
import com.armandodarienzo.k9board.shared.KEY9_ID
import com.armandodarienzo.k9board.shared.KEY9_TEXT_LATIN
import com.armandodarienzo.k9board.shared.Key2SpecialChars
import com.armandodarienzo.k9board.shared.Key3SpecialChars
import com.armandodarienzo.k9board.shared.Key4SpecialChars
import com.armandodarienzo.k9board.shared.Key5SpecialChars
import com.armandodarienzo.k9board.shared.Key6SpecialChars
import com.armandodarienzo.k9board.shared.Key7SpecialChars
import com.armandodarienzo.k9board.shared.Key8SpecialChars
import com.armandodarienzo.k9board.shared.Key9SpecialChars
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.model.KeyPopupProperties
import com.armandodarienzo.k9board.shared.service.Key9Service

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Keypad(
    columnScope: ColumnScope,
    service: Key9Service? = null,
    keyboardSize: Int,
    languageSet: String,
    isCaps: KeyboardCapsStatus?,
    isManual: Boolean?
) {

    val key2text = when(languageSet){
        "ru-RU" -> "абвг"
        else -> KEY2_TEXT_LATIN
    }

    val key3text = when(languageSet){
        "ru-RU" -> "дежз"
        else -> KEY3_TEXT_LATIN
    }

    val key4text = when(languageSet){
        "ru-RU" -> "ийкл"
        else -> KEY4_TEXT_LATIN
    }

    val key5text = when(languageSet){
        "ru-RU" -> "мноп"
        else -> KEY5_TEXT_LATIN
    }

    val key6text = when(languageSet){
        "ru-RU" -> "рсту"
        else -> KEY6_TEXT_LATIN
    }

    val key7text = when(languageSet){
        "ru-RU" -> "фхцч"
        else -> KEY7_TEXT_LATIN
    }

    val key8text = when(languageSet){
        "ru-RU" -> "шщъы"
        else -> KEY8_TEXT_LATIN
    }

    val key9text = when(languageSet){
        "ru-RU" -> "ьэюя"
        else -> KEY9_TEXT_LATIN
    }

    columnScope.apply {

        /*First row*/
        Row(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY1_ID,
                text = KEY1_TEXT,
                service = service,
                numberASCIIcode = ASCII_CODE_1,
                keyboardHeight = keyboardSize
            )
            KeyboardTextKey(
                id = KEY2_ID,
                modifier = Modifier
                    .weight(1f),
                text = key2text,
                capsStatus = isCaps,
                service = service,
                numberASCIIcode = ASCII_CODE_2,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                    KeyPopupProperties(
                        Key2SpecialChars.VALUES,
                        Alignment.BottomCenter,
                        onIdSelected = { service?.writeSpecificChar(it) }
                    )
            )

            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY3_ID,
                text = key3text,
                capsStatus = isCaps,
                service = service,
                numberASCIIcode = ASCII_CODE_3,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                    KeyPopupProperties(
                        Key3SpecialChars.VALUES,
                        Alignment.BottomStart,
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
                capsStatus = isCaps,
                service = service,
                numberASCIIcode = ASCII_CODE_4,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                    KeyPopupProperties(
                        Key4SpecialChars.VALUES,
                        Alignment.CenterEnd,
                        onIdSelected = { service?.writeSpecificChar(it) }
                    )
            )
            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY5_ID,
                text = key5text,
                capsStatus = isCaps,
                service = service,
                numberASCIIcode = ASCII_CODE_5,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                KeyPopupProperties(
                    Key5SpecialChars.VALUES,
                    Alignment.Center,
                    onIdSelected = { service?.writeSpecificChar(it) }
                )
            )
            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY6_ID,
                text = key6text,
                capsStatus = isCaps,
                service = service,
                numberASCIIcode = ASCII_CODE_6,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                    KeyPopupProperties(
                        Key6SpecialChars.VALUES,
                        Alignment.CenterStart,
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
                capsStatus = isCaps,
                service = service,
                numberASCIIcode = ASCII_CODE_7,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                    KeyPopupProperties(
                        Key7SpecialChars.VALUES,
                        Alignment.TopEnd,
                        onIdSelected = { service?.writeSpecificChar(it) }
                    )
            )
            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY8_ID,
                text = key8text,
                capsStatus = isCaps,
                service = service,
                numberASCIIcode = ASCII_CODE_8,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                    KeyPopupProperties(
                        Key8SpecialChars.VALUES,
                        Alignment.TopCenter,
                        onIdSelected = { service?.writeSpecificChar(it) }
                    )
            )
            KeyboardTextKey(
                modifier = Modifier
                    .weight(1f),
                id = KEY9_ID,
                text = key9text,
                capsStatus = isCaps,
                service = service,
                numberASCIIcode = ASCII_CODE_9,
                keyboardHeight = keyboardSize,
                keyPopupProperties =
                    KeyPopupProperties(
                        Key9SpecialChars.VALUES,
                        Alignment.TopStart,
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
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = {
                            if (isManual == true) {
                                service?.exitManualMode()
                            } else {
                                service?.swapClick()
                            }
                        },
                        onLongClick = {
                            service?.enterManualMode()
                        }
                    ),
                text = "sync",
                iconID = if (isManual == true) R.drawable.ic_baseline_edit_note_24 else R.drawable.ic_sync_white_12dp,
//                            color = MaterialTheme.colorScheme.secondaryContainer,
            )
            KeyboardKey(
                modifier = Modifier
                    .weight(1f)
                    .combinedClickable(
                        onClick = {
                            service?.spaceClick()
                        }
                    ),
                text = "⎵",
            )

        }

    }



}