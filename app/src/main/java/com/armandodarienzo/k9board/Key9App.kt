package com.armandodarienzo.k9board

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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
        val channel = NotificationChannel(
            "download_channel",
            "Language pack download",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

    }

}