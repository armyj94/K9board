package com.armandodarienzo.k9board.settings_app.usecase

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.armandodarienzo.k9board.shared.model.CoroutineDownloadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DownloadLanguagePackUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(tag: String) {
        val data = Data.Builder().putString("languageTag", tag).build()
        val request = OneTimeWorkRequestBuilder<CoroutineDownloadWorker>().apply {
            setInputData(data)
            setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 3000, TimeUnit.MILLISECONDS)
            setInitialDelay(1000, TimeUnit.MILLISECONDS)
            addTag(tag)
        }.build()

        WorkManager.getInstance(context)
            .beginUniqueWork(tag, ExistingWorkPolicy.REPLACE, request)
            .enqueue()
    }
}