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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.armandodarienzo.k9board.keyboard.KeyboardAction
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.KeyboardCurrentView
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
import com.armandodarienzo.k9board.shared.codifyChars
import com.armandodarienzo.k9board.shared.model.KeyPopupProperties
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Keypad(
    columnScope: ColumnScope,
    keyboardSize: Int,
    languageSet: String,
    isCaps: KeyboardCapsStatus?,
    isManual: Boolean,
    keyboardCurrentView: MutableState<KeyboardCurrentView>,
    onAction: (KeyboardAction) -> Unit = {},
) {
    val key2text = if (languageSet == "ru-RU") "абвг" else KEY2_TEXT_LATIN
    val key3text = if (languageSet == "ru-RU") "дежз" else KEY3_TEXT_LATIN
    val key4text = if (languageSet == "ru-RU") "ийкл" else KEY4_TEXT_LATIN
    val key5text = if (languageSet == "ru-RU") "мноп" else KEY5_TEXT_LATIN
    val key6text = if (languageSet == "ru-RU") "рсту" else KEY6_TEXT_LATIN
    val key7text = if (languageSet == "ru-RU") "фхцч" else KEY7_TEXT_LATIN
    val key8text = if (languageSet == "ru-RU") "шщъы" else KEY8_TEXT_LATIN
    val key9text = if (languageSet == "ru-RU") "ьэюя" else KEY9_TEXT_LATIN

    columnScope.apply {

        /*First row*/
        Row(
            modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyboardKey(
                modifier = Modifier.weight(1f).combinedClickable(
                    onClick = {
                        val codes = codifyChars(
                            if (isCaps == KeyboardCapsStatus.LOWER_CASE) KEY1_TEXT
                            else KEY1_TEXT.uppercase(Locale.ROOT)
                        ).also { ASCII_CODE_1.let { n -> it.add(n) } }.toIntArray()
                        if (isManual) onAction(KeyboardAction.ManualKeyPressed(codes, KEY1_ID))
                        else onAction(KeyboardAction.T9KeyPressed(codes.last()))
                    },
                    onLongClick = {
                        keyboardCurrentView.value = KeyboardCurrentView.SYMBOLS_VIEW
                        onAction(KeyboardAction.EnterManualMode)
                    }
                ),
                text = KEY1_TEXT,
            )
            KeyboardTextKey(id = KEY2_ID, modifier = Modifier.weight(1f), text = key2text, capsStatus = isCaps, isManual = isManual, numberASCIIcode = ASCII_CODE_2, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(Key2SpecialChars.VALUES, Alignment.BottomCenter, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.T9KeyPressed(it.last())) },
                onManualKeyClick = { codes, id -> onAction(KeyboardAction.ManualKeyPressed(codes, id)) })
            KeyboardTextKey(id = KEY3_ID, modifier = Modifier.weight(1f), text = key3text, capsStatus = isCaps, isManual = isManual, numberASCIIcode = ASCII_CODE_3, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(Key3SpecialChars.VALUES, Alignment.BottomStart, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.T9KeyPressed(it.last())) },
                onManualKeyClick = { codes, id -> onAction(KeyboardAction.ManualKeyPressed(codes, id)) })
        }

        /*Second row*/
        Row(
            modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyboardTextKey(id = KEY4_ID, modifier = Modifier.weight(1f), text = key4text, capsStatus = isCaps, isManual = isManual, numberASCIIcode = ASCII_CODE_4, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(Key4SpecialChars.VALUES, Alignment.CenterEnd, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.T9KeyPressed(it.last())) },
                onManualKeyClick = { codes, id -> onAction(KeyboardAction.ManualKeyPressed(codes, id)) })
            KeyboardTextKey(id = KEY5_ID, modifier = Modifier.weight(1f), text = key5text, capsStatus = isCaps, isManual = isManual, numberASCIIcode = ASCII_CODE_5, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(Key5SpecialChars.VALUES, Alignment.Center, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.T9KeyPressed(it.last())) },
                onManualKeyClick = { codes, id -> onAction(KeyboardAction.ManualKeyPressed(codes, id)) })
            KeyboardTextKey(id = KEY6_ID, modifier = Modifier.weight(1f), text = key6text, capsStatus = isCaps, isManual = isManual, numberASCIIcode = ASCII_CODE_6, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(Key6SpecialChars.VALUES, Alignment.CenterStart, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.T9KeyPressed(it.last())) },
                onManualKeyClick = { codes, id -> onAction(KeyboardAction.ManualKeyPressed(codes, id)) })
        }

        /*Third row*/
        Row(
            modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyboardTextKey(id = KEY7_ID, modifier = Modifier.weight(1f), text = key7text, capsStatus = isCaps, isManual = isManual, numberASCIIcode = ASCII_CODE_7, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(Key7SpecialChars.VALUES, Alignment.TopEnd, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.T9KeyPressed(it.last())) },
                onManualKeyClick = { codes, id -> onAction(KeyboardAction.ManualKeyPressed(codes, id)) })
            KeyboardTextKey(id = KEY8_ID, modifier = Modifier.weight(1f), text = key8text, capsStatus = isCaps, isManual = isManual, numberASCIIcode = ASCII_CODE_8, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(Key8SpecialChars.VALUES, Alignment.TopCenter, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.T9KeyPressed(it.last())) },
                onManualKeyClick = { codes, id -> onAction(KeyboardAction.ManualKeyPressed(codes, id)) })
            KeyboardTextKey(id = KEY9_ID, modifier = Modifier.weight(1f), text = key9text, capsStatus = isCaps, isManual = isManual, numberASCIIcode = ASCII_CODE_9, keyboardHeight = keyboardSize,
                keyPopupProperties = KeyPopupProperties(Key9SpecialChars.VALUES, Alignment.TopStart, onIdSelected = { onAction(KeyboardAction.WriteSpecificChar(it)) }),
                onKeyClick = { onAction(KeyboardAction.T9KeyPressed(it.last())) },
                onManualKeyClick = { codes, id -> onAction(KeyboardAction.ManualKeyPressed(codes, id)) })
        }

        /*4th row*/
        Row(
            modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyboardKey(
                modifier = Modifier.weight(1f).combinedClickable(
                    onClick = {
                        if (isManual) onAction(KeyboardAction.ExitManualMode)
                        else onAction(KeyboardAction.SwapWord)
                    },
                    onLongClick = { onAction(KeyboardAction.EnterManualMode) }
                ),
                text = "sync",
                iconID = if (isManual) R.drawable.ic_baseline_edit_note_24 else R.drawable.ic_sync_white_12dp,
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