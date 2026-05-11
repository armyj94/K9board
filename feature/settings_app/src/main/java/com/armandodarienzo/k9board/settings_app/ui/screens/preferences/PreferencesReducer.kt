package com.armandodarienzo.k9board.settings_app.ui.screens.preferences

import androidx.compose.runtime.Immutable
import com.armandodarienzo.k9board.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.model.KeyboardSize
import com.armandodarienzo.k9board.settings_app.ui.base.Reducer

class PreferencesReducer :
    Reducer<PreferencesReducer.PreferencesState, PreferencesReducer.Event, PreferencesReducer.Effect> {

    @Immutable
    data class PreferencesState(
        val keyboardSize: KeyboardSize,
        val doubleSpaceChar: DoubleSpaceCharacter,
        val startWithManual: Boolean,
        val autoCaps: Boolean,
    ) : Reducer.ViewState {
        companion object {
            fun initial() = PreferencesState(
                keyboardSize = KeyboardSize.MEDIUM,
                doubleSpaceChar = DoubleSpaceCharacter.NONE,
                startWithManual = false,
                autoCaps = true,
            )
        }
    }

    @Immutable
    sealed class Event : Reducer.ViewEvent {
        data class PreferencesLoaded(
            val keyboardSize: KeyboardSize,
            val doubleSpaceChar: DoubleSpaceCharacter,
            val startWithManual: Boolean,
            val autoCaps: Boolean,
        ) : Event()

        data class KeyboardSizeUpdated(val size: KeyboardSize) : Event()
        data class DoubleSpaceCharUpdated(val char: DoubleSpaceCharacter) : Event()
        data class StartWithManualUpdated(val enabled: Boolean) : Event()
        data class AutoCapsUpdated(val enabled: Boolean) : Event()
    }

    sealed class Effect : Reducer.SideEffect

    override fun reduce(
        previousState: PreferencesState,
        event: Event
    ): Pair<PreferencesState, Effect?> = when (event) {
        is Event.PreferencesLoaded -> previousState.copy(
            keyboardSize = event.keyboardSize,
            doubleSpaceChar = event.doubleSpaceChar,
            startWithManual = event.startWithManual,
            autoCaps = event.autoCaps,
        ) to null

        is Event.KeyboardSizeUpdated -> previousState.copy(keyboardSize = event.size) to null
        is Event.DoubleSpaceCharUpdated -> previousState.copy(doubleSpaceChar = event.char) to null
        is Event.StartWithManualUpdated -> previousState.copy(startWithManual = event.enabled) to null
        is Event.AutoCapsUpdated -> previousState.copy(autoCaps = event.enabled) to null
    }
}