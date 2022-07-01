package ru.vladik.playlists.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import ru.vladik.playlists.constants.Strings

object SharedPreferences {
    fun saveVkUserToken(context: Context, token: String?) {
        val preferences: SharedPreferences =
            context.getSharedPreferences(Strings.SHARED_PREFERENCES_AUTH, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(Strings.SHARED_PREFERENCES_VK_USER_TOKEN, token)
        editor.apply()
    }

    fun getVkUserToken(context: Context): String? {
        val preferences =
            context.getSharedPreferences(Strings.SHARED_PREFERENCES_AUTH, Context.MODE_PRIVATE)
        return preferences.getString(Strings.SHARED_PREFERENCES_VK_USER_TOKEN, null)
    }
}