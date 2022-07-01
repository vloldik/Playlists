package ru.vladik.playlists.utils

import android.content.Context
import android.content.Intent
import ru.vladik.playlists.R
import ru.vladik.playlists.activities.VkAuthActivity
import ru.vladik.playlists.api.vk.VkApi
import ru.vladik.playlists.constants.Strings
import ru.vladik.playlists.dataclasses.MusicService

object MusicServicesUtil {
    private val userChangedListeners: HashSet<UserChangedListener> = HashSet()

    interface UserChangedListener {
        fun onUserChanged(service: MusicService)
    }

    fun addUserChangedListener(listener: UserChangedListener) {
        userChangedListeners.add(listener)
    }

    fun removeUserChangedListener(listener: UserChangedListener) {
        userChangedListeners.remove(listener)
    }

    fun notifyUserChanged(service: MusicService) {
        refreshServicesVariables()
        for (listener in userChangedListeners) {
            listener.onUserChanged(service)
        }
    }

    fun initServicesVariables() {
        AppServices.lastFm = MusicService(Strings.LAST_FM_MUSIC_SERVICE_NAME,
            true, R.drawable.lastfm_logo,
        )

        AppServices.vk = MusicService(Strings.VK_MUSIC_SERVICE_NAME,
            Constants.vkUser != null, R.drawable.vk_logo,
            userName = Constants.vkUser?.firstName, )
    }

    fun refreshServicesVariables() {
        AppServices.vk.loggedIn = Constants.vkUser != null
        AppServices.vk.userName = Constants.vkUser?.firstName
    }

    fun logIn(service: MusicService, context: Context, token: String, saveToken: Boolean = false) {
        when(service) {
            AppServices.vk -> vkLogIn(context, token, saveToken)
        }
    }

    fun logOut(service: MusicService, context: Context) {
        when(service) {
            AppServices.vk -> vkLogOut(context)
        }
    }

    private fun vkLogIn(context: Context, token: String, saveToken: Boolean = false) {
        val vkService = VkApi(token).getService()
        Constants.vkService = vkService
        Constants.vkUser = vkService.getUser().execute().body().response[0]
        if (saveToken) SharedPreferences.saveVkUserToken(context, token)
        refreshServicesVariables()
    }

    private fun vkLogOut(context: Context) {
        Constants.vkService = null
        Constants.vkUser = null
        SharedPreferences.saveVkUserToken(context, null)
        refreshServicesVariables()
    }

    fun startAuthActivity(service: MusicService, context: Context) {
        var intent: Intent? = null
        when(service) {
            AppServices.vk -> intent = Intent(context, VkAuthActivity::class.java)
        }
        checkNotNull(intent)
        context.startActivity(intent)
    }
}