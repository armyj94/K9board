package com.armandodarienzo.k9board.presentation.keyboard

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.setText
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.widget.addTextChangedListener
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
//import com.armandodarienzo.k9board.model.KeyPopupProperties
import com.armandodarienzo.k9board.shared.R
//import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardKey
//import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardRepeatableKey
//import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardTextKey
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.KeyboardCurrentView
import com.armandodarienzo.k9board.shared.service.Key9Service
import com.armandodarienzo.k9board.shared.*
import com.armandodarienzo.k9board.shared.model.KeyPopupProperties

//import com.armandodarienzo.k9board.ui.ReverseArrangement

@RequiresApi(Build.VERSION_CODES.S)
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
private fun SmallRound() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ){
        CustomKeyboard(
            Modifier.align(Alignment.BottomCenter),
            backGroundColorId = android.R.color.system_accent1_50,
            languageSet = "us-US",
            keyboardSize = 134,
            hapticFeedback = false)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(device = WearDevices.SQUARE, showSystemUi = true)
@Composable
private fun Square() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ){
        CustomKeyboard(
            Modifier.align(Alignment.BottomCenter),
            backGroundColorId = android.R.color.system_accent1_50,
            languageSet = "us-US",
            keyboardSize = 140,
            hapticFeedback = false)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
private fun CyrillicSmall() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ){
        CustomKeyboard(
            Modifier.align(Alignment.BottomCenter),
            backGroundColorId = android.R.color.system_accent1_50,
            languageSet = "ru-RU",
            keyboardSize = 140,
            hapticFeedback = false)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Composable
private fun LargeRound() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ){
        CustomKeyboard(
            Modifier.align(Alignment.BottomCenter),
            backGroundColorId = android.R.color.system_accent1_50,
            languageSet = "us-US",
            keyboardSize = 158,
            hapticFeedback = false)
    }
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



//    val visibleBox = remember { mutableStateOf(false) }

//    TimeText(
//        Modifier
//            .zIndex(1f)
//    )
    Box(
        modifier = Modifier
            .height(keyboardSize.dp)
            .then(modifier)
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ){

            Row(
                horizontalArrangement = Arrangement.Start,
            ) {

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ){
                        KeyboardKey(
                            text =
                            if (keyboardView == KeyboardCurrentView.NUMPAD_VIEW) {
                                KEY2_TEXT_LATIN
                            } else if (keyboardView == KeyboardCurrentView.EMOJI_VIEW) {
                                "123"
                            } else {
                                "emojis"
                            },
                            iconID =
                            if (keyboardView == KeyboardCurrentView.TEXT_VIEW)
                                R.drawable.ic_insert_emoticon_white_18dp
                            else
                                null,
                            //color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (keyboardView == KeyboardCurrentView.NUMPAD_VIEW) {
                                        service?.exitManualMode()
                                        keyboardView = KeyboardCurrentView.TEXT_VIEW
                                    } else if (keyboardView == KeyboardCurrentView.EMOJI_VIEW) {
                                        keyboardView = KeyboardCurrentView.NUMPAD_VIEW
                                        service?.enterManualMode()
                                    } else {
                                        keyboardView = KeyboardCurrentView.EMOJI_VIEW
                                    }
                                }
                        )

                    }
                    Row(
                        modifier = Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if(keyboardView == KeyboardCurrentView.TEXT_VIEW) {

                            KeyboardKey(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {

                                        if (caps?.value == KeyboardCapsStatus.LOWER_CASE) {
                                            shiftKeyTimer = System.currentTimeMillis()
                                        }

                                        val nowInMillis = System.currentTimeMillis()

                                        caps?.value =
                                            if (
                                                (nowInMillis - shiftKeyTimer < 500L
                                                        && caps?.value == KeyboardCapsStatus.UPPER_CASE)
                                                || (isManual?.value == true
                                                        && caps?.value == KeyboardCapsStatus.LOWER_CASE)
                                            ) {
                                                KeyboardCapsStatus.CAPS_LOCK
                                            } else if (caps?.value == KeyboardCapsStatus.LOWER_CASE) {
                                                KeyboardCapsStatus.UPPER_CASE
                                            } else {
                                                KeyboardCapsStatus.LOWER_CASE
                                            }

                                    },
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
//                            color = MaterialTheme.colorScheme.secondaryContainer,
                            )

                        } else {

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
                    Row(
                        modifier = Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ){

                    }

                }

                Column(
                    modifier = Modifier.weight(3f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (keyboardView == KeyboardCurrentView.TEXT_VIEW) {
                        Keypad(
                            this,
                            service = service,
                            keyboardSize = keyboardSize,
                            languageSet = languageSet,
                            isCaps = caps?.value,
                            isManual = isManual?.value
                        )
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
                            ){
                                EmojiPicker(service)
                            }

                        }

                    } else if (keyboardView == KeyboardCurrentView.NUMPAD_VIEW) {
                        Numpad(
                            this,
                            service = service,
                            keyboardSize = keyboardSize,
                        )
                    }

                }
//
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
                            text = "IMEAction",
                            iconID = actionIconId,
                            symbolsColor = MaterialTheme.colors.primaryVariant,
//                        color =
//                            if(!isSystemInDarkTheme())
//                                MaterialTheme.colorScheme.inversePrimary
//                            else
//                                MaterialTheme.colorScheme.primary,
//                        symbolsColor =
//                            if(!isSystemInDarkTheme())
//                                MaterialTheme.colorScheme.onSurface
//                            else
//                                MaterialTheme.colorScheme.inverseOnSurface,

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
//                        color = MaterialTheme.colorScheme.secondaryContainer,
                            isRepeatableAction = { service?.deleteChar() }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(start = 2.dp, end = 2.dp)
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                    }
                }
            }

        }

    }



}