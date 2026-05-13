package com.armandodarienzo.k9board.shared.ui.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MviStoreDelegate<State : Reducer.ViewState, Event : Reducer.ViewEvent, Effect : Reducer.SideEffect>(
    private val initialState: State,
    private val scope: CoroutineScope,
    private val reducer: Reducer<State, Event, Effect>,
    private val effectDelegate: EffectDelegate<Effect>,
    private val initialDataLoad: (suspend () -> Unit)? = null
) : MviStore<State, Event> {

    private val _state = MutableStateFlow(initialState)

    override val state: StateFlow<State> by lazy {
        _state.onStart {
            if (initialDataLoad != null) {
                scope.launch { initialDataLoad.invoke() }
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = initialState
        )
    }

    override fun sendEvent(event: Event) {
        val (newState, sideEffect) = reducer.reduce(_state.value, event)
        _state.tryEmit(newState)
        if (sideEffect != null) {
            effectDelegate.sendEffect(sideEffect)
        }
    }
}