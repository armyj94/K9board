package com.armandodarienzo.k9board.settings_app.ui.screens.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armandodarienzo.k9board.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.model.KeyboardSize
import com.armandodarienzo.k9board.settings_app.ui.base.MviProcessor
import com.armandodarienzo.k9board.settings_app.ui.base.MviStoreDelegate
import com.armandodarienzo.k9board.settings_app.ui.base.StandardEffectDelegate
import com.armandodarienzo.k9board.usecase.base.UseCaseResult
import com.armandodarienzo.k9board.usecase.settings.GetDoubleSpaceCharUseCase
import com.armandodarienzo.k9board.usecase.settings.GetKeyboardSizeUseCase
import com.armandodarienzo.k9board.usecase.settings.IsAutoCapsEnabledUseCase
import com.armandodarienzo.k9board.usecase.settings.IsStartWithManualEnabledUseCase
import com.armandodarienzo.k9board.usecase.settings.SetAutoCapsUseCase
import com.armandodarienzo.k9board.usecase.settings.SetDoubleSpaceCharUseCase
import com.armandodarienzo.k9board.usecase.settings.SetKeyboardSizeUseCase
import com.armandodarienzo.k9board.usecase.settings.SetStartWithManualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val getKeyboardSize: GetKeyboardSizeUseCase,
    private val setKeyboardSize: SetKeyboardSizeUseCase,
    private val getDoubleSpaceChar: GetDoubleSpaceCharUseCase,
    private val setDoubleSpaceChar: SetDoubleSpaceCharUseCase,
    private val isStartWithManualEnabled: IsStartWithManualEnabledUseCase,
    private val setStartWithManual: SetStartWithManualUseCase,
    private val isAutoCapsEnabled: IsAutoCapsEnabledUseCase,
    private val setAutoCaps: SetAutoCapsUseCase,
    private val effectDelegate: StandardEffectDelegate<PreferencesReducer.Effect>
) : ViewModel(),
    MviProcessor<PreferencesReducer.PreferencesState, PreferencesViewModel.Action, PreferencesReducer.Effect>,
    com.armandodarienzo.k9board.settings_app.ui.base.EffectDelegate<PreferencesReducer.Effect> by effectDelegate {

    private val store = MviStoreDelegate(
        initialState = PreferencesReducer.PreferencesState.initial(),
        scope = viewModelScope,
        reducer = PreferencesReducer(),
        effectDelegate = effectDelegate,
        initialDataLoad = ::loadPreferences
    )

    override val state = store.state

    sealed class Action : MviProcessor.MviAction {
        data class SetKeyboardSize(val size: KeyboardSize) : Action()
        data class SetDoubleSpaceChar(val char: DoubleSpaceCharacter) : Action()
        data class SetStartWithManual(val enabled: Boolean) : Action()
        data class SetAutoCaps(val enabled: Boolean) : Action()
    }

    override fun processAction(action: Action) {
        when (action) {
            is Action.SetKeyboardSize -> onSetKeyboardSize(action.size)
            is Action.SetDoubleSpaceChar -> onSetDoubleSpaceChar(action.char)
            is Action.SetStartWithManual -> onSetStartWithManual(action.enabled)
            is Action.SetAutoCaps -> onSetAutoCaps(action.enabled)
        }
    }

    private suspend fun loadPreferences() {
        val size = (getKeyboardSize(Unit) as? UseCaseResult.Success)?.successData ?: KeyboardSize.MEDIUM
        val char = (getDoubleSpaceChar(Unit) as? UseCaseResult.Success)?.successData ?: DoubleSpaceCharacter.NONE
        val manual = (isStartWithManualEnabled(Unit) as? UseCaseResult.Success)?.successData ?: false
        val autoCapsEnabled = (isAutoCapsEnabled(Unit) as? UseCaseResult.Success)?.successData ?: true
        store.sendEvent(
            PreferencesReducer.Event.PreferencesLoaded(size, char, manual, autoCapsEnabled)
        )
    }

    private fun onSetKeyboardSize(size: KeyboardSize) {
        store.sendEvent(PreferencesReducer.Event.KeyboardSizeUpdated(size))
        viewModelScope.launch { setKeyboardSize(size) }
    }

    private fun onSetDoubleSpaceChar(char: DoubleSpaceCharacter) {
        store.sendEvent(PreferencesReducer.Event.DoubleSpaceCharUpdated(char))
        viewModelScope.launch { setDoubleSpaceChar(char) }
    }

    private fun onSetStartWithManual(enabled: Boolean) {
        store.sendEvent(PreferencesReducer.Event.StartWithManualUpdated(enabled))
        viewModelScope.launch { setStartWithManual(enabled) }
    }

    private fun onSetAutoCaps(enabled: Boolean) {
        store.sendEvent(PreferencesReducer.Event.AutoCapsUpdated(enabled))
        viewModelScope.launch { setAutoCaps(enabled) }
    }
}