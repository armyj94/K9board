package com.armandodarienzo.k9board.ui.keyboard

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.shared.KEY2_SPECIAL_CHARS
import com.armandodarienzo.k9board.shared.KEYBOARD_POPUP_MAX_COLUMNS
import java.util.Locale
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview
fun PopupBoxPreview() {

    PopupBox(
        showPopup = true,
        onClickOutside = { },
        color = MaterialTheme.colorScheme.background,
        characters = KEY2_SPECIAL_CHARS.VALUES
    )

}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun PopupBox(
    characters: List<String>,
    showPopup: Boolean,
    onClickOutside: () -> Unit,
    color: Color,
    capsStatus: KeyboardCapsStatus? = KeyboardCapsStatus.LOWER_CASE,
) {

    val configuration = LocalConfiguration.current
    val popupWidth = (configuration.screenWidthDp / 2).dp

    var selectedId by rememberSaveable { mutableStateOf(0) }
    val gridState = rememberLazyGridState()


    val columns =
        min(
            characters.size, KEYBOARD_POPUP_MAX_COLUMNS
        )

    if (showPopup) {
        Scrim(onClose = { onClickOutside() })
        // popup
        Box(

        ) {
            Popup(
                //alignment = Alignment.Center,
                properties = PopupProperties(
                    excludeFromSystemGesture = true,
                    clippingEnabled = false
                ),
                offset = IntOffset(0, 0),
                // to dismiss on click outside
                onDismissRequest = { onClickOutside() }
            ) {
                Box(
                    Modifier
                        .width(popupWidth)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(20))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {


                    LazyVerticalGrid(
                        state = gridState,
                        modifier = Modifier.popupDragHandler(
                            lazyGridState = gridState,
                            setSelectedId = { selectedId = it },
                            autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() },
                            closePopup = onClickOutside
                        ),
                        columns = GridCells.Fixed(columns),
                        userScrollEnabled = false
                    ) {
                        items(characters.size) {
                            PopUpKey(
                                text = characters[it],
                                selected = it == selectedId,
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
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10))
            .background(
                if (selected) {
                    if(!isSystemInDarkTheme())
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


fun Modifier.popupDragHandler(
    lazyGridState: LazyGridState,
    autoScrollThreshold: Float,
    setSelectedId: (Int) -> Unit = { },
    closePopup: () -> Unit = { },
    setAutoScrollSpeed: (Float) -> Unit = { },
) : Modifier = pointerInput(autoScrollThreshold, setSelectedId, setAutoScrollSpeed) {
    fun charIndexAtOffset(hitPoint: Offset): Int? =
        lazyGridState.layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.index

    detectDragGestures(
        onDragStart = { offset ->
//            Log.d("test", "onDragStart: $offset")
//            charIndexAtOffset(offset)?.let { key ->
//                Log.d("test", "onDrag key: $key")
//                setSelectedId(key)
//            }
        },
        onDragCancel = {
            setAutoScrollSpeed(0f)
            setSelectedId(0)
            Log.d("test", "onDragCancel")
            closePopup()},
        onDragEnd = {
            setAutoScrollSpeed(0f)
            setSelectedId(0)
            Log.d("test", "onDragEnd")
            //TODO: write char to InputConnection
            closePopup()},
        onDrag = { change, _ ->

                charIndexAtOffset(change.position)?.let { pointerCharIndex ->
                    Log.d("test", "onDrag pointerCharIndex: $pointerCharIndex")
                    setSelectedId(pointerCharIndex)
                }
        }
    )

} then Modifier.graphicsLayer { this.alpha = alpha }