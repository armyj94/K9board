package com.armandodarienzo.k9board

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.armandodarienzo.k9board.shared.getDatabaseName
import com.armandodarienzo.k9board.shared.model.SupportedLanguageTag
import com.armandodarienzo.k9board.viewmodel.DictionaryDataHelper.Companion.ASSETS_PATH
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import java.io.FileOutputStream

@HiltAndroidApp
class Key9App: Application() {

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            "download_channel",
            "Language pack download",
            NotificationManager.IMPORTANCE_HIGH
        )

        val defaultDBName = getDatabaseName(SupportedLanguageTag.AMERICAN.value)
        val languageRelativePath = "$ASSETS_PATH/$defaultDBName"
        val inputStream = this.assets.open(languageRelativePath)

        try {
            val outputFile = File(this.getDatabasePath(defaultDBName).path)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()

            outputStream.flush()
            outputStream.close()
        } catch (exception: Throwable) {
            throw RuntimeException("The default database couldn't be moved in the default folder.", exception)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

}