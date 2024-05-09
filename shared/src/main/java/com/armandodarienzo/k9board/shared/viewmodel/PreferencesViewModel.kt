package com.armandodarienzo.k9board.shared.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

// view model
@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _keyboardSizeState = mutableStateOf(KeyboardSize.MEDIUM)
    val keyboardSizeState : State<KeyboardSize> = _keyboardSizeState

    init {
        viewModelScope.launch {
            _keyboardSizeState.value = userPreferencesRepository.getKeyboardSize().getOrNull()!!
        }
    }

    fun setKeyboardSize(size: KeyboardSize) {
        _keyboardSizeState.value = size
        viewModelScope.launch {
            userPreferencesRepository.setKeyboardSize(size)
        }
    }
}