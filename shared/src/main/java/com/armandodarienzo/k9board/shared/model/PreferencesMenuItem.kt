package com.armandodarienzo.k9board.shared.model

class PreferencesMenuItem(
    val name: String,
    var value: String,
    val onClick: () -> Unit
)