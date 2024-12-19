package com.armandodarienzo.k9board.shared.model

class RadioOption<T>(
    var value: PreferencesOption<T>,
    var selected: Boolean
) {
    val labelId: Int = value.getLabelId()
}