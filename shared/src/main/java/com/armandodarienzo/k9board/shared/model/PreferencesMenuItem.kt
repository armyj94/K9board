package com.armandodarienzo.k9board.shared.model

class PreferencesMenuItem<T>(
    val name: String,
    var value: T,
    val onClick: () -> Unit
)