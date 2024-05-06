package com.armandodarienzo.k9board.shared.repository

import com.armandodarienzo.k9board.shared.model.KeyboardKeySize

interface UserPreferencesRepository {

    suspend fun setTheme(
        theme: String
    )

    suspend fun getTheme(): Result<String>

    suspend fun setLanguage(
        language: String
    )

    suspend fun getLanguage(): Result<String>

    suspend fun setKeySize(
        keySize: KeyboardKeySize
    )

    suspend fun getKeySize(): Result<KeyboardKeySize>

}