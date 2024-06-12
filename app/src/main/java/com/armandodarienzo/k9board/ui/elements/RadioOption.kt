package com.armandodarienzo.k9board.ui.elements

import com.armandodarienzo.k9board.shared.model.PreferencesOption

class RadioOption<T>(
    var value: PreferencesOption<T>,
    var selected: Boolean
) {
    val labelId: Int = value.getLabelId()
}