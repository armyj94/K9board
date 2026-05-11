package com.armandodarienzo.k9board.settings_app.ui.screens.language

import androidx.compose.runtime.Immutable
import com.armandodarienzo.k9board.shared.model.DatabaseStatus
import com.armandodarienzo.k9board.settings_app.ui.base.Reducer

class LanguageSelectionReducer :
    Reducer<LanguageSelectionReducer.LanguageSelectionState, LanguageSelectionReducer.Event, LanguageSelectionReducer.Effect> {

    @Immutable
    data class LanguageSelectionState(
        val selectedLanguage: String,
        val downloadStatus: Map<String, DatabaseStatus>,
    ) : Reducer.ViewState {
        companion object {
            fun initial() = LanguageSelectionState(
                selectedLanguage = "en-US",
                downloadStatus = emptyMap(),
            )
        }
    }

    @Immutable
    sealed class Event : Reducer.ViewEvent {
        data class LanguageSelected(val tag: String) : Event()
        data class DownloadStatusUpdated(val tag: String, val status: DatabaseStatus) : Event()
        data class InitialDataLoaded(
            val selectedLanguage: String,
            val statuses: Map<String, DatabaseStatus>
        ) : Event()
    }

    sealed class Effect : Reducer.SideEffect

    override fun reduce(
        previousState: LanguageSelectionState,
        event: Event
    ): Pair<LanguageSelectionState, Effect?> = when (event) {
        is Event.LanguageSelected -> previousState.copy(selectedLanguage = event.tag) to null

        is Event.DownloadStatusUpdated -> previousState.copy(
            downloadStatus = previousState.downloadStatus + (event.tag to event.status)
        ) to null

        is Event.InitialDataLoaded -> previousState.copy(
            selectedLanguage = event.selectedLanguage,
            downloadStatus = event.statuses,
        ) to null
    }
}