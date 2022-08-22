package ru.vladik.playlists.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.vladik.playlists.dataclasses.PlatformUserRegistrationInfo
import ru.vladik.playlists.dataclasses.musicservises.MusicService

class PlatformRegistrationSqlHelper(
    context: Context?,
    ) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "m"
        private const val TABLE_NAME = "app_users"
        private const val KEY_USER_ID = "id"
        private const val KEY_PLATFORM_ID = "platformId"
        private const val KEY_USERNAME = "username"
        private const val KEY_TOKEN = "token"
        private const val KEY_LOGGED_IN = "loggedIn"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME ($KEY_PLATFORM_ID INTEGER PRIMARY KEY, " +
                    "$KEY_USER_ID TEXT, $KEY_USERNAME TEXT, $KEY_TOKEN TEXT, $KEY_LOGGED_IN INTEGER)")
    }

    fun saveRegistration(registrationInfo: PlatformUserRegistrationInfo) {
        val values = ContentValues()
        with(values) {
            put(KEY_PLATFORM_ID, registrationInfo.musicPlatformId)
            put(KEY_USER_ID, registrationInfo.userId)
            put(KEY_USERNAME, registrationInfo.username)
            put(KEY_TOKEN, registrationInfo.token)
            put(KEY_LOGGED_IN, if (registrationInfo.loggedIn) 1 else 0)
        }
        writableDatabase.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getServiceRegistrationInfo(service: MusicService): PlatformUserRegistrationInfo? {
        val cursor = readableDatabase.query(TABLE_NAME, arrayOf(KEY_USER_ID, KEY_USERNAME, KEY_TOKEN, KEY_LOGGED_IN),
            "$KEY_PLATFORM_ID = ${service.id}", arrayOf(), null, null, null)
        if (cursor.columnCount != 4 || !cursor.moveToFirst()) return null
        val info = PlatformUserRegistrationInfo(
            service.id,
            cursor.getString(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getInt(3) == 1
        )
        cursor.close()
        return info
    }

    fun unregister(service: MusicService) {
        if (getServiceRegistrationInfo(service) == null) return
        val values = ContentValues()
        with(values) {
            put(KEY_LOGGED_IN, 0)
        }
        writableDatabase.update(TABLE_NAME, values, "$KEY_PLATFORM_ID = ${service.id}", null)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}