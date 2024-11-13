package com.armandodarienzo.k9board.shared.model

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
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
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.random.Random

class CoroutineDownloadWorker(
    private val context: Context,
    private val params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result{

        val languageTag = inputData.getString("languageTag")
        val dbName = "${DATABASE_NAME}_${languageTag}.sqlite"
        val link = "${WEBSITE_URL}dictionaries/$dbName"
        val path = applicationContext.getDatabasePath(dbName).path


        val firstUpdate = workDataOf(Pair(Progress, 0F))

//        startForegorundService()
        setProgress(firstUpdate)
        withContext(Dispatchers.IO){

            try{
                val url = URL(link)
                (url.openConnection() as HttpURLConnection).also {
                    it.connectTimeout = 10000
                    it.readTimeout = 10000
                    it.connect()

                    val length = it.contentLengthLong
                    it.inputStream.use { input ->
                        FileOutputStream(File(path)).use { output ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            if(it.responseCode == 200){
                                var bytesRead = input.read(buffer)
                                var bytesCopied = 0L
                                while (bytesRead > 0) {

                                    output.write(buffer, 0, bytesRead)
                                    bytesCopied += bytesRead

                                    val update = workDataOf(Pair(Progress, (bytesCopied.toFloat()/length)))
                                    setProgress(update)

                                    bytesRead = input.read(buffer)

                                }

                            }

                        }

                    }

                }

            } catch (e: IOException){
                e.printStackTrace()
                return@withContext Result.failure()
            } catch (e: SocketTimeoutException){
                e.printStackTrace()
                return@withContext Result.failure()
            } catch (e: ConnectException){
                e.printStackTrace()
                return@withContext Result.failure()
            } catch (e: Exception){
                e.printStackTrace()
                return@withContext Result.failure()
            }


        }

        return Result.success()
    }

    private suspend fun startForegorundService() {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, "download_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText("Downloading language pack")
                    .setContentTitle("Download in progress")
                    .build()
            )
        )
    }



    companion object {
        const val Progress = "Progress"
    }
}