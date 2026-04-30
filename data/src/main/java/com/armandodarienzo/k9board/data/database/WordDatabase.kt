package com.armandodarienzo.k9board.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.armandodarienzo.k9board.data.dao.WordDao
import com.armandodarienzo.k9board.data.model.WordEntity
import java.io.File

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {

        fun create(context: Context, dbName: String, assetPath: String): WordDatabase =
            Room.databaseBuilder(context, WordDatabase::class.java, dbName)
                .createFromAsset(assetPath)
                .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                .addCallback(pragmaCallback)
                .build()

        fun createFromFile(context: Context, dbName: String, file: File): WordDatabase =
            Room.databaseBuilder(context, WordDatabase::class.java, dbName)
                .createFromFile(file)
                .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                .addCallback(pragmaCallback)
                .build()

        private val pragmaCallback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                db.execSQL("PRAGMA synchronous = NORMAL")
            }
        }
    }
}
