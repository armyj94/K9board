package com.armandodarienzo.k9board.shared.ui.base

import kotlinx.coroutines.flow.StateFlow

interface MviStore<State : Reducer.ViewState, Event : Reducer.ViewEvent> {
    val state: StateFlow<State>
    fun sendEvent(event: Event)
}