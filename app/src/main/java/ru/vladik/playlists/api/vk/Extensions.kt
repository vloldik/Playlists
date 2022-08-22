package ru.vladik.playlists.api.vk

import android.util.Log
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.vladik.playlists.api.vk.models.AlbumsResponse
import ru.vladik.playlists.api.vk.models.Track
import ru.vladik.playlists.api.vk.models.TrackListResponse
import ru.vladik.playlists.api.vk.models.TracksResponse

fun VkService.getAllAudios(ownerId: String? = null, albumId: String? = null): Call<TracksResponse> {
    val code = StringBuilder()
    code.append("return ")
    fun getAudioCode(count: String) = StringBuilder().apply {
        append("API.audio.get({")
        append("owner_id:")
        if (ownerId == null) append("API.users.get()[0].id") else append(ownerId)
        append(",")
        if (albumId != null) append("album_id: $albumId,")
        append("count: $count")
        append("})")
    }
    code.append(getAudioCode(getAudioCode("1").append(".count").toString()))
    code.append(";")
    return executeForTrackListResponse(code.toString())
}

fun VkService.getUserAlbums(count: Int = 0, offset: Int = 0) : Call<AlbumsResponse> {
    val code = "return API.audio.getPlaylists({owner_id:API.users.get()[0].id, count:$count, offset:$offset});"
    return executeForAlbumListResponse(code)
}