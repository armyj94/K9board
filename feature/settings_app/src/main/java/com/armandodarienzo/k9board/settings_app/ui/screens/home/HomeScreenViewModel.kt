package com.armandodarienzo.k9board.settings_app.ui.screens.home

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armandodarienzo.k9board.shared.ui.base.EffectDelegate
import com.armandodarienzo.k9board.shared.ui.base.MviProcessor
import com.armandodarienzo.k9board.shared.ui.base.MviStoreDelegate
import com.armandodarienzo.k9board.shared.ui.base.StandardEffectDelegate
import com.armandodarienzo.k9board.shared.ui.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val effectDelegate: StandardEffectDelegate<HomeScreenReducer.Effect>
) : ViewModel(),
    MviProcessor<HomeScreenReducer.HomeScreenState, HomeScreenViewModel.Action, HomeScreenReducer.Effect>,
    EffectDelegate<HomeScreenReducer.Effect> by effectDelegate {

    private val store = MviStoreDelegate(
        initialState = HomeScreenReducer.HomeScreenState,
        scope = viewModelScope,
        reducer = HomeScreenReducer(),
        effectDelegate = effectDelegate,
    )

    override val state: StateFlow<HomeScreenReducer.HomeScreenState> = store.state

    sealed class Action : MviProcessor.MviAction {
        data object EnableKeyboard : Action()
        data object ChangeKeyboard : Action()
        data object NavigateToLanguage : Action()
        data object NavigateToPreferences : Action()
        data object NavigateToTestKeyboard : Action()
    }

    override fun processAction(action: Action) {
        when (action) {
            Action.EnableKeyboard -> onEnableKeyboard()
            Action.ChangeKeyboard -> onChangeKeyboard()
            Action.NavigateToLanguage ->
                effectDelegate.sendEffect(HomeScreenReducer.Effect.NavigateTo(Screens.LanguageSelectionScreen.name))
            Action.NavigateToPreferences ->
                effectDelegate.sendEffect(HomeScreenReducer.Effect.NavigateTo(Screens.PreferencesScreen.name))
            Action.NavigateToTestKeyboard ->
                effectDelegate.sendEffect(HomeScreenReducer.Effect.NavigateTo(Screens.KeyboardTestScreen.name))
        }
    }

    private fun onEnableKeyboard() {
        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        effectDelegate.sendEffect(HomeScreenReducer.Effect.LaunchActivity(intent))
    }

    private fun onChangeKeyboard() {
        effectDelegate.sendEffect(HomeScreenReducer.Effect.ShowImePicker)
    }
}