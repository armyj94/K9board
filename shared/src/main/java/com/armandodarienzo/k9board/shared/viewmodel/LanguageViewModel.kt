package com.armandodarienzo.k9board.shared.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.armandodarienzo.k9board.shared.DATABASE_NAME
import com.armandodarienzo.k9board.shared.WEBSITE_URL
import com.armandodarienzo.k9board.shared.getDatabaseName
import com.armandodarienzo.k9board.shared.model.CoroutineDownloadWorker
import com.armandodarienzo.k9board.shared.model.DatabaseStatus
import com.armandodarienzo.k9board.shared.model.SupportedLanguageTag
import com.armandodarienzo.k9board.shared.packName
import com.armandodarienzo.k9board.shared.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.annotation.meta.When
import javax.inject.Inject


@HiltViewModel
class LanguageViewModel@Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel()  {

    private val TAG = "LanguageViewModel"

    private val _languageState = mutableStateOf(SupportedLanguageTag.AMERICAN.value)
    val languageState : State<String> = _languageState

    private val _databaseStatuses: MutableMap<String, MutableStateFlow<DatabaseStatus>> = mutableMapOf()
    val databaseStatus: Map<String, StateFlow<DatabaseStatus>> = _databaseStatuses




    init {
        val workManager = WorkManager.getInstance(mContext)

        viewModelScope.launch {

            _languageState.value = userPreferencesRepository.getLanguage().getOrNull()!!


            SupportedLanguageTag.entries.forEach { entry ->

                val dbName = getDatabaseName(entry.value)
                val path = mContext.getDatabasePath(dbName).path

                _databaseStatuses[entry.value] = MutableStateFlow(
                    DatabaseStatus(
                        tag = entry.value,
                        state = if (File(path).exists()) {
                            Log.d(TAG, "initial state for ${entry.value} is DOWNLOADED")
                            DatabaseStatus.Companion.Statuses.DOWNLOADED
                        } else {
                            Log.d(TAG, "initial state for ${entry.value} is NOT_DOWNLOADED")
                            DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED
                        }
                    )
                )

                workManager.getWorkInfosForUniqueWorkLiveData(entry.value)
                    .asFlow()
                    .onEach { workInfoList ->
                        val workInfo = workInfoList.firstOrNull()
                        workInfo?.let {
                            Log.d(TAG, "state for ${entry.value} is ${it.state}")

                            val newState: DatabaseStatus

                            when (it.state) {
                                WorkInfo.State.SUCCEEDED ->
                                    /* The code above is because once the state becomes SUCCEEDED
                                     * it is not possible to change its state to another until
                                     * another work with the same name start (if the policy is
                                     * REPLACE as in our case). This resulting in a wrong
                                     * DatabaseStatus after deleting the database*/
                                    newState = if (File(path).exists()) {
                                        DatabaseStatus(entry.value, DatabaseStatus.Companion.Statuses.DOWNLOADED)
                                    } else {
                                        DatabaseStatus(entry.value, DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED)
                                    }
                                WorkInfo.State.FAILED ->
                                    newState = DatabaseStatus(entry.value, DatabaseStatus.Companion.Statuses.ERROR)
                                WorkInfo.State.ENQUEUED ->
                                    newState = DatabaseStatus(entry.value, DatabaseStatus.Companion.Statuses.DOWNLOADING)
                                WorkInfo.State.RUNNING -> {
                                    newState = DatabaseStatus(entry.value, DatabaseStatus.Companion.Statuses.DOWNLOADING, it.progress.getFloat(CoroutineDownloadWorker.Progress, 0F))
                                }
                                WorkInfo.State.BLOCKED ->
                                    newState = DatabaseStatus(entry.value, DatabaseStatus.Companion.Statuses.ERROR)
                                WorkInfo.State.CANCELLED -> {
                                    newState = DatabaseStatus(entry.value, DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED)
                                }
                            }

                            _databaseStatuses[entry.value]?.value = newState
                        }
                    }
                    .launchIn(viewModelScope)
            }

        }
    }

    fun setLanguage(tag: String) {
        _languageState.value = tag
        viewModelScope.launch {
            userPreferencesRepository.setLanguage(tag)
        }
    }

    fun downloadLanguagePack(tag: String) {
        val data = Data.Builder()
        data.putString("languageTag", tag)
        val downloadWorkRequest = OneTimeWorkRequestBuilder<CoroutineDownloadWorker>().apply {
            setInputData(data.build())
            addTag(tag)
        }.build()

        WorkManager.getInstance(mContext).beginUniqueWork(tag, ExistingWorkPolicy.REPLACE, downloadWorkRequest).enqueue()
    }

    fun cancelDownload(tag: String) {
        Log.d(TAG, "cancelDownload of $tag")
        WorkManager.getInstance(mContext).cancelUniqueWork(tag)
        _databaseStatuses[tag]?.value = DatabaseStatus(tag, DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED)
        mContext.deleteDatabase(getDatabaseName(tag))
    }

}