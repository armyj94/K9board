package com.armandodarienzo.k9board.shared.model


import androidx.compose.ui.Alignment


class KeyPopupProperties(
    val chars: List<String> = emptyList(),
    val alignment: Alignment = Alignment.Center,
    val onIdSelected: (String) -> Unit = {}
) {
}