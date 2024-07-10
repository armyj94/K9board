package com.armandodarienzo.k9board.ui.keyboard

import android.os.Build
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.armandodarienzo.k9board.model.KeyPopupProperties
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardKey
import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardRepeatableKey
import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardTextKey
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.KeyboardCurrentView
import com.armandodarienzo.k9board.shared.service.Key9Service
import com.armandodarienzo.k9board.shared.*
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.ui.ReverseArrangement

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun CustomKeyboardPreview() {
    CustomKeyboard(
        backGroundColorId = android.R.color.system_accent1_50,
        languageSet = "us-US",
        keyboardSize = 280,
        hapticFeedback = false)
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun CustomKeyboardPreviewRU() {
    CustomKeyboard(backGroundColorId = android.R.color.system_accent1_50,
        languageSet = "ru-RU",
        keyboardSize = 280,
        hapticFeedback = false)
}


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomKeyboard(
    modifier: Modifier = Modifier,
    service: Key9Service? = null,
    backGroundColorId: Int,
    languageSet: String,
    keyboardSize: Int,
    hapticFeedback: Boolean,
) {
    val TAG = object {}::class.java.enclosingMethod?.name

    val backgroundColor: Color = colorResource(backGroundColorId)
    var reverseLayout by remember { mutableStateOf(false) }
    val collapsed by remember { mutableStateOf(false) }
    var keyboardView by remember { mutableStateOf(KeyboardCurrentView.TEXT_VIEW) }

    val actionId = service?.currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
    val actionIconId =
        when (actionId) {
            EditorInfo.IME_ACTION_SEND -> {
                R.drawable.ic_baseline_send_18
            }
            EditorInfo.IME_ACTION_SEARCH -> {
                R.drawable.ic_baseline_search_18
            }
            EditorInfo.IME_ACTION_NEXT -> {
                R.drawable.rounded_keyboard_double_arrow_right_24
            }
            EditorInfo.IME_ACTION_GO -> {
                R.drawable.outline_arrow_right_alt_24
            }
            else -> {
               R.drawable.rounded_subdirectory_arrow_left_24
            }
        }
    val imeAction =
        when (actionId) {
            EditorInfo.IME_ACTION_SEND,
            EditorInfo.IME_ACTION_SEARCH,
            EditorInfo.IME_ACTION_NEXT,
            EditorInfo.IME_ACTION_GO -> {
                { service.currentInputConnection?.performEditorAction(actionId) }
            }
            else -> {
                { service?.newLine() }
            }
        }

    val caps = service?.isCaps
    val isManual = service?.isManual
    var shiftKeyTimer by remember { mutableStateOf(0L) }

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

//    val visibleBox = remember { mutableStateOf(false) }


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(keyboardSize.dp)
            .then(modifier),
        color = backgroundColor
    ){

        Row(
            Modifier
                .padding(top = 4.dp, bottom = 4.dp)
                .fillMaxHeight(),
            horizontalArrangement =
                if(reverseLayout){
                    ReverseArrangement
                } else {
                    Arrangement.Start
                }
        ) {
            if (collapsed){
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    KeyboardKey(
                        modifier = Modifier.clickable{
                            reverseLayout = !reverseLayout
                        },
                        id = 101,
                        text = "Reverse layout",
                        iconID =
                            if(reverseLayout) R.drawable.ic_baseline_chevron_left_24
                            else R.drawable.ic_baseline_chevron_right_24,
                    )
                }
            }

            Column(
                modifier = Modifier.weight(3f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (keyboardView == KeyboardCurrentView.TEXT_VIEW) {
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
                            capsStatus = caps?.value,
                            service = service,
                            numberASCIIcode = ASCII_CODE_2,
                            keyboardHeight = keyboardSize,
                            keyPopupProperties =
                                KeyPopupProperties(
                                    KEY2_SPECIAL_CHARS.VALUES,
                                    Alignment.BottomCenter
                                )
                        )

                        KeyboardTextKey(
                            modifier = Modifier
                                .weight(1f),
                            id = KEY3_ID,
                            text = key3text,
                            capsStatus = caps?.value,
                            service = service,
                            numberASCIIcode = ASCII_CODE_3,
                            keyboardHeight = keyboardSize
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
                            capsStatus = caps?.value,
                            service = service,
                            numberASCIIcode = ASCII_CODE_4,
                            keyboardHeight = keyboardSize
                        )
                        KeyboardTextKey(
                            modifier = Modifier
                                .weight(1f),
                            id = KEY5_ID,
                            text = key5text,
                            capsStatus = caps?.value,
                            service = service,
                            numberASCIIcode = ASCII_CODE_5,
                            keyboardHeight = keyboardSize
                        )
                        KeyboardTextKey(
                            modifier = Modifier
                                .weight(1f),
                            id = KEY6_ID,
                            text = key6text,
                            capsStatus = caps?.value,
                            service = service,
                            numberASCIIcode = ASCII_CODE_6,
                            keyboardHeight = keyboardSize
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
                            capsStatus = caps?.value,
                            service = service,
                            numberASCIIcode = ASCII_CODE_7,
                            keyboardHeight = keyboardSize
                        )
                        KeyboardTextKey(
                            modifier = Modifier
                                .weight(1f),
                            id = KEY8_ID,
                            text = key8text,
                            capsStatus = caps?.value,
                            service = service,
                            numberASCIIcode = ASCII_CODE_8,
                            keyboardHeight = keyboardSize
                        )
                        KeyboardTextKey(
                            modifier = Modifier
                                .weight(1f),
                            id = KEY9_ID,
                            text = key9text,
                            capsStatus = caps?.value,
                            service = service,
                            numberASCIIcode = ASCII_CODE_9,
                            keyboardHeight = keyboardSize
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
                                .clickable {
                                    service?.swapClick()
                                    Log.d(
                                        object {}::class.java.enclosingMethod?.name,
                                        "isManual = $isManual"
                                    )
                                },
                            id = KEYSWAP_ID,
                            text = "sync",
                            iconID = if (isManual?.value == true) R.drawable.ic_baseline_edit_note_24 else R.drawable.ic_sync_white_12dp,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        )
                        KeyboardKey(
                            modifier = Modifier
                                .weight(1f)
                                .combinedClickable(
                                    onClick = {
                                        service?.spaceClick()
                                    }
                                ),
                            id = KEYSPACE_ID,
                            text = "⎵",
                            ratio = 3.3f
                        )
                        KeyboardKey(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {

                                    if (caps?.value == KeyboardCapsStatus.LOWER_CASE) {
                                        shiftKeyTimer = System.currentTimeMillis()
                                    }

                                    val nowInMillis = System.currentTimeMillis()

                                    caps?.value =
                                        if (nowInMillis - shiftKeyTimer < 500L && caps?.value == KeyboardCapsStatus.UPPER_CASE) {
                                            KeyboardCapsStatus.CAPS_LOCK
                                        } else if (caps?.value == KeyboardCapsStatus.LOWER_CASE) {
                                            KeyboardCapsStatus.UPPER_CASE
                                        } else {
                                            KeyboardCapsStatus.LOWER_CASE
                                        }

                                },
                            id = KEYSHIFT_ID,
                            text = "shift",
                            iconID =
                                when (caps?.value) {
                                    KeyboardCapsStatus.UPPER_CASE ->
                                        R.drawable.ic_system_filled_shift_24px
                                    KeyboardCapsStatus.CAPS_LOCK ->
                                        R.drawable.ic_system_filled_permanent_shift_24px
                                    else ->
                                        R.drawable.ic_keyboard_capslock_white_18dp
                                },
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        )

                    }
                } else if (keyboardView == KeyboardCurrentView.EMOJI_VIEW) {
                    Row(
                        modifier = Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .weight(4f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .then(modifier)
                        ){
                            EmojiPicker(service)
                        }

                    }

                }


            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
                                imeAction()
                            },
                        id = KEYACTION_ID,
                        text = "IMEAction",
                        iconID = actionIconId,
                        color =
                            if(!isSystemInDarkTheme())
                                MaterialTheme.colorScheme.inversePrimary
                            else
                                MaterialTheme.colorScheme.primary,
                        symbolsColor =
                            if(!isSystemInDarkTheme())
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.inverseOnSurface,

                            )

                }
                Row(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    KeyboardRepeatableKey(
                        modifier = Modifier
                            .weight(1f)
//                            .combinedClickable (
//                                onClick = {
//                                    service?.deleteChar()
//                                },
//                                onLongClick = {
//                                    service?.deleteChar()
//                                }
//                            ),
                             ,
                        id = KEYDELETE_ID,
                        text = "canc",
                        iconID = R.drawable.ic_backspace_white_18dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        isRepeatableAction = { service?.deleteChar() }
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    KeyboardKey(
                        id = 102,
                        text = "123",
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    KeyboardKey(
                        id = KEYEMOJI_ID,
                        text = "emojis",
                        iconID = R.drawable.ic_insert_emoticon_white_18dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (keyboardView == KeyboardCurrentView.EMOJI_VIEW)
                                    keyboardView = KeyboardCurrentView.TEXT_VIEW
                                else
                                    keyboardView = KeyboardCurrentView.EMOJI_VIEW
                            }
//                                    .aspectRatio(1f, false)

                    )
                }
            }
        }

    }

}


