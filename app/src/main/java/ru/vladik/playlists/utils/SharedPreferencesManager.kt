package ru.vladik.playlists.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import ru.vladik.playlists.api.vk.models.User as VkUser
import ru.vladik.playlists.constants.SHARED_PREFERENCES_AUTH
import ru.vladik.playlists.constants.SHARED_PREFERENCES_VK_USER
import ru.vladik.playlists.constants.SHARED_PREFERENCES_VK_USER_TOKEN
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService
import ru.vladik.playlists.sqlite.PlatformRegistrationSqlHelper

object SharedPreferencesManager {
    fun saveVkUserToken(context: Context, token: String?) {
        saveVkUserTokenWithUser(context, token, null)
    }

    fun saveVkUserTokenWithUser(context: Context, token: String?, vkUser: VkUser?) {
        val preferences: SharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES_AUTH, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(SHARED_PREFERENCES_VK_USER_TOKEN, token)
        if (vkUser != null) {
            editor.putStringSet(SHARED_PREFERENCES_VK_USER, vkUser.toStringSet())
        }
        editor.apply()
    }

    fun deleteVlUserTokenWithUser(context: Context) {
        val preferences: SharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES_AUTH, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        with(editor) {
            remove(SHARED_PREFERENCES_VK_USER)
            remove(SHARED_PREFERENCES_VK_USER_TOKEN)
            apply()
        }
    }

    fun getVkUserToken(context: Context): String? {
        val preferences =
            context.getSharedPreferences(SHARED_PREFERENCES_AUTH, Context.MODE_PRIVATE)
        return preferences.getString(SHARED_PREFERENCES_VK_USER_TOKEN, null)
    }

    fun getVkUser(context: Context): VkUser? {
        val preferences =
            context.getSharedPreferences(SHARED_PREFERENCES_AUTH, Context.MODE_PRIVATE)
        val set = preferences.getStringSet(SHARED_PREFERENCES_VK_USER, null)
        return if (set != null) {
            VkUser(set)
        } else {
            null
        }
    }
}