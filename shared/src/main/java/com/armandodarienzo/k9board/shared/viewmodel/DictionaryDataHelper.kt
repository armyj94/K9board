package com.armandodarienzo.k9board.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.preference.PreferenceManager
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.armandodarienzo.k9board.*
import com.armandodarienzo.k9board.model.Word
import com.armandodarienzo.k9board.shared.ASSET_PACKS_BASE_NAME
import com.armandodarienzo.k9board.shared.LANGUAGE_TAG_ENGLISH_AMERICAN
import com.armandodarienzo.k9board.shared.SHARED_PREFS_SET_LANGUAGE
import com.armandodarienzo.k9board.shared.USER_WORDS_FLAG
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception


//const val TAG = "DictionaryDataHelper"



class DictionaryDataHelper(val context: Context, private val dbName: String): SQLiteOpenHelper(context, dbName, null, REQUIRED_DATABASE_VERSION) {


//    private fun installIfNecessary() {
//
////        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
////        val setLanguage = prefs.getString(SHARED_PREFS_SET_LANGUAGE, LANGUAGE_TAG_ENGLISH_AMERICAN)
//
//        var setLanguage: String
//        val languageSetKey = stringPreferencesKey(SHARED_PREFS_SET_LANGUAGE)
//        val scope = CoroutineScope(Dispatchers.Main)
//        val languageRelativePath = "$ASSETS_PATH/$dbName"
//        Log.d("armando_debug", "languageRelativePath : $languageRelativePath");
//
//        scope.launch {
//            var languageSetState = flow {
//                context.dataStore.data.map {
//                    it[languageSetKey]
//                }.filterNotNull().collect(collector = {
//                    this.emit(it)
//                })
//            }.collectLatest {
//
//                setLanguage = it
//
//                if(!dbInstalled()){
//
//                    val inputStream: InputStream
//                    if (setLanguage == LANGUAGE_TAG_ENGLISH_AMERICAN)
//                        inputStream = context.assets.open(languageRelativePath)
//                    else
//                    {
//                        val assetPackManager = AssetPackManagerFactory.getInstance(context)
//                        val assetPackPath = assetPackManager.getPackLocation("${ASSET_PACKS_BASE_NAME}_${setLanguage?.replace("-", "_")}")
//
//                        val assetsFolderPath = assetPackPath?.assetsPath()
//                        val assetPath = "$assetsFolderPath/$languageRelativePath"
//
//                        Log.d("armando_debug", "assetPath is $assetPath")
//
//                        inputStream = FileInputStream(File(assetPath))
//                    }
//
//                    try {
//
//                        val outputFile = File(context.getDatabasePath(dbName).path)
//                        val outputStream = FileOutputStream(outputFile)
//
//                        inputStream.copyTo(outputStream)
//                        inputStream.close()
//
//                        outputStream.flush()
//                        outputStream.close()
//                    } catch (exception: Throwable) {
//                        throw RuntimeException("The default database couldn't be moved in the default folder.", exception)
//                    }
//
//                }
//
//
//            }
//        }
//
//
//
//
//
//    }

    private fun installIfNecessary() {

        //TODO: change with PreferenceDataStore
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val setLanguage = prefs.getString(SHARED_PREFS_SET_LANGUAGE, LANGUAGE_TAG_ENGLISH_AMERICAN)
        val languageRelativePath = "$ASSETS_PATH/$dbName"

        if(!dbInstalled()){

            val inputStream: InputStream
            if (setLanguage == LANGUAGE_TAG_ENGLISH_AMERICAN)
                inputStream = context.assets.open(languageRelativePath)
            else
            {
                val assetPackManager = AssetPackManagerFactory.getInstance(context)
                val assetPackPath = assetPackManager.getPackLocation("${ASSET_PACKS_BASE_NAME}_${setLanguage?.replace("-", "_")}")

                val assetsFolderPath = assetPackPath?.assetsPath()
                val assetPath = "$assetsFolderPath/$languageRelativePath"

                inputStream = FileInputStream(File(assetPath))
            }

            try {

                val outputFile = File(context.getDatabasePath(dbName).path)
                val outputStream = FileOutputStream(outputFile)

                inputStream.copyTo(outputStream)
                inputStream.close()

                outputStream.flush()
                outputStream.close()
            } catch (exception: Throwable) {
                throw RuntimeException("The default database couldn't be moved in the default folder.", exception)
            }

        }
    }


    private fun dbInstalled(): Boolean {

        val dbFile = File(context.getDatabasePath(dbName).path)
        return dbFile.exists()

    }


    override fun getWritableDatabase(): SQLiteDatabase {
//        throw RuntimeException("The $DATABASE_NAME database is not writable.")
//        Log.d(TAG, "getWritableDatabase")
        var db = getWritableDatabaseNoUpdate()
        //installOrUpdateIfNecessary()
        updateIfNecessary(db)
        return db
    }

    private fun updateIfNecessary(db: SQLiteDatabase) {
        val dbVersion = getDBVersion(db)


        if(dbVersion != REQUIRED_DATABASE_VERSION){
            var queryString: String

            try{
//                if(dbVersion <= 2) {
//                    queryString = "CREATE TABLE \"prova\" (\n" +
//                            "\t\"test\"\tINTEGER\n" +
//                            ");"
//                    db.execSQL(queryString)
//                    updateDBVersion(db, 2)
//                    Log.d(TAG, "Updated db to version 2")
//
//                }
            } catch (e: Exception) {
                Log.e("Exception", "Error while trying to update DB")
            }

        }
    }

    fun getWritableDatabaseNoUpdate(): SQLiteDatabase {
//        Log.d(TAG, "getWritableDatabase")
        installIfNecessary()
        return super.getWritableDatabase()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        throw RuntimeException("The $dbName database is not read-only.")
    }

    @SuppressLint("Range")
    fun getWordsByCode(code: String): MutableList<Word>{

        val db = writableDatabase

        var words = arrayListOf<Word>()
        var queryString = "SELECT $WORDS_TABLE_COLUMN_TEXT, " +
                "$WORDS_TABLE_COLUMN_FREQUENCY, " +
                "$WORDS_TABLE_COLUMN_FLAGS, " +
                "$WORDS_TABLE_COLUMN_ORIGINALFREQUENCY, " +
                "$WORDS_TABLE_COLUMN_OFFENSIVE, " +
                "$WORDS_TABLE_COLUMN_T9CODE " +
                "FROM $WORDS_TABLE_NAME " +
                "WHERE $WORDS_TABLE_COLUMN_T9CODE = \"$code\" " +
                "ORDER BY  $WORDS_TABLE_COLUMN_FREQUENCY DESC, $WORDS_TABLE_COLUMN_TEXT ASC"// AND $WORDS_TABLE_COLUMN_LANGUAGE = '${locale.language}' AND $WORDS_TABLE_COLUMN_REGION = '${locale.country}'"

//        Log.d(TAG, queryString)


        var cursor = db.rawQuery(queryString, null)


        try{

            if (cursor.moveToNext()){

                do{


                    var word_text = cursor.getString(cursor.getColumnIndex(WORDS_TABLE_COLUMN_TEXT))
                    var word_frequency = cursor.getInt(cursor.getColumnIndex(WORDS_TABLE_COLUMN_FREQUENCY))
                    var word_flags = cursor.getString(cursor.getColumnIndex(WORDS_TABLE_COLUMN_FLAGS))
                    var word_originalFrequency = cursor.getInt(cursor.getColumnIndex(WORDS_TABLE_COLUMN_ORIGINALFREQUENCY))
                    var word_possiblyOffensive = cursor.getString(cursor.getColumnIndex(WORDS_TABLE_COLUMN_OFFENSIVE))
                    var word_code = cursor.getString(cursor.getColumnIndex(WORDS_TABLE_COLUMN_T9CODE))


                    var word =
                        Word(
                            word_text,
                            word_frequency,
                            word_flags,
                            word_originalFrequency,
                            word_possiblyOffensive,
                            word_code
                        )
                    words.add(word)

                } while (cursor.moveToNext())

            }

        } catch (e: Exception) {
            Log.e("Exception", "Error while trying to get words from database: ${e.message}")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }

        return words

    }

    @SuppressLint("Range")
    fun gePlaceholderWordsByCode(code: String, queryDepth: Int): MutableList<Word>{

        var sequenceOf0= generateSequence { 0 }
        var sequenceOf9 = generateSequence { 9 }

        val db = writableDatabase//readableDatabase
        var words = arrayListOf<Word>()
        var queryString = "SELECT substr($WORDS_TABLE_COLUMN_TEXT,1,${code.length}) AS $WORDS_TABLE_COLUMN_TEXT, " +
                "MAX($WORDS_TABLE_COLUMN_FREQUENCY) AS $WORDS_TABLE_COLUMN_FREQUENCY, " +
                //"$WORDS_TABLE_COLUMN_FLAGS, " +
                "MAX($WORDS_TABLE_COLUMN_ORIGINALFREQUENCY) AS $WORDS_TABLE_COLUMN_ORIGINALFREQUENCY " +
                "FROM $WORDS_TABLE_NAME " +
                "WHERE $WORDS_TABLE_COLUMN_T9CODE BETWEEN \"${code}${sequenceOf0.take(queryDepth).toList().joinToString("")}\" AND \"${code}${sequenceOf9.take(queryDepth).toList().joinToString("")}\"" +
                "GROUP BY substr($WORDS_TABLE_COLUMN_TEXT,1,${code.length})"
        val cursor = db.rawQuery(queryString, null)

//        Log.d(TAG, queryString)

        try{

            if (cursor.moveToFirst()){

                do{

                    var word_text = cursor.getString(cursor.getColumnIndex(WORDS_TABLE_COLUMN_TEXT))
                    var word_frequency = cursor.getInt(cursor.getColumnIndex(WORDS_TABLE_COLUMN_FREQUENCY))
                    var word_originalFrequency = cursor.getInt(cursor.getColumnIndex(WORDS_TABLE_COLUMN_ORIGINALFREQUENCY))

                    var word =
                        Word(
                            word_text,
                            word_frequency,
                            "",
                            word_originalFrequency,
                            "",
                            code
                        )
                    words.add(word)

                } while (cursor.moveToNext())

            }

        } catch (e: Exception) {
            Log.e("Exception", "Error while trying to get words from database: ${e.stackTrace}")

        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }


        return words

    }

    @SuppressLint("Range")
    fun getWordsByFlag(flags: String): MutableList<Word>{

        val db = writableDatabase

        var words = arrayListOf<Word>()
        var queryString = "SELECT $WORDS_TABLE_COLUMN_TEXT, " +
                "$WORDS_TABLE_COLUMN_FREQUENCY, " +
                "$WORDS_TABLE_COLUMN_FLAGS, " +
                "$WORDS_TABLE_COLUMN_ORIGINALFREQUENCY, " +
                "$WORDS_TABLE_COLUMN_OFFENSIVE, " +
                "$WORDS_TABLE_COLUMN_T9CODE " +
                "FROM $WORDS_TABLE_NAME " +
                "WHERE $WORDS_TABLE_COLUMN_FLAGS = \"$flags\" " +
                "ORDER BY  $WORDS_TABLE_COLUMN_FREQUENCY DESC, $WORDS_TABLE_COLUMN_TEXT ASC"// AND $WORDS_TABLE_COLUMN_LANGUAGE = '${locale.language}' AND $WORDS_TABLE_COLUMN_REGION = '${locale.country}'"

        Log.d("Exception", queryString)


        var cursor = db.rawQuery(queryString, null)


        try{

            if (cursor.moveToNext()){

                do{


                    var word_text = cursor.getString(cursor.getColumnIndex(WORDS_TABLE_COLUMN_TEXT))
                    var word_frequency = cursor.getInt(cursor.getColumnIndex(WORDS_TABLE_COLUMN_FREQUENCY))
                    var word_flags = cursor.getString(cursor.getColumnIndex(WORDS_TABLE_COLUMN_FLAGS))
                    var word_originalFrequency = cursor.getInt(cursor.getColumnIndex(WORDS_TABLE_COLUMN_ORIGINALFREQUENCY))
                    var word_possiblyOffensive = cursor.getString(cursor.getColumnIndex(WORDS_TABLE_COLUMN_OFFENSIVE))
                    var word_code = cursor.getString(cursor.getColumnIndex(WORDS_TABLE_COLUMN_T9CODE))


                    var word =
                        Word(
                            word_text,
                            word_frequency,
                            word_flags,
                            word_originalFrequency,
                            word_possiblyOffensive,
                            word_code
                        )
                    words.add(word)

                } while (cursor.moveToNext())

            }

        } catch (e: Exception) {
            Log.e("Exception", "Error while trying to get words from database: ${e.message}")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }

        return words

    }

    fun getUserWords(): MutableList<Word>{
        return getWordsByFlag(USER_WORDS_FLAG)
    }

    @SuppressLint("Range")
    fun getMeanFrequency(): Int{
        val db = writableDatabase
        val queryString = "SELECT AVG($WORDS_TABLE_COLUMN_FREQUENCY) FROM $WORDS_TABLE_NAME"
        var wordFrequency: Int = 0

        val cursor = db.rawQuery(queryString, null)

        try{

            if (cursor.moveToFirst()){

                wordFrequency = cursor.getInt(cursor.getColumnIndex("AVG($WORDS_TABLE_COLUMN_FREQUENCY)"))

            }

        } catch (e: Exception) {
            Log.d("Exception", "Error while trying to get words from database")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }

        return wordFrequency

    }


    @SuppressLint("Range")
    fun getMaxLength(): Int{
        val db = writableDatabase
        val queryString = "SELECT MAX(LENGTH($WORDS_TABLE_COLUMN_TEXT)) FROM $WORDS_TABLE_NAME"
        var wordMaxLength: Int = 0

        val cursor = db.rawQuery(queryString, null)

        try{

            if (cursor.moveToFirst()){

                wordMaxLength = cursor.getInt(cursor.getColumnIndex("MAX(LENGTH($WORDS_TABLE_COLUMN_TEXT))"))

            }

        } catch (e: Exception) {
            Log.e("Exception", "Error while trying to get words from database")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }

        return wordMaxLength

    }

    @SuppressLint("Range")
    private fun getDBVersion(db: SQLiteDatabase): Int{
        val queryString = "SELECT $DB_CONFIG_COLUMN_VERSION FROM $DB_CONFIG_TABLE_NAME;"
        var dbVersion: Int = 0

        val cursor = db.rawQuery(queryString, null)

        try{

            if (cursor.moveToFirst()){

                dbVersion = cursor.getInt(cursor.getColumnIndex("$DB_CONFIG_COLUMN_VERSION"))

            }

        } catch (e: Exception) {
            Log.e("Exception", "Error while trying to get database version")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }

        return dbVersion


    }

    fun upsert(word: Word){

        val db = writableDatabase//readableDatabase
//        val queryString =
//            " WITH new ($WORDS_TABLE_COLUMN_TEXT, $WORDS_TABLE_COLUMN_FREQUENCY, $WORDS_TABLE_COLUMN_FLAGS, $WORDS_TABLE_COLUMN_ORIGINALFREQUENCY, " +
//                "$WORDS_TABLE_COLUMN_OFFENSIVE, $WORDS_TABLE_COLUMN_T9CODE) AS " +
//                "( VALUES('${word.text}', ${word.frequency}, '${word.flags}', ${word.originalFrequency}, '${word.possiblyOffensive}', '${word.t9Code}') ) " +
//                "INSERT OR REPLACE INTO $WORDS_TABLE_NAME ($WORDS_TABLE_COLUMN_TEXT, $WORDS_TABLE_COLUMN_FREQUENCY, $WORDS_TABLE_COLUMN_FLAGS, $WORDS_TABLE_COLUMN_ORIGINALFREQUENCY, "+
//                "$WORDS_TABLE_COLUMN_OFFENSIVE, $WORDS_TABLE_COLUMN_T9CODE) " +
//                " SELECT new.$WORDS_TABLE_COLUMN_TEXT, CASE WHEN old.$WORDS_TABLE_COLUMN_FREQUENCY IS NULL THEN new.$WORDS_TABLE_COLUMN_FREQUENCY ELSE old.$WORDS_TABLE_COLUMN_FREQUENCY + 1 END AS $WORDS_TABLE_COLUMN_FREQUENCY, " +
//                "CASE WHEN old.$WORDS_TABLE_COLUMN_FLAGS IS NULL THEN new.$WORDS_TABLE_COLUMN_FLAGS ELSE old.$WORDS_TABLE_COLUMN_FLAGS END AS $WORDS_TABLE_COLUMN_FLAGS, " +
//                "CASE WHEN old.$WORDS_TABLE_COLUMN_FREQUENCY IS NULL THEN new.$WORDS_TABLE_COLUMN_FREQUENCY ELSE old.$WORDS_TABLE_COLUMN_FREQUENCY END AS originalFrequency, new.$WORDS_TABLE_COLUMN_OFFENSIVE, " +
//                "new.$WORDS_TABLE_COLUMN_T9CODE " +
//                "FROM new LEFT JOIN $WORDS_TABLE_NAME AS old ON new.$WORDS_TABLE_COLUMN_TEXT = old.$WORDS_TABLE_COLUMN_TEXT;"

        val queryString =
            " WITH new ($WORDS_TABLE_COLUMN_TEXT, $WORDS_TABLE_COLUMN_FREQUENCY, $WORDS_TABLE_COLUMN_FLAGS, $WORDS_TABLE_COLUMN_ORIGINALFREQUENCY, " +
                    "$WORDS_TABLE_COLUMN_OFFENSIVE, $WORDS_TABLE_COLUMN_T9CODE) AS " +
                    "( VALUES('${word.text}', ${word.frequency}, '${word.flags}', ${word.originalFrequency}, '${word.possiblyOffensive}', '${word.t9Code}') ) " +
                    "INSERT OR REPLACE INTO $WORDS_TABLE_NAME ($WORDS_TABLE_COLUMN_TEXT, $WORDS_TABLE_COLUMN_FREQUENCY, $WORDS_TABLE_COLUMN_FLAGS, $WORDS_TABLE_COLUMN_ORIGINALFREQUENCY, "+
                    "$WORDS_TABLE_COLUMN_OFFENSIVE, $WORDS_TABLE_COLUMN_T9CODE) " +
                    " SELECT new.$WORDS_TABLE_COLUMN_TEXT, CASE WHEN old.$WORDS_TABLE_COLUMN_TEXT IS NULL THEN new.$WORDS_TABLE_COLUMN_FREQUENCY ELSE old.$WORDS_TABLE_COLUMN_FREQUENCY + 1 END AS $WORDS_TABLE_COLUMN_FREQUENCY, " +
                    "CASE WHEN old.$WORDS_TABLE_COLUMN_TEXT IS NULL THEN new.$WORDS_TABLE_COLUMN_FLAGS ELSE old.$WORDS_TABLE_COLUMN_FLAGS END AS $WORDS_TABLE_COLUMN_FLAGS, " +
                    "CASE WHEN old.$WORDS_TABLE_COLUMN_TEXT IS NULL THEN new.$WORDS_TABLE_COLUMN_FREQUENCY ELSE old.$WORDS_TABLE_COLUMN_FREQUENCY END AS originalFrequency, new.$WORDS_TABLE_COLUMN_OFFENSIVE, " +
                    "new.$WORDS_TABLE_COLUMN_T9CODE " +
                    "FROM new LEFT JOIN $WORDS_TABLE_NAME AS old ON new.$WORDS_TABLE_COLUMN_TEXT = old.$WORDS_TABLE_COLUMN_TEXT;"

//        Log.d(TAG, queryString)

        try{

            db.execSQL(queryString)

        } catch (e: Exception) {
            Log.e("Exception", "Error while trying insert word into database")
        }

    }

    fun delete(wordText: String){

        val db = writableDatabase

        val where = "$WORDS_TABLE_COLUMN_TEXT=?"
        val whereArgs = arrayOf(wordText)

        try {
            db.delete(WORDS_TABLE_NAME, where, whereArgs)
        } catch (e: Exception) {
            Log.e("Exception", "Error while trying delete word \"${wordText}\" into database")
        }


    }


    fun saveUserWord(wordText: String){

        val db = writableDatabase

//        val where = "$WORDS_TABLE_COLUMN_TEXT=?"
//        val whereArgs = arrayOf(wordText)

        val queryString = "UPDATE $WORDS_TABLE_NAME SET $WORDS_TABLE_COLUMN_FLAGS = REPLACE($WORDS_TABLE_COLUMN_FLAGS, \"${USER_WORDS_FLAG}\", \"\") " +
                "WHERE $WORDS_TABLE_COLUMN_TEXT = \"$wordText\""


        try {
            db.execSQL(queryString)
        } catch (e: Exception) {
            Log.e("Exception", "Error while trying update word \"${wordText}\" into database")
        }


    }



    fun updateDBVersion(db: SQLiteDatabase, version: Int){
        val queryString = "UPDATE $DB_CONFIG_TABLE_NAME SET $DB_CONFIG_COLUMN_VERSION = $version"

        try{
            db.execSQL(queryString)
        } catch (e: Exception) {
            Log.e("Exception", "Error while trying to update db version")
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Nothing to do
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Nothing to do
    }



    companion object {

        const val ASSETS_PATH = "databases"
        const val REQUIRED_DATABASE_VERSION = 1

        //words table
        const val WORDS_TABLE_NAME = "words"
        const val WORDS_TABLE_COLUMN_TEXT = "text"
        const val WORDS_TABLE_COLUMN_FREQUENCY = "frequency"
        const val WORDS_TABLE_COLUMN_FLAGS = "flags"
        const val WORDS_TABLE_COLUMN_ORIGINALFREQUENCY = "originalFrequency"
        const val WORDS_TABLE_COLUMN_OFFENSIVE = "possiblyOffensive"
        const val WORDS_TABLE_COLUMN_T9CODE = "t9code"

        //db_config table
        const val DB_CONFIG_TABLE_NAME = "db_config"
        const val DB_CONFIG_COLUMN_VERSION = "version"

    }

}