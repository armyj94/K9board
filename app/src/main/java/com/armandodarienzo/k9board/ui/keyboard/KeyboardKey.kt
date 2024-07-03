package com.armandodarienzo.wear.utility.KeyOboard.ui.components

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.armandodarienzo.k9board.shared.service.Key9Service
import com.armandodarienzo.k9board.shared.codifyChars
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.shared.KEY2_SPECIAL_CHARS
import com.armandodarienzo.k9board.ui.keyboard.PopupBox
import kotlinx.coroutines.delay
import java.util.*

@Preview
@Composable
fun KeyboardKeyPreview(){
    KeyboardKey(id = 0, text = "abc")
}

@Preview
@Composable
fun KeyboardIconKeyPreview(){
    KeyboardKey(id = 0, text = "check", iconID = com.google.android.material.R.drawable.mtrl_ic_check_mark)
}


@Composable
fun KeyboardKey(
    modifier: Modifier = Modifier,
    id: Int,
    text: String,
    iconID: Int? = null,
    iconAngle: Float = 0f,
    capsStatus: KeyboardCapsStatus? = KeyboardCapsStatus.LOWER_CASE,
    textStyle: TextStyle = TextStyle(),
    ratio: Float = 1f,
    color: Color =
        if(!isSystemInDarkTheme()) Color.White
        else MaterialTheme.colorScheme.inverseOnSurface,
    symbolsColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
//            .clip(RoundedCornerShape(30.dp))
            .fillMaxSize()
//            .clip(CircleShape)
//            .aspectRatio(ratio, true)
            .clip(RoundedCornerShape(10))
            .background(color)
            .then(modifier)
    ) {
        if(iconID == null) {
            Text(
//                modifier = Modifier
//                    .padding(10.dp),
                text = if (capsStatus == KeyboardCapsStatus.LOWER_CASE) text else text.uppercase(
                    Locale.ROOT
                ), //TODO: replace with ToUpperCaseByLanguageTag()
                style = textStyle,
                fontSize = 14.sp,
                color = symbolsColor
            )
        } else {
            Icon(
//                modifier = Modifier
//                    .padding(10.dp),
                modifier = Modifier
                    .fillMaxSize(0.6f)
                    .rotate(iconAngle),
                painter = painterResource(iconID),
                contentDescription = text,
                tint = symbolsColor
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun KeyboardRepeatableKey(
    modifier: Modifier = Modifier,
    id: Int,
    text: String,
    iconID: Int? = null,
    iconAngle: Float = 0f,
    capsStatus: KeyboardCapsStatus? = KeyboardCapsStatus.LOWER_CASE,
    textStyle: TextStyle = TextStyle(),
    ratio: Float = 1f,
    color: Color =
        if(!isSystemInDarkTheme()) Color.White
        else MaterialTheme.colorScheme.inverseOnSurface,
    symbolsColor: Color = MaterialTheme.colorScheme.onSurface,
    maxDelayMillis: Long = 200L,
    minDelayMillis: Long = 5L,
    delayDecayFactor: Float = 0.2f,
    isRepeatableAction : () -> Unit = { }
){
    val currentClickListener by rememberUpdatedState(isRepeatableAction)
    var pressed by remember { mutableStateOf(false) }

    LaunchedEffect(pressed) {
        var currentDelayMillis = maxDelayMillis

        while (pressed) {
            currentClickListener()
            delay(currentDelayMillis)
            currentDelayMillis =
                (currentDelayMillis - (currentDelayMillis * delayDecayFactor))
                    .toLong().coerceAtLeast(minDelayMillis)
        }
    }


    KeyboardKey(
        modifier =
            modifier
                .pointerInteropFilter {
                    pressed = when (it.action) {
                        MotionEvent.ACTION_DOWN -> true

                        else -> false
                    }

                    true
                },
        id = id,
        text = text,
        iconID = iconID,
        iconAngle = iconAngle,
        capsStatus = capsStatus,
        textStyle = textStyle,
        ratio = ratio,
        color = color,
        symbolsColor = symbolsColor)
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyboardTextKey(
    modifier: Modifier = Modifier,
    id: Int,
    text: String,
    iconID: Int? = null,
    iconAngle: Float = 0f,
    capsStatus: KeyboardCapsStatus? = KeyboardCapsStatus.LOWER_CASE,
    textStyle: TextStyle = TextStyle(),
    ratio: Float = 1f,
    color: Color =
        if(!isSystemInDarkTheme()) Color.White
        else MaterialTheme.colorScheme.inverseOnSurface,
    symbolsColor: Color = MaterialTheme.colorScheme.onSurface,
    service: Key9Service?,
    numberASCIIcode: Int,
){

    val visibleBox = remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        KeyboardKey(
            modifier =
                Modifier
                    .combinedClickable(
                        onClick = {
                            service?.keyClick(
                                codifyChars(
                                    if (capsStatus == KeyboardCapsStatus.LOWER_CASE) text
                                    else text.uppercase(Locale.ROOT)
                                )
                                    .also { it.add(numberASCIIcode) }
                                    .toIntArray(), false, numberASCIIcode)
                        },
                        onLongClick = {
                            visibleBox.value = true
                        }
                    ),
            id = id,
            text = text,
            iconID = iconID,
            iconAngle = iconAngle,
            capsStatus = capsStatus,
            textStyle = textStyle,
            ratio = ratio,
            color = color,
            symbolsColor = symbolsColor)

        PopupBox(
            characters = KEY2_SPECIAL_CHARS.VALUES,
            showPopup = visibleBox.value,
            onClickOutside = { visibleBox.value = false },
            color = color,
            capsStatus = capsStatus
        )
    }



}

