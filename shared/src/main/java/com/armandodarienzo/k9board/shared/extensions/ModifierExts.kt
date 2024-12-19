package com.armandodarienzo.k9board.shared.extensions

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.minus
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect

inline fun Modifier.applyIf(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}

fun Modifier.popupDragHandler(
    lazyGridState: LazyGridState,
    boxOffset: MutableState<IntOffset>,
    startId: Int = 0,
    setSelectedId: (Int) -> Unit = { },
    selectId: () -> Unit = { },
    closePopup: () -> Unit = { }
) : Modifier = pointerInput(setSelectedId) {
    fun charIndexAtOffset(hitPoint: Offset): Int? =
        lazyGridState.layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.index

    var charIndex: Int? = startId

    detectDragGesturesAfterLongPress(
        onDragStart = { _ ->  },
        onDragCancel = {
            setSelectedId(startId)
            charIndex = startId
            closePopup()
        },
        onDragEnd = {
            charIndex?.let {
                selectId()
            }
            setSelectedId(startId)
            charIndex = startId
            closePopup()
        },
        onDrag = { change, _ ->
            charIndex = charIndexAtOffset(change.position.minus(boxOffset.value))

            charIndex?.let { pointerCharIndex ->
                setSelectedId(pointerCharIndex)
            }

        }
    )

} then Modifier.graphicsLayer { this.alpha = alpha }