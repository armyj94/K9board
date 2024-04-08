package com.armandodarienzo.k9board

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.color.DynamicColors

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class Key9App: Application() {

    override fun onCreate() {
        super.onCreate()



//        DynamicColors.applyToActivitiesIfAvailable(this)
    }

}