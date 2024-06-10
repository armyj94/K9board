package com.armandodarienzo.k9board.shared.repository

import com.armandodarienzo.k9board.shared.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.shared.model.KeyboardSize

interface UserPreferencesRepository {

    suspend fun setTheme(
        theme: String
    )

    suspend fun getTheme(): Result<String>

    suspend fun setLanguage(language: String)

    suspend fun getLanguage(): Result<String>

    suspend fun setKeyboardSize(
        keySize: KeyboardSize
    )

    suspend fun getKeyboardSize(): Result<KeyboardSize>

    suspend fun setHapticFeedback(enabled: Boolean)

    suspend fun isHapticFeedbackEnabled(): Result<Boolean>

    suspend fun setDoubleSpaceCharacter(character: DoubleSpaceCharacter)

    suspend fun getDoubleSpaceCharacter(): Result<DoubleSpaceCharacter>

    suspend fun setStartWithManual(enabled: Boolean)

    suspend fun isStartWithManualEnabled(): Result<Boolean>

}