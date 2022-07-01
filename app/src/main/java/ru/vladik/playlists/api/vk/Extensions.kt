package ru.vladik.playlists.api.vk

import android.util.Log
import retrofit2.Call
import ru.vladik.playlists.api.vk.models.Track
import ru.vladik.playlists.api.vk.models.TrackListResponse
import ru.vladik.playlists.api.vk.models.TracksResponse

fun VkService.getAudioWithUrl(track: Track): Call<TrackListResponse> {
    val ids = "${track.ownerId}_${track.tId}"
    val code = "return API.audio.getById({\"audios\": \"$ids\"});"
    return executeForTrackListResponse(code)
}

fun VkService.getAudiosWithUrl(tracks: MutableList<Track>): Call<TrackListResponse> {
    val ids = StringBuilder()
    for (item in tracks) {
        if (item.contentRestricted != 1) {
            ids.append("${item.ownerId}_${item.tId},")
        }
    }
    val code = "return API.audio.getById({\"audios\": \"[0,$ids]\"});"
    Log.d("main", code)
    return executeForTrackListResponse(code)
}