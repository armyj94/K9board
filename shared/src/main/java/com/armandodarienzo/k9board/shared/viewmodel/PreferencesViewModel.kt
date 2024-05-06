package com.armandodarienzo.k9board.shared.viewmodel

import androidx.lifecycle.ViewModel
import com.armandodarienzo.k9board.shared.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// view model
@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    fun cacheTheme(
        theme: String
    ) = flow {
        userPreferencesRepository.setTheme(theme)
        //emit(MainEvent.NamedCachedSuccess)
        emit(theme)
    }

    fun getCachedTheme() = flow {
        val result = userPreferencesRepository.getTheme()
        val theme = result.getOrNull().orEmpty() // don't care if it failed right now but you might
        //emit(MainEvent.CachedNameFetchSuccess(name))
        emit(theme)
    }
}