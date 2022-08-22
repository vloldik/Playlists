package ru.vladik.playlists.utils.vk

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.vladik.playlists.api.vk.VkService
import ru.vladik.playlists.api.vk.models.Track
import ru.vladik.playlists.api.vk.models.TrackListResponse

class VkUtils {


    interface OnUrlListener {
        fun onUrl(url: String)
    }
}