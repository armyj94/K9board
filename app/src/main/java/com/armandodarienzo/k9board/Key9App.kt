package com.armandodarienzo.k9board

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.armandodarienzo.k9board.shared.USER_PREFERENCES_NAME

import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Key9App: Application() {

    override fun onCreate() {
        super.onCreate()



//        DynamicColors.applyToActivitiesIfAvailable(this)
    }

}