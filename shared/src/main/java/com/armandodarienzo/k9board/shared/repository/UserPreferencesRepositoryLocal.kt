package com.armandodarienzo.k9board.shared.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.armandodarienzo.k9board.shared.LANGUAGE_TAG_ENGLISH_AMERICAN
import com.armandodarienzo.k9board.shared.SHARED_PREFS_KEYS_SIZE
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_THEME
import com.armandodarienzo.k9board.shared.THEME_MATERIAL_YOU
import com.armandodarienzo.k9board.shared.model.KeyboardKeySize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepositoryLocal @Inject constructor(
    private val userDataStorePreferences: DataStore<Preferences>
) : UserPreferencesRepository {

    override suspend fun setTheme(theme: String) {
        setStringPreference(KEY_THEME, theme)
    }

    override suspend fun getTheme(): Result<String> {
        return getStringPreference(KEY_THEME, THEME_MATERIAL_YOU)
    }

    override suspend fun setLanguage(language: String) {
        setStringPreference(KEY_LANGUAGE, language)
    }

    override suspend fun getLanguage(): Result<String> {
        return getStringPreference(KEY_THEME, LANGUAGE_TAG_ENGLISH_AMERICAN)
    }

    override suspend fun setKeySize(keySize: KeyboardKeySize) {
        setStringPreference(KEY_KEYS_SIZE, keySize.value)
    }

    override suspend fun getKeySize(): Result<KeyboardKeySize> {
        return getStringPreference(KEY_KEYS_SIZE, KeyboardKeySize.MEDIUM.value)
            .mapCatching { KeyboardKeySize.from(it) ?: KeyboardKeySize.MEDIUM }
    }


    private suspend fun <T> setStringPreference(key: Preferences.Key<T>, value: T) {
        Result.runCatching {
            userDataStorePreferences.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    private suspend fun <T> getStringPreference(key: Preferences.Key<T>, defaultValue: T): Result<T> {
        return Result.runCatching {
            val flow = userDataStorePreferences.data
                .catch { exception ->
                    /*
                     * dataStore.data throws an IOException when an error
                     * is encountered when reading data
                     */
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    // Get our theme value, defaulting to "" if not set
                    preferences[key]
                }
            val value = flow.firstOrNull() ?: defaultValue
            value
        }
    }

    private companion object {

        val KEY_THEME = stringPreferencesKey(
            name = SHARED_PREFS_SET_THEME
        )

        val KEY_LANGUAGE = stringPreferencesKey(
            name = SHARED_PREFS_SET_LANGUAGE
        )

        val KEY_KEYS_SIZE = intPreferencesKey(
            name = SHARED_PREFS_KEYS_SIZE
        )

    }

}