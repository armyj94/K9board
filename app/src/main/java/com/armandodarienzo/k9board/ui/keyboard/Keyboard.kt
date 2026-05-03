package com.armandodarienzo.k9board.ui.keyboard

import android.os.Build
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
import com.armandodarienzo.k9board.keyboard.KeyboardIntent
import com.armandodarienzo.k9board.keyboard.KeyboardState
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.KeyboardCurrentView
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.model.KeyPopupProperties
import com.armandodarienzo.k9board.shared.*
import com.armandodarienzo.k9board.shared.extensions.ReverseArrangement
import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardKey
import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardRepeatableKey
import com.armandodarienzo.wear.utility.KeyOboard.ui.components.KeyboardTextKey
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun CustomKeyboardPreview() {
    CustomKeyboard(
        state = KeyboardState(backgroundColorId = android.R.color.system_accent1_50, languageSet = "us-US", keyboardSize = 280),
        onIntent = {},
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomKeyboard(
    modifier: Modifier = Modifier,
    state: KeyboardState,
    onIntent: (KeyboardIntent) -> Unit,
) {
    val backgroundColor: Color = colorResource(state.backgroundColorId.takeIf { it != 0 } ?: android.R.color.system_neutral2_50)
    var reverseLayout by remember { mutableStateOf(false) }
    var keyboardView = remember { mutableStateOf(KeyboardCurrentView.TEXT_VIEW) }

    val imeActionId = state.imeActionId
    val actionIconId = when (imeActionId) {
        EditorInfo.IME_ACTION_SEND -> R.drawable.ic_baseline_send_18
        EditorInfo.IME_ACTION_SEARCH -> R.drawable.ic_baseline_search_18
        EditorInfo.IME_ACTION_NEXT -> R.drawable.rounded_keyboard_double_arrow_right_24
        EditorInfo.IME_ACTION_GO -> R.drawable.outline_arrow_right_alt_24
        else -> R.drawable.rounded_subdirectory_arrow_left_24
    }
    val imeAction: () -> Unit = when (imeActionId) {
        EditorInfo.IME_ACTION_SEND,
        EditorInfo.IME_ACTION_SEARCH,
        EditorInfo.IME_ACTION_NEXT,
        EditorInfo.IME_ACTION_GO -> { { onIntent(KeyboardIntent.ImeActionPressed) } }
        else -> { { onIntent(KeyboardIntent.NewLinePressed) } }
    }

    val caps = state.capsStatus
    val isManual = state.isManual
    var shiftKeyTimer by remember { mutableLongStateOf(0L) }

    val languageSet = state.languageSet
    val keyboardSize = state.keyboardSize

    val key2text = if (languageSet == "ru-RU") "абвг" else KEY2_TEXT_LATIN
    val key3text = if (languageSet == "ru-RU") "дежз" else KEY3_TEXT_LATIN
    val key4text = if (languageSet == "ru-RU") "ийкл" else KEY4_TEXT_LATIN
    val key5text = if (languageSet == "ru-RU") "мноп" else KEY5_TEXT_LATIN
    val key6text = if (languageSet == "ru-RU") "рсту" else KEY6_TEXT_LATIN
    val key7text = if (languageSet == "ru-RU") "фхцч" else KEY7_TEXT_LATIN
    val key8text = if (languageSet == "ru-RU") "шщъы" else KEY8_TEXT_LATIN
    val key9text = if (languageSet == "ru-RU") "ьэюя" else KEY9_TEXT_LATIN

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(keyboardSize.dp)
            .then(modifier),
        color = backgroundColor
    ) {
        Row(
            Modifier.padding(top = 4.dp, bottom = 4.dp).fillMaxHeight(),
            horizontalArrangement = if (reverseLayout) ReverseArrangement else Arrangement.Start
        ) {
            Column(
                modifier = Modifier.weight(3f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (keyboardView.value == KeyboardCurrentView.TEXT_VIEW) {
                    Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        KeyboardKey(
                            modifier = Modifier.weight(1f).combinedClickable(
                                onClick = {
                                    val codes = codifyChars(if (caps == KeyboardCapsStatus.LOWER_CASE) KEY1_TEXT else KEY1_TEXT.uppercase(Locale.ROOT))
                                        .also { ASCII_CODE_1.let { n -> it.add(n) } }.toIntArray()
                                    if (isManual) onIntent(KeyboardIntent.ManualKeyPressed(codes, KEY1_ID))
                                    else onIntent(KeyboardIntent.T9KeyPressed(codes.last()))
                                },
                                onLongClick = {
                                    keyboardView.value = KeyboardCurrentView.SYMBOLS_VIEW
                                    onIntent(KeyboardIntent.EnterManualMode)
                                }
                            ),
                            text = KEY1_TEXT,
                        )
                        KeyboardTextKey(id = KEY2_ID, modifier = Modifier.weight(1f), text = key2text, capsStatus = caps, isManual = isManual, numberASCIIcode = ASCII_CODE_2, keyboardHeight = keyboardSize,
                            keyPopupProperties = KeyPopupProperties(Key2SpecialChars.VALUES, Alignment.BottomCenter, onIdSelected = { onIntent(KeyboardIntent.WriteSpecificChar(it)) }),
                            onKeyClick = { onIntent(KeyboardIntent.T9KeyPressed(it.last())) },
                            onManualKeyClick = { codes, id -> onIntent(KeyboardIntent.ManualKeyPressed(codes, id)) })
                        KeyboardTextKey(id = KEY3_ID, modifier = Modifier.weight(1f), text = key3text, capsStatus = caps, isManual = isManual, numberASCIIcode = ASCII_CODE_3, keyboardHeight = keyboardSize,
                            keyPopupProperties = KeyPopupProperties(Key3SpecialChars.VALUES, Alignment.BottomStart, onIdSelected = { onIntent(KeyboardIntent.WriteSpecificChar(it)) }),
                            onKeyClick = { onIntent(KeyboardIntent.T9KeyPressed(it.last())) },
                            onManualKeyClick = { codes, id -> onIntent(KeyboardIntent.ManualKeyPressed(codes, id)) })
                    }

                    Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        KeyboardTextKey(id = KEY4_ID, modifier = Modifier.weight(1f), text = key4text, capsStatus = caps, isManual = isManual, numberASCIIcode = ASCII_CODE_4, keyboardHeight = keyboardSize,
                            keyPopupProperties = KeyPopupProperties(Key4SpecialChars.VALUES, Alignment.CenterEnd, onIdSelected = { onIntent(KeyboardIntent.WriteSpecificChar(it)) }),
                            onKeyClick = { onIntent(KeyboardIntent.T9KeyPressed(it.last())) },
                            onManualKeyClick = { codes, id -> onIntent(KeyboardIntent.ManualKeyPressed(codes, id)) })
                        KeyboardTextKey(id = KEY5_ID, modifier = Modifier.weight(1f), text = key5text, capsStatus = caps, isManual = isManual, numberASCIIcode = ASCII_CODE_5, keyboardHeight = keyboardSize,
                            keyPopupProperties = KeyPopupProperties(Key5SpecialChars.VALUES, Alignment.Center, onIdSelected = { onIntent(KeyboardIntent.WriteSpecificChar(it)) }),
                            onKeyClick = { onIntent(KeyboardIntent.T9KeyPressed(it.last())) },
                            onManualKeyClick = { codes, id -> onIntent(KeyboardIntent.ManualKeyPressed(codes, id)) })
                        KeyboardTextKey(id = KEY6_ID, modifier = Modifier.weight(1f), text = key6text, capsStatus = caps, isManual = isManual, numberASCIIcode = ASCII_CODE_6, keyboardHeight = keyboardSize,
                            keyPopupProperties = KeyPopupProperties(Key6SpecialChars.VALUES, Alignment.CenterStart, onIdSelected = { onIntent(KeyboardIntent.WriteSpecificChar(it)) }),
                            onKeyClick = { onIntent(KeyboardIntent.T9KeyPressed(it.last())) },
                            onManualKeyClick = { codes, id -> onIntent(KeyboardIntent.ManualKeyPressed(codes, id)) })
                    }

                    Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        KeyboardTextKey(id = KEY7_ID, modifier = Modifier.weight(1f), text = key7text, capsStatus = caps, isManual = isManual, numberASCIIcode = ASCII_CODE_7, keyboardHeight = keyboardSize,
                            keyPopupProperties = KeyPopupProperties(Key7SpecialChars.VALUES, Alignment.TopEnd, onIdSelected = { onIntent(KeyboardIntent.WriteSpecificChar(it)) }),
                            onKeyClick = { onIntent(KeyboardIntent.T9KeyPressed(it.last())) },
                            onManualKeyClick = { codes, id -> onIntent(KeyboardIntent.ManualKeyPressed(codes, id)) })
                        KeyboardTextKey(id = KEY8_ID, modifier = Modifier.weight(1f), text = key8text, capsStatus = caps, isManual = isManual, numberASCIIcode = ASCII_CODE_8, keyboardHeight = keyboardSize,
                            keyPopupProperties = KeyPopupProperties(Key8SpecialChars.VALUES, Alignment.TopCenter, onIdSelected = { onIntent(KeyboardIntent.WriteSpecificChar(it)) }),
                            onKeyClick = { onIntent(KeyboardIntent.T9KeyPressed(it.last())) },
                            onManualKeyClick = { codes, id -> onIntent(KeyboardIntent.ManualKeyPressed(codes, id)) })
                        KeyboardTextKey(id = KEY9_ID, modifier = Modifier.weight(1f), text = key9text, capsStatus = caps, isManual = isManual, numberASCIIcode = ASCII_CODE_9, keyboardHeight = keyboardSize,
                            keyPopupProperties = KeyPopupProperties(Key9SpecialChars.VALUES, Alignment.TopStart, onIdSelected = { onIntent(KeyboardIntent.WriteSpecificChar(it)) }),
                            onKeyClick = { onIntent(KeyboardIntent.T9KeyPressed(it.last())) },
                            onManualKeyClick = { codes, id -> onIntent(KeyboardIntent.ManualKeyPressed(codes, id)) })
                    }

                    Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        KeyboardKey(
                            modifier = Modifier.weight(1f).combinedClickable(
                                onClick = {
                                    if (isManual) onIntent(KeyboardIntent.ExitManualMode)
                                    else onIntent(KeyboardIntent.SwapWord)
                                },
                                onLongClick = { onIntent(KeyboardIntent.EnterManualMode) }
                            ),
                            text = "sync",
                            iconID = if (isManual) R.drawable.ic_baseline_edit_note_24 else R.drawable.ic_sync_white_12dp,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        )
                        KeyboardKey(
                            modifier = Modifier.weight(1f).combinedClickable(
                                onClick = { onIntent(KeyboardIntent.SpacePressed) },
                                onDoubleClick = { onIntent(KeyboardIntent.DoubleSpacePressed) }
                            ),
                            text = "⎵",
                        )
                        KeyboardKey(
                            modifier = Modifier.weight(1f).clickable {
                                val nowMs = System.currentTimeMillis()
                                if (caps == KeyboardCapsStatus.LOWER_CASE) shiftKeyTimer = nowMs
                                onIntent(KeyboardIntent.ShiftToggled(shiftKeyTimer, nowMs))
                            },
                            text = "shift",
                            iconID = when (caps) {
                                KeyboardCapsStatus.UPPER_CASE -> R.drawable.ic_system_filled_shift_24px
                                KeyboardCapsStatus.CAPS_LOCK -> R.drawable.ic_system_filled_permanent_shift_24px
                                else -> R.drawable.ic_keyboard_capslock_white_18dp
                            },
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        )
                    }
                } else if (keyboardView.value == KeyboardCurrentView.EMOJI_VIEW) {
                    Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(4f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().fillMaxHeight().then(modifier)) {
                            EmojiPicker(onIntent)
                        }
                    }
                } else if (keyboardView.value == KeyboardCurrentView.NUMPAD_VIEW) {
                    Numpad(this, keyboardSize = keyboardSize, onIntent = onIntent)
                } else if (keyboardView.value == KeyboardCurrentView.SYMBOLS_VIEW) {
                    Symbolspad(this, keyboardSize = keyboardSize, keyboardCurrentView = keyboardView, onIntent = onIntent)
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeyboardKey(
                        modifier = Modifier.weight(1f).clickable { imeAction() },
                        text = "IMEAction",
                        iconID = actionIconId,
                        color = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary,
                        symbolsColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.inverseOnSurface,
                    )
                }
                Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeyboardRepeatableKey(
                        modifier = Modifier.weight(1f),
                        id = KEYDELETE_ID,
                        text = "canc",
                        iconID = R.drawable.ic_backspace_white_18dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        isRepeatableAction = { onIntent(KeyboardIntent.DeletePressed) }
                    )
                }
                Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeyboardKey(
                        text = if (keyboardView.value == KeyboardCurrentView.NUMPAD_VIEW) KEY2_TEXT_LATIN else "123",
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.weight(1f).clickable {
                            if (keyboardView.value == KeyboardCurrentView.NUMPAD_VIEW) {
                                onIntent(KeyboardIntent.ExitManualMode)
                                keyboardView.value = KeyboardCurrentView.TEXT_VIEW
                            } else {
                                keyboardView.value = KeyboardCurrentView.NUMPAD_VIEW
                                onIntent(KeyboardIntent.EnterManualMode)
                            }
                        }
                    )
                }
                Row(modifier = Modifier.padding(start = 2.dp, end = 2.dp).weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeyboardKey(
                        text = "emojis",
                        iconID = R.drawable.ic_insert_emoticon_white_18dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.weight(1f).clickable {
                            if (keyboardView.value == KeyboardCurrentView.EMOJI_VIEW)
                                keyboardView.value = KeyboardCurrentView.TEXT_VIEW
                            else
                                keyboardView.value = KeyboardCurrentView.EMOJI_VIEW
                        }
                    )
                }
            }
        }
    }
}
