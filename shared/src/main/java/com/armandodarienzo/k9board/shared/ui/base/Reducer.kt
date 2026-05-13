package com.armandodarienzo.k9board.shared.ui.base

fun interface Reducer<State : Reducer.ViewState, Event : Reducer.ViewEvent, Effect : Reducer.SideEffect> {

    interface ViewState
    interface ViewEvent
    interface SideEffect

    fun reduce(previousState: State, event: Event): Pair<State, Effect?>
}