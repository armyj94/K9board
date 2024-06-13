package com.armandodarienzo.k9board.shared.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.armandodarienzo.k9board.shared.LANGUAGE_TAG_ENGLISH_AMERICAN
import com.armandodarienzo.k9board.shared.SHARED_PREFS_DOUBLE_SPACE_CHARACTER
import com.armandodarienzo.k9board.shared.SHARED_PREFS_HAPTIC_FEEDBACK
import com.armandodarienzo.k9board.shared.SHARED_PREFS_KEYBOARD_SIZE
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_THEME
import com.armandodarienzo.k9board.shared.SHARED_PREFS_START_MANUAL
import com.armandodarienzo.k9board.shared.THEME_MATERIAL_YOU
import com.armandodarienzo.k9board.shared.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepositoryLocal @Inject constructor(
    private val userDataStorePreferences: DataStore<Preferences>
) : UserPreferencesRepository {

    override suspend fun setTheme(theme: String) {
        setPreference(KEY_THEME, theme)
    }

    override suspend fun getTheme(): Result<String> {
        return getPreference(KEY_THEME, THEME_MATERIAL_YOU)
    }

    override suspend fun setLanguage(language: String) {
        setPreference(KEY_LANGUAGE, language)
    }

    override suspend fun getLanguage(): Result<String> {
        return getPreference(KEY_THEME, LANGUAGE_TAG_ENGLISH_AMERICAN)
    }

    override suspend fun setKeyboardSize(keySize: KeyboardSize) {
        setPreference(KEY_KEYBOARD_SIZE, keySize.value)
    }

    override suspend fun getKeyboardSize(): Result<KeyboardSize> {
        return getPreference(KEY_KEYBOARD_SIZE, KeyboardSize.MEDIUM.value)
            .mapCatching { (KeyboardSize from it) ?: KeyboardSize.MEDIUM }
    }

    override suspend fun setHapticFeedback(enabled: Boolean) {
        setPreference(KEY_HAPTIC_FEEDBACK, enabled)
    }

    override suspend fun isHapticFeedbackEnabled(): Result<Boolean> {
        return getPreference(KEY_HAPTIC_FEEDBACK, false)
    }

    override suspend fun setDoubleSpaceCharacter(character: DoubleSpaceCharacter) {
        setPreference(KEY_DOUBLE_SPACE_CHARACTER, character.value)
    }

    override suspend fun getDoubleSpaceCharacter(): Result<DoubleSpaceCharacter> {
        return getPreference(KEY_DOUBLE_SPACE_CHARACTER, DoubleSpaceCharacter.NONE.value)
            .mapCatching { (DoubleSpaceCharacter from it) ?: DoubleSpaceCharacter.NONE }
    }

    override suspend fun setStartWithManual(enabled: Boolean) {
        setPreference(KEY_START_MANUAL, enabled)
    }

    override suspend fun isStartWithManualEnabled(): Result<Boolean> {
        return getPreference(KEY_START_MANUAL, false)
    }


    private suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        Result.runCatching {
            userDataStorePreferences.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    private suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Result<T> {
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

        val KEY_KEYBOARD_SIZE = doublePreferencesKey(
            name = SHARED_PREFS_KEYBOARD_SIZE
        )

        val KEY_HAPTIC_FEEDBACK = booleanPreferencesKey(
            name = SHARED_PREFS_HAPTIC_FEEDBACK
        )

        val KEY_DOUBLE_SPACE_CHARACTER = stringPreferencesKey(
            name = SHARED_PREFS_DOUBLE_SPACE_CHARACTER
        )

        val KEY_START_MANUAL = booleanPreferencesKey(
            name = SHARED_PREFS_START_MANUAL
        )
    }

}