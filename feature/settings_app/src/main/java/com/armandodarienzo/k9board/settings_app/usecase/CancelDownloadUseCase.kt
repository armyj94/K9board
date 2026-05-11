package com.armandodarienzo.k9board.settings_app.usecase

import android.content.Context
import androidx.work.WorkManager
import com.armandodarienzo.k9board.shared.getDatabaseName
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CancelDownloadUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(tag: String) {
        WorkManager.getInstance(context).cancelUniqueWork(tag)
        context.deleteDatabase(getDatabaseName(tag))
    }
}