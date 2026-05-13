package com.armandodarienzo.k9board.settings_app.ui.screens.home

import android.content.Intent
import androidx.compose.runtime.Immutable
import com.armandodarienzo.k9board.settings_app.ui.base.Reducer

class HomeScreenReducer :
    Reducer<HomeScreenReducer.HomeScreenState, HomeScreenReducer.Event, HomeScreenReducer.Effect> {

    @Immutable
    object HomeScreenState : Reducer.ViewState

    sealed class Event : Reducer.ViewEvent

    @Immutable
    sealed class Effect : Reducer.SideEffect {
        data class LaunchActivity(val intent: Intent) : Effect()
        data object ShowImePicker : Effect()
        data class NavigateTo(val route: String) : Effect()
    }

    override fun reduce(
        previousState: HomeScreenState,
        event: Event
    ): Pair<HomeScreenState, Effect?> = previousState to null
}