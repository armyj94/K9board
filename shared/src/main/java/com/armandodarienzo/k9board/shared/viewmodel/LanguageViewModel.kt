package com.armandodarienzo.k9board.shared.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.armandodarienzo.k9board.shared.DATABASE_NAME
import com.armandodarienzo.k9board.shared.WEBSITE_URL
import com.armandodarienzo.k9board.shared.model.CoroutineDownloadWorker
import com.armandodarienzo.k9board.shared.model.DatabaseStatus
import com.armandodarienzo.k9board.shared.model.SupportedLanguageTag
import com.armandodarienzo.k9board.shared.packName
import com.armandodarienzo.k9board.shared.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

//    private val assetPackManager = AssetPackManagerFactory.getInstance(mContext)
//
//    private val languagePackNames = SupportedLanguageTag.entries
//        .toMutableList()
//        .map { it.value }
//        .map { tag ->
//           packName(tag)
//        }
//
    private val _languageState = mutableStateOf(SupportedLanguageTag.AMERICAN.value)
    val languageState : State<String> = _languageState

    private var workInfosLiveData : MutableMap<String, LiveData<List<WorkInfo>>> = mutableMapOf() // = WorkManager.getInstance(mContext).getWorkInfosForUniqueWorkLiveData(tag)

    private var workInfos : MutableMap<String, WorkInfo?> = mutableMapOf()
    private var workInfoObserver : MutableMap<String, Observer<List<WorkInfo>>> = mutableMapOf()

    private val _databaseStatuses: MutableMap<String, MutableState<DatabaseStatus>> = mutableMapOf()
    val databaseStatus: Map<String, State<DatabaseStatus>> = _databaseStatuses




    init {
        val workManager = WorkManager.getInstance(mContext)

        viewModelScope.launch {

            _languageState.value = userPreferencesRepository.getLanguage().getOrNull()!!


            SupportedLanguageTag.entries.forEach { entry ->

                val dbName = "${DATABASE_NAME}_${entry.value}.sqlite"
//                val link = "${WEBSITE_URL}dictionaries/$dbName"
                val path = mContext.getDatabasePath(dbName).path

                _databaseStatuses[entry.value] = mutableStateOf(
                    DatabaseStatus(
                        tag = entry.value,
                        state =
                            if (File(path).exists())
                                {DatabaseStatus.Companion.Statuses.DOWNLOADED}
                            else
                                {DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED}
                    )
                )

                workInfosLiveData[entry.value] = workManager.getWorkInfosForUniqueWorkLiveData(entry.value)
                workInfoObserver[entry.value] = createWorkInfoObserver(entry.value)
                workInfosLiveData[entry.value]?.observeForever(workInfoObserver[entry.value]!!)
            }


//
//            assetPackManager.requestPackStates(
//                languagePackNames.minus(packName(SupportedLanguageTag.AMERICAN.value))
//            ).runCatching {
//                _assetPackStatesMapState.value = this.packStates()
//                assetPackManager.registerListener { assetPackState ->
//                    val map = _assetPackStatesMapState.value.toMutableMap()
//                    map[assetPackState.name()] = assetPackState
//                    _assetPackStatesMapState.value = map
//                }
//
//            }

        }
    }

    override fun onCleared() {
        SupportedLanguageTag.entries.forEach { entry ->
            workInfosLiveData[entry.value]?.removeObserver { workInfoObserver[entry.value] }
        }
        super.onCleared()
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
        val packName = packName(tag)
//        assetPackManager.cancel(listOf(packName)).packStates.values.forEach {
//            Log.d(TAG, "cancelDownload assetPackState.status = ${it.status()}")
//            val map = _assetPackStatesMapState.value.toMutableMap()
//            map[it.name()] = it
//            _assetPackStatesMapState.value = map
//        }
//        assetPackManager.removePack(packName)
    }

    fun removePack(tag: String) {
        val packName = packName(tag)
//        if (languageState.value == tag) {
//            setLanguage(SupportedLanguageTag.AMERICAN.value)
//        }

        //TODO: remove copied database files

//        assetPackManager.removePack(packName)
    }

    fun getDownloadProgress(tag: String) : Float {
//        val map = _assetPackStatesMapState.value
//        val packName = packName(tag)
//
//        val downloaded = map[packName]?.bytesDownloaded() ?: 0
//        val totalSize = map[packName]?.totalBytesToDownload() ?: 0
//
//        try {
//            return (100L * downloaded / totalSize) / 100F
//        } catch (e: ArithmeticException) {
            return 0F
//        }
    }

    private fun createWorkInfoObserver(entryValue: String): Observer<List<WorkInfo>> {
        return Observer { workInfoList ->
            workInfos[entryValue] = workInfoList.firstOrNull()
            val workInfo = workInfoList.firstOrNull()
            workInfo?.let {
                Log.d(TAG, "workInfo.state = ${it.state}")

                var newState: DatabaseStatus

                when (it.state) {
                    WorkInfo.State.SUCCEEDED ->
                        newState = DatabaseStatus(entryValue, DatabaseStatus.Companion.Statuses.DOWNLOADED)
                    WorkInfo.State.FAILED ->
                        newState = DatabaseStatus(entryValue, DatabaseStatus.Companion.Statuses.ERROR)
                    WorkInfo.State.ENQUEUED ->
                        newState = DatabaseStatus(entryValue, DatabaseStatus.Companion.Statuses.DOWNLOADING)
                    WorkInfo.State.RUNNING -> {
                        newState = DatabaseStatus(entryValue, DatabaseStatus.Companion.Statuses.DOWNLOADING, it.progress.getFloat(CoroutineDownloadWorker.Progress, 0F))
                    }
                    WorkInfo.State.BLOCKED ->
                        newState = DatabaseStatus(entryValue, DatabaseStatus.Companion.Statuses.ERROR)
                    WorkInfo.State.CANCELLED -> {
                        newState = DatabaseStatus(entryValue, DatabaseStatus.Companion.Statuses.NOT_DOWNLOADED)
                        //TODO deleteFile
                    }
                }

                _databaseStatuses[entryValue]?.value = newState
            }
        }
    }

    fun getDownloadWorkInfoLiveData(tag: String): State<DatabaseStatus>? {
        return databaseStatus[tag]
    }

}