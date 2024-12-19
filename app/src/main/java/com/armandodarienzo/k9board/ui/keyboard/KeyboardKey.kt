package com.armandodarienzo.wear.utility.KeyOboard.ui.components

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.armandodarienzo.k9board.shared.service.Key9Service
import com.armandodarienzo.k9board.shared.codifyChars
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.shared.model.KeyPopupProperties
import com.armandodarienzo.k9board.shared.KEYBOARD_POPUP_MAX_COLUMNS
import com.armandodarienzo.k9board.shared.extensions.applyIf
import com.armandodarienzo.k9board.shared.extensions.popupDragHandler
import com.armandodarienzo.k9board.ui.keyboard.PopupBox
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

@Preview
@Composable
fun KeyboardKeyPreview(){
    KeyboardKey(text = "abc")
}

@Preview
@Composable
fun KeyboardIconKeyPreview(){
    KeyboardKey(text = "check", iconID = com.google.android.material.R.drawable.mtrl_ic_check_mark)
}

@Composable
fun KeyboardKey(
    modifier: Modifier = Modifier,
    text: String,
    iconID: Int? = null,
    iconAngle: Float = 0f,
    capsStatus: KeyboardCapsStatus? = KeyboardCapsStatus.LOWER_CASE,
    textStyle: TextStyle = TextStyle(),
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
        text = text,
        iconID = iconID,
        iconAngle = iconAngle,
        capsStatus = capsStatus,
        textStyle = textStyle,
        color = color,
        symbolsColor = symbolsColor)
}

@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyboardTextKey(
    modifier: Modifier = Modifier,
    id: Int,
    text: String,
    capsStatus: KeyboardCapsStatus? = KeyboardCapsStatus.LOWER_CASE,
    textStyle: TextStyle = TextStyle(),
    color: Color =
        if(!isSystemInDarkTheme()) Color.White
        else MaterialTheme.colorScheme.inverseOnSurface,
    symbolsColor: Color = MaterialTheme.colorScheme.onSurface,
    service: Key9Service?,
    numberASCIIcode: Int? = null,
    keyboardHeight: Int,
    keyPopupProperties: KeyPopupProperties? = null,
){

    val charList = remember { mutableStateOf(mutableListOf<String>()) }
    var startId = 0

    keyPopupProperties?.let {
        charList.value =
            text.toCharArray().map{ char -> char.toString() }.toMutableList().also { list ->
                list.addAll(it.chars)
            }.map { char ->
                if (capsStatus != KeyboardCapsStatus.LOWER_CASE) {
                    char.uppercase(Locale.ROOT)
                } else char
            }.toMutableStateList()

        val columns =
            min(
                charList.value.size, KEYBOARD_POPUP_MAX_COLUMNS
            )

        val rows = ceil((charList.value.size.toFloat() / columns)).toInt()

        //TODO: improve startId logic for TopStart alignment
        startId =
            when (keyPopupProperties.alignment) {
                Alignment.BottomStart -> columns - 1
                Alignment.BottomCenter -> ceil(columns / 2f).toInt() - 1
                Alignment.BottomEnd -> 0
                Alignment.CenterStart ->
                    columns * (ceil(rows / 2f).toInt() - 1) + columns -1
                Alignment.Center ->
                    columns * (ceil(rows / 2f).toInt() - 1) + ceil(columns / 2f).toInt() - 1
                Alignment.CenterEnd -> columns * (ceil(rows / 2f).toInt() - 1)
                Alignment.TopStart -> charList.value.size - 1
                Alignment.TopCenter ->
                    min(
                        columns * (rows - 1) + ceil(columns / 2f).toInt() - 1,
                        charList.value.size - 1
                    )
                Alignment.TopEnd -> columns * (rows - 1)
                else -> 0
            }

    }

    val visibleBox = remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    var selectedId by rememberSaveable { mutableStateOf(startId) }
    val boxOffset = remember { mutableStateOf(IntOffset.Zero) }
    var keySize by remember { mutableStateOf(IntSize.Zero) }

    val configuration = LocalConfiguration.current
    val popupWidth = (configuration.screenWidthDp * 0.6).dp
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
                            - popupWidthPx.roundToInt() + (keySize.width / 2f).roundToInt()
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
                        Alignment.TopEnd, Alignment.Top ->
                            - popupHeightPx.roundToInt() + (keySize.height / 2f).roundToInt()
                        Alignment.BottomStart, Alignment.BottomCenter,
                        Alignment.BottomEnd, Alignment.Bottom ->
                            (keySize.height / 2f).roundToInt()
                        Alignment.End, Alignment.Start, Alignment.Center,
                            Alignment.CenterStart, Alignment.CenterEnd->
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
                        if (service?.isManual?.value == true) {
                            service.addCharToCurrentText(
                                codifyChars(
                                    if (capsStatus == KeyboardCapsStatus.LOWER_CASE) text
                                    else text.uppercase(Locale.ROOT)
                                )
                                .also {
                                    numberASCIIcode?.let { numberASCIIcode ->
                                        it.add(numberASCIIcode)
                                    }
                                }
                                .toIntArray(),
                                id
                            )
                        } else {
                            service?.keyClick(
                                codifyChars(
                                    if (capsStatus == KeyboardCapsStatus.LOWER_CASE) text
                                    else text.uppercase(Locale.ROOT)
                                )
                                .also {
                                    numberASCIIcode?.let { numberASCIIcode ->
                                        it.add(numberASCIIcode)
                                    }
                                }
                                .toIntArray()
                            )
                        }


                    },
                    onLongClick = {
                        visibleBox.value = true
                    }
                )
                .applyIf(
                    keyPopupProperties != null, {
                        popupDragHandler(
                            lazyGridState = gridState,
                            boxOffset = boxOffset,
                            startId = startId,
                            selectId = { keyPopupProperties!!.onIdSelected(charList.value[selectedId]) },
                            setSelectedId = { selectedId = it },
                            closePopup = { visibleBox.value = false }
                        )
                    }
                ),
            text = text,
            capsStatus = capsStatus,
            textStyle = textStyle,
            color = color,
            symbolsColor = symbolsColor)

        keyPopupProperties?.let {

            PopupBox(
                characters = charList.value,
                popupWidth = popupWidth,
                popupHeight = popupHeight,
                showPopup = visibleBox.value,
                onClickOutside = { visibleBox.value = false },
                color = color,
                boxOffset = boxOffset,
                gridState = gridState,
                selectedId = selectedId,
            )
        }




    }





}

