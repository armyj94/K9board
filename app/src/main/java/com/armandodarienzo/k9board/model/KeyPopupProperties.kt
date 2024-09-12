package com.armandodarienzo.k9board.model


import android.graphics.Color
import androidx.compose.ui.Alignment


class KeyPopupProperties(
    val chars: List<String> = emptyList(),
    val alignment: Alignment = Alignment.Center,
    val onIdSelected: (String) -> Unit = {}
) {
}