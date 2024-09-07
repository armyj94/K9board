package com.armandodarienzo.k9board.shared.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armandodarienzo.k9board.shared.model.SupportedLanguageTag
import com.armandodarienzo.k9board.shared.packName
import com.armandodarienzo.k9board.shared.repository.UserPreferencesRepository
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.ktx.packStates
import com.google.android.play.core.ktx.requestFetch
import com.google.android.play.core.ktx.requestPackStates
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LanguageViewModel@Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel()  {

    private val TAG = "LanguageViewModel"

    private val assetPackManager = AssetPackManagerFactory.getInstance(mContext)

    private val languagePackNames = SupportedLanguageTag.entries
        .toMutableList()
        .map { it.value }
        .map { tag ->
           packName(tag)
        }

    private val _languageState = mutableStateOf(SupportedLanguageTag.AMERICAN.value)
    val languageState : State<String> = _languageState

    private var _assetPackStatesMapState = mutableStateOf<Map<String, AssetPackState>>(emptyMap())
    val assetPackStatesMapState : State<Map<String, AssetPackState>> = _assetPackStatesMapState

    init {
        viewModelScope.launch {

            _languageState.value = userPreferencesRepository.getLanguage().getOrNull()!!

            assetPackManager.requestPackStates(
                languagePackNames.minus(packName(SupportedLanguageTag.AMERICAN.value))
            ).runCatching {
                _assetPackStatesMapState.value = this.packStates()
                assetPackManager.registerListener { assetPackState ->
                    val map = _assetPackStatesMapState.value.toMutableMap()
                    map[assetPackState.name()] = assetPackState
                    _assetPackStatesMapState.value = map
                }

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
        val packName = packName(tag)
        viewModelScope.launch {
            assetPackManager.requestFetch(listOf(packName))
        }
    }

    fun cancelDownload(tag: String) {
        val packName = packName(tag)
        assetPackManager.cancel(listOf(packName)).packStates.values.forEach {
            Log.d(TAG, "cancelDownload assetPackState.status = ${it.status()}")
            val map = _assetPackStatesMapState.value.toMutableMap()
            map[it.name()] = it
            _assetPackStatesMapState.value = map
        }
        assetPackManager.removePack(packName)
    }

    fun removePack(tag: String) {
        val packName = packName(tag)
        if (languageState.value == tag) {
            setLanguage(SupportedLanguageTag.AMERICAN.value)
        }

        //TODO: remove copied database files

        assetPackManager.removePack(packName)
    }

    fun getDownloadProgress(tag: String) : Float {
        val map = _assetPackStatesMapState.value
        val packName = packName(tag)

        val downloaded = map[packName]?.bytesDownloaded() ?: 0
        val totalSize = map[packName]?.totalBytesToDownload() ?: 0

        try {
            return (100L * downloaded / totalSize) / 100F
        } catch (e: ArithmeticException) {
            return 0F
        }
    }

}