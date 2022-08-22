package ru.vladik.playlists.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.vladik.playlists.activities.VkAuthActivity
import ru.vladik.playlists.api.vk.VkApi
import ru.vladik.playlists.api.vk.VkService
import ru.vladik.playlists.api.vk.models.UserResponse
import ru.vladik.playlists.constants.VK_MUSIC_SERVICE_NAME
import ru.vladik.playlists.dataclasses.PlatformUserRegistrationInfo
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService
import ru.vladik.playlists.sqlite.PlatformRegistrationSqlHelper

object LoginUtil {
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

    private fun notifyUserChanged(service: MusicService) {
        userChangedListeners.forEach { listener ->
            listener.onUserChanged(service)
        }
    }

    fun logOut(service: MusicService, context: Context) {
        when(service) {
            VkMusicService -> vkLogOut(context)
        }
    }

    fun logIn(service: MusicService, context: Context, token: String, saveToken: Boolean) {
        when(service) {
            VkMusicService -> vkLogIn(context, token, saveToken)
        }
    }

    fun vkLogIn(context: Context, token: String, saveToken: Boolean = false) {
        val vkService = VkApi(token).getService()
        vkService.getUser().enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>?, response: Response<UserResponse>) {
                val user = response.body().response[0]
                val info = PlatformUserRegistrationInfo(VkMusicService.id, user.id.toString(), user.firstName, token, true)
                if (saveToken) PlatformRegistrationSqlHelper(context).saveRegistration(info)
                notifyUserChanged(VkMusicService)
            }

            override fun onFailure(call: Call<UserResponse>?, t: Throwable?) {
                Toast.makeText(context, "Ошибка входа", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun vkLogOut(context: Context) {
        PlatformRegistrationSqlHelper(context).unregister(VkMusicService)
        notifyUserChanged(VkMusicService)
    }

    fun startAuthActivity(service: MusicService, context: Context) {
        var intent: Intent? = null
        when(service) {
            VkMusicService -> intent = Intent(context, VkAuthActivity::class.java)
        }
        checkNotNull(intent)
        context.startActivity(intent)
    }
}