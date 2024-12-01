package com.armandodarienzo.k9board.shared.model


import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.armandodarienzo.k9board.shared.DATABASE_NAME
import com.armandodarienzo.k9board.shared.R
import com.armandodarienzo.k9board.shared.WEBSITE_URL
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.URL
import java.util.Locale
import kotlin.random.Random


class CoroutineDownloadWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val languageTag = inputData.getString("languageTag")
        val dbName = "${DATABASE_NAME}_${languageTag}.sqlite"
        val link = "${WEBSITE_URL}dictionaries/$dbName"
        val path = applicationContext.getDatabasePath(dbName).path


        val notificationId = Random.nextInt()

        val firstUpdate = workDataOf(Pair(Progress, 0F))

        setForeground(createForegroundInfo(notificationId, languageTag!!, 0))
        setProgress(firstUpdate)

        val result = withContext(Dispatchers.IO) {

            try {
                val url = URL(link)
                (url.openConnection() as HttpURLConnection).also {
                    it.connectTimeout = 10000
                    it.readTimeout = 10000
                    it.connect()

                    val length = it.contentLengthLong
                    it.inputStream.use { input ->
                        FileOutputStream(File(path)).use { output ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            if (it.responseCode == 200) {
                                var bytesRead = input.read(buffer)
                                var bytesCopied = 0L
                                var lastProgress = 0
                                while (bytesRead > 0) {

                                    output.write(buffer, 0, bytesRead)
                                    bytesCopied += bytesRead

                                    val progress = (bytesCopied.toFloat() / length)
                                    val normalizedProgress = (progress * 100).toInt()

                                    val update =
                                        workDataOf(Pair(Progress, progress))
                                    setProgress(update)

                                    if (normalizedProgress % 5 == 0
                                        && normalizedProgress != lastProgress
                                    ) {
                                        setForegroundAsync(
                                            createForegroundInfo(
                                                notificationId,
                                                languageTag,
                                                normalizedProgress
                                            )
                                        )
                                        lastProgress = normalizedProgress
                                    }

                                    bytesRead = input.read(buffer)

                                }

                            }

                        }

                    }

                    return@withContext Result.success()

                }

            } catch (e: SocketException) {

                return@withContext Result.retry()
            } catch (e: SocketTimeoutException) {

                return@withContext Result.retry()
            } catch (e: IOException) {

                return@withContext Result.failure()
            } catch (e: ConnectException) {

                return@withContext Result.failure()
            } catch (e: Exception) {

                return@withContext Result.failure()
            }


        }

        return result as Result
    }

    @NonNull
    private fun createForegroundInfo(
        notificationId: Int,
        languageTag: String,
        progress: Int
    ): ForegroundInfo {

        val languageName =
            Locale.forLanguageTag(languageTag).displayName.replaceFirstChar { it.uppercase() }

        val context = applicationContext

        val intent = WorkManager.getInstance(context).createCancelPendingIntent(id)

        val notification: Notification =
            NotificationCompat.Builder(context, "download_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Downloading $languageName")
                .setTicker("Download in progress")
                .setSilent(true)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_delete, "cancel", intent)
                .setProgress(100, progress, false)
                .build()

        return ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)

    }

    @NonNull
    private fun createForegroundInfoFailed(
        notificationId: Int,
        languageTag: String
    ): ForegroundInfo {

        val languageName =
            Locale.forLanguageTag(languageTag).displayName.replaceFirstChar { it.uppercase() }

        val context = applicationContext

        val notification: Notification =
            NotificationCompat.Builder(context, "download_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Failed to download $languageName")
                .build()

        return ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)

    }


    companion object {
        const val Progress = "Progress"
    }
}