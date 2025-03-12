package com.online.course.manager.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.online.course.manager.BuildVars
import com.online.course.manager.Utils


class AppDb(private val context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val TAG = "AppDb"
        private const val DB_NAME = "app.db"
        private const val DB_VERSION = 1
        private const val APP_TABLE = "data"
        private const val COL_PRIMARY_KEY = "id"
        private const val COL_KEY = "col_key"
        private const val COL_VALUE = "col_value"
        private const val COURSE_KEY = "course"
        private const val APP_CONFIG_KEY = "app_config"
        private const val USER_KEY = "user"
        private const val TOKEN_KEY = "token"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + APP_TABLE + "(" +
                        COL_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                        COL_KEY + " TEXT , " +
                        COL_VALUE + " TEXT " +
                        ")"
            )
        } catch (ex: SQLiteException) {
            if (BuildVars.LOGS_ENABLED) {
                Log.e(TAG, ex.toString())
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}


    fun saveData(
        type: DataType,
        data: String,
        keySuffix: String? = null
    ) {
        var colKey = type.value
        if (keySuffix != null) {
            colKey += keySuffix
        }

        deleteData(type, keySuffix)

        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put(COL_KEY, colKey)
        values.put(COL_VALUE, data)
        db.insert(APP_TABLE, null, values)
    }

    @SuppressLint("Range")
    fun getData(
        type: DataType,
        keySuffix: String? = null
    ): String? {
        if (!Utils.doesDatabaseExist(context, DB_NAME)) {
            return null
        }

        val db: SQLiteDatabase = readableDatabase
        var colKey = type.value

        if (keySuffix != null) {
            colKey += keySuffix
        }

        val cursor = db.query(
            APP_TABLE, null, "$COL_KEY =?",
            arrayOf(colKey), null, null, null
        )
        if (cursor.moveToFirst()) {
            val data = cursor.getString(cursor.getColumnIndex(COL_VALUE))
            cursor.close()
            return data
        }
        cursor.close()
        return null
    }

    @SuppressLint("Range")
    fun getListData(
        type: DataType
    ): List<String>? {
        if (!Utils.doesDatabaseExist(context, DB_NAME)) {
            return null
        }

        val db: SQLiteDatabase = readableDatabase

        val cursor = db.query(
            APP_TABLE, null, "$COL_KEY LIKE ?",
            arrayOf("${type.value}%"), null, null, null
        )

        if (cursor.moveToFirst()) {
            val data = ArrayList<String>()

            do {
                data.add(cursor.getString(cursor.getColumnIndex(COL_VALUE)))
            } while (cursor.moveToNext())

            cursor.close()
            return data
        }
        cursor.close()
        return null
    }


    fun deleteData(type: DataType, keySuffix: String? = null) {
        if (!Utils.doesDatabaseExist(context, DB_NAME)) {
            return
        }

        var colKey = type.value
        if (keySuffix != null) {
            colKey += keySuffix
        }

        val db: SQLiteDatabase = writableDatabase
        db.delete(APP_TABLE, "$COL_KEY =?", arrayOf(colKey))
    }

    fun deleteAllData() {
        if (!Utils.doesDatabaseExist(context, DB_NAME)) {
            return
        }

        val db: SQLiteDatabase = writableDatabase
        db.delete(APP_TABLE, "$COL_KEY !=?", arrayOf(APP_CONFIG_KEY))
    }

    fun checkIfMyCoursesIsSaved(): Boolean {
        if (!Utils.doesDatabaseExist(context, DB_NAME)) {
            return false
        }

        val db: SQLiteDatabase = readableDatabase
        val cursor =
            db.query(
                APP_TABLE,
                null,
                "$COL_KEY LIKE ?",
                arrayOf("$COURSE_KEY%"),
                null,
                null,
                null
            )

        if (cursor.moveToFirst()) {
            return true
        }

        cursor.close()
        return false
    }

    enum class DataType(val value: String) {
        TOKEN(TOKEN_KEY),
        COURSE(COURSE_KEY),
        APP_CONFIG(APP_CONFIG_KEY),
        USER(USER_KEY)
    }
}