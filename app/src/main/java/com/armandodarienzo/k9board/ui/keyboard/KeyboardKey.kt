package com.armandodarienzo.wear.utility.KeyOboard.ui.components

import android.os.Build
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.armandodarienzo.k9board.shared.service.Key9Service
import com.armandodarienzo.k9board.shared.codifyChars
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.KeyPopupProperties
import com.armandodarienzo.k9board.ui.keyboard.PopupBox
import com.armandodarienzo.k9board.ui.keyboard.popupDragHandler
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.roundToInt

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
    symbolsColor: Color = MaterialTheme.colorScheme.onSurface
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
    keyboardHeight: Int,
    keyPopupProperties: KeyPopupProperties? = null
){

    val visibleBox = remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    var selectedId by rememberSaveable { mutableStateOf(0) }
    val boxOffset = remember { mutableStateOf(IntOffset.Zero) }
    var keySize by remember { mutableStateOf(IntSize.Zero) }

    val configuration = LocalConfiguration.current
    val popupWidth = (configuration.screenWidthDp / 1.5).dp
    val popupHeight = (keyboardHeight * 0.7).dp

    val popupWidthPx = with(LocalDensity.current) { popupWidth.toPx() }
    val popupHeightPx = with(LocalDensity.current) { popupHeight.toPx() }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                keySize = it.size

                val offsetX =
                    when (keyPopupProperties?.alignment) {
                        Alignment.TopStart, Alignment.CenterStart, Alignment.BottomStart,
                        Alignment.Start ->
                            -(keySize.width / 2f).roundToInt()
                        Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd, Alignment.End ->
                            (keySize.width / 2f).roundToInt()
                        Alignment.BottomCenter, Alignment.TopCenter, Alignment.Center -> {
                            - (popupWidthPx / 2f).roundToInt() + (keySize.width / 2f).roundToInt()
                        }

                    else -> 0
                }

                val offsetY =
                    when (keyPopupProperties?.alignment) {
                        Alignment.TopStart, Alignment.TopCenter,
                        Alignment.TopEnd, Alignment.Top -> -(keySize.height / 2f).roundToInt()
                        Alignment.BottomStart, Alignment.BottomCenter,
                        Alignment.BottomEnd, Alignment.Bottom -> (keySize.height / 2f).roundToInt()
                        Alignment.End, Alignment.Start, Alignment.Center ->
                            - (popupHeightPx / 2f).roundToInt() + (keySize.height / 2f).roundToInt()

                        else -> 0
                    }

                boxOffset.value = IntOffset(offsetX, offsetY)
//                boxOffset.value = IntOffset(0, 0)
            }
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
                )
                .popupDragHandler(
                    lazyGridState = gridState,
                    boxOffset = boxOffset,
                    setSelectedId = { selectedId = it },
                    closePopup = { visibleBox.value = false }
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

        keyPopupProperties?.let {
            val charList =
                text.toCharArray().map{ char -> char.toString() }.toMutableList().also { list ->
                    list.addAll(it.chars)
                }

            PopupBox(
                characters = charList,
                alignment = it.alignment,
                popupWidth = popupWidth,
                popupHeight = popupHeight,
                showPopup = visibleBox.value,
                onClickOutside = { visibleBox.value = false },
                color = color,
                boxOffset = boxOffset,
                gridState = gridState,
                selectedId = selectedId,
                capsStatus = capsStatus
            )
        }




    }



}

