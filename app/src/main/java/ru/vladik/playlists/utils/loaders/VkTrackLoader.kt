package ru.vladik.playlists.utils.loaders

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.vladik.playlists.api.vk.VkService
import ru.vladik.playlists.api.vk.getAllAudios
import ru.vladik.playlists.api.vk.models.Track
import ru.vladik.playlists.api.vk.models.TracksResponse
import ru.vladik.playlists.dataclasses.tracks.TrackModel

open class VkTrackLoader(
    context: Context, private val playlistId: String,
    private val count: Int = TrackLoader.DEFAULT_COUNT, private val ownerId: String
) : TrackLoader {
    private val vkService = VkService.getInstance(context)
    private val offset: Int
        get() {
            return currentPage * count
        }
    override var hasNext = true
    override var currentPage = 0

    override fun nextPage(onResponse: (MutableList<out TrackModel>) -> Unit) {
        loadPage(currentPage++, onResponse)
    }

    override fun prevPage(onResponse: (MutableList<out TrackModel>) -> Unit) {
        loadPage(--currentPage, onResponse)
    }

    override fun loadAll(onResponse: (MutableList<out TrackModel>) -> Unit) {
        vkService?.getAllAudios(albumId = playlistId)?.enqueue(LoadTrackResponseCallback(onResponse))
    }

    override fun loadPage(page: Int, onResponse: (MutableList<out TrackModel>) -> Unit) {
        vkService
            ?.getTracks(
                albumId = playlistId,
                count = count,
                offset = count * page,
                ownerId = ownerId
            )
            ?.enqueue(LoadTrackResponseCallback(onResponse))
    }

    private inner class LoadTrackResponseCallback(private val onResponse: (MutableList<out TrackModel>) -> Unit) :
        Callback<TracksResponse> {

        override fun onResponse(call: Call<TracksResponse>?, response: Response<TracksResponse>?) {
            val tracks: MutableList<Track> = ArrayList()
            response?.body()?.response?.also { page ->
                hasNext = page.count > offset
                page.items.map {
                    tracks.add(it)
                }
            }
            onResponse(tracks)
        }

        override fun onFailure(call: Call<TracksResponse>?, t: Throwable?) {
        }

    }
}