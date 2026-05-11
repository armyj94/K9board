package com.armandodarienzo.k9board.settings_app.ui.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MviProcessor<State : Reducer.ViewState, Action : MviProcessor.MviAction, Effect : Reducer.SideEffect> {

    interface MviAction

    val state: StateFlow<State>
    val effect: Flow<Effect>

    fun processAction(action: Action)
}