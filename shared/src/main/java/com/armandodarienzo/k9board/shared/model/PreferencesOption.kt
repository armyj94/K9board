package com.armandodarienzo.k9board.shared.model

sealed interface PreferencesOption<T> {

    val value: T

    fun getLabelId(): Int

}