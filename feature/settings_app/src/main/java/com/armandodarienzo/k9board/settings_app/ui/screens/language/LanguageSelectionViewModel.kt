package com.armandodarienzo.k9board.settings_app.ui.screens.language

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.armandodarienzo.k9board.model.SupportedLanguageTag
import com.armandodarienzo.k9board.settings_app.ui.base.EffectDelegate
import com.armandodarienzo.k9board.settings_app.ui.base.MviProcessor
import com.armandodarienzo.k9board.settings_app.ui.base.MviStoreDelegate
import com.armandodarienzo.k9board.settings_app.ui.base.StandardEffectDelegate
import com.armandodarienzo.k9board.settings_app.usecase.CancelDownloadUseCase
import com.armandodarienzo.k9board.settings_app.usecase.DownloadLanguagePackUseCase
import com.armandodarienzo.k9board.shared.getDatabaseName
import com.armandodarienzo.k9board.shared.model.CoroutineDownloadWorker
import com.armandodarienzo.k9board.shared.model.DatabaseStatus
import com.armandodarienzo.k9board.usecase.base.UseCaseResult
import com.armandodarienzo.k9board.usecase.settings.GetLanguageUseCase
import com.armandodarienzo.k9board.usecase.settings.SetLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LanguageSelectionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getLanguage: GetLanguageUseCase,
    private val setLanguage: SetLanguageUseCase,
    private val downloadLanguagePack: DownloadLanguagePackUseCase,
    private val cancelDownload: CancelDownloadUseCase,
    private val effectDelegate: StandardEffectDelegate<LanguageSelectionReducer.Effect>
) : ViewModel(),
    MviProcessor<LanguageSelectionReducer.LanguageSelectionState, LanguageSelectionViewModel.Action, LanguageSelectionReducer.Effect>,
    EffectDelegate<LanguageSelectionReducer.Effect> by effectDelegate {

    private val TAG = "LanguageSelectionViewModel"

    private val store = MviStoreDelegate(
        initialState = LanguageSelectionReducer.LanguageSelectionState.initial(),
        scope = viewModelScope,
        reducer = LanguageSelectionReducer(),
        effectDelegate = effectDelegate,
        initialDataLoad = ::loadInitialData
    )

    override val state = store.state

    sealed class Action : MviProcessor.MviAction {
        data class SelectLanguage(val tag: String) : Action()
        data class Download(val tag: String) : Action()
        data class CancelDownload(val tag: String) : Action()
        data class RemoveLanguagePack(val tag: String) : Action()
        data object NavigateBack : Action()
    }

    override fun processAction(action: Action) {
        when (action) {
            is Action.SelectLanguage -> onSelectLanguage(action.tag)
            is Action.Download -> onDownload(action.tag)
            is Action.CancelDownload -> onCancelDownload(action.tag)
            is Action.RemoveLanguagePack -> onCancelDownload(action.tag)
            Action.NavigateBack -> effectDelegate.sendEffect(LanguageSelectionReducer.Effect.NavigateBack)
        }
    }

    private suspend fun loadInitialData() {
        val selectedLanguage = (getLanguage(Unit) as? UseCaseResult.Success)?.successData
            ?: SupportedLanguageTag.AMERICAN.value

        val statuses = SupportedLanguageTag.entries.associate { entry ->
            val dbName = getDatabaseName(entry.value)
            val path = context.getDatabasePath(dbName).path
            entry.value to DatabaseStatus(
                tag = entry.value,
                state = if (File(path).exists())
                    DatabaseStatus.Companion.Statuses.DOWNLOADED
                else
                    DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED
            )
        }

        store.sendEvent(LanguageSelectionReducer.Event.InitialDataLoaded(selectedLanguage, statuses))

        observeWorkManagerStatuses()
    }

    private fun observeWorkManagerStatuses() {
        val workManager = WorkManager.getInstance(context)
        SupportedLanguageTag.entries.forEach { entry ->
            val dbName = getDatabaseName(entry.value)
            val path = context.getDatabasePath(dbName).path

            workManager.getWorkInfosForUniqueWorkLiveData(entry.value)
                .asFlow()
                .onEach { workInfoList ->
                    val workInfo = workInfoList.firstOrNull() ?: return@onEach
                    Log.d(TAG, "state for ${entry.value} is ${workInfo.state}")

                    val newStatus = when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> DatabaseStatus(
                            entry.value,
                            if (File(path).exists())
                                DatabaseStatus.Companion.Statuses.DOWNLOADED
                            else
                                DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED
                        )
                        WorkInfo.State.FAILED, WorkInfo.State.BLOCKED ->
                            DatabaseStatus(entry.value, DatabaseStatus.Companion.Statuses.ERROR)

                        WorkInfo.State.ENQUEUED ->
                            DatabaseStatus(entry.value, DatabaseStatus.Companion.Statuses.DOWNLOADING)

                        WorkInfo.State.RUNNING -> DatabaseStatus(
                            entry.value,
                            DatabaseStatus.Companion.Statuses.DOWNLOADING,
                            workInfo.progress.getFloat(CoroutineDownloadWorker.PROGRESS, 0F)
                        )

                        WorkInfo.State.CANCELLED ->
                            DatabaseStatus(entry.value, DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED)
                    }

                    store.sendEvent(
                        LanguageSelectionReducer.Event.DownloadStatusUpdated(entry.value, newStatus)
                    )
                }
                .launchIn(viewModelScope)
        }
    }

    private fun onSelectLanguage(tag: String) {
        store.sendEvent(LanguageSelectionReducer.Event.LanguageSelected(tag))
        viewModelScope.launch { setLanguage(tag) }
    }

    private fun onDownload(tag: String) {
        downloadLanguagePack(tag)
    }

    private fun onCancelDownload(tag: String) {
        if (state.value.selectedLanguage == tag) {
            onSelectLanguage(SupportedLanguageTag.AMERICAN.value)
        }
        cancelDownload(tag)
        store.sendEvent(
            LanguageSelectionReducer.Event.DownloadStatusUpdated(
                tag,
                DatabaseStatus(tag, DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED)
            )
        )
    }
}