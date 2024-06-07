package com.armandodarienzo.k9board.model

class MainMenuItem(
    val name: String,
    val optionKeyString: String?,
    val iconID: Int,
    val onClick: () -> Unit? = {}) {}