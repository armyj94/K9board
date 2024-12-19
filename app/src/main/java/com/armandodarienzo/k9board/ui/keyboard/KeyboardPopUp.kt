package com.armandodarienzo.k9board.ui.keyboard

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.minus
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.shared.Key2SpecialChars
import com.armandodarienzo.k9board.shared.KEYBOARD_POPUP_MAX_COLUMNS
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun PopupBoxPreview() {

    PopupBox(
        showPopup = true,
        popupWidth = 200.dp,
        popupHeight = 200.dp,
        onClickOutside = { },
        color = MaterialTheme.colorScheme.background,
        characters = Key2SpecialChars.VALUES
    )

}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun PopupBox(
    characters: List<String>,
    showPopup: Boolean,
    popupWidth: Dp,
    popupHeight: Dp,
    onClickOutside: () -> Unit,
    color: Color,
    boxOffset: MutableState<IntOffset> = mutableStateOf(IntOffset.Zero),
    gridState: LazyGridState = rememberLazyGridState(),
    selectedId: Int = 0,
) {

    val columns =
        min(
            characters.size, KEYBOARD_POPUP_MAX_COLUMNS
        )

    val rows = ceil((characters.size.toFloat() / columns)).toInt()
    val popupKeyRatio = (popupWidth * rows) / (popupHeight * columns)

    if (showPopup) {
        // popup
        Box(
        ) {
            Popup(
                //alignment = Alignment.Center,
                properties = PopupProperties(
                    excludeFromSystemGesture = true,
                    clippingEnabled = false
                ),
                offset = boxOffset.value,
                // to dismiss on click outside
                onDismissRequest = { onClickOutside() }
            ) {
                Box(
                    Modifier
                        .width(popupWidth)
                        .height(popupHeight)
                        .clip(RoundedCornerShape(20))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = gridState,
                        verticalArrangement = Arrangement.Center,
                        horizontalArrangement = Arrangement.Center,
                        columns = GridCells.Fixed(columns),
                        userScrollEnabled = false
                    ) {

                        items(
                            characters.size
                        ) {
                            PopUpKey(
                                text = characters[it],
                                selected = it == selectedId,
                                ratio = popupKeyRatio
                            )
                        }
                    }

                }
            }
        }


    }
}

@Composable
private fun Scrim(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(onClose) {
                detectTapGestures { onClose() }
            }
            .background(Color.DarkGray.copy(alpha = 0.75f))

    )
}

@Composable
fun PopUpKey(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean = false,
    capsStatus: KeyboardCapsStatus? = KeyboardCapsStatus.LOWER_CASE,
    textStyle: TextStyle = TextStyle(),
    color: Color =
        if(!isSystemInDarkTheme()) Color.White
        else MaterialTheme.colorScheme.inverseOnSurface,
    symbolsColor: Color = MaterialTheme.colorScheme.onSurface,
    ratio: Float = 1f
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(ratio)
            .clip(RoundedCornerShape(10))
            .background(
                if (selected) {
                    if (!isSystemInDarkTheme())
                        MaterialTheme.colorScheme.inversePrimary
                    else
                        MaterialTheme.colorScheme.primary
                } else
                    color
            )
            .then(modifier)
    ) {
        Text(
            text = if (capsStatus == KeyboardCapsStatus.LOWER_CASE) text else text.uppercase(
                Locale.ROOT
            ), //TODO: replace with ToUpperCaseByLanguageTag()
            style = textStyle,
            fontSize = 14.sp,
            color = symbolsColor
        )

    }
}


