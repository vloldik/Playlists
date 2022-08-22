package ru.vladik.playlists.utils.loaders

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.vladik.playlists.api.vk.VkService
import ru.vladik.playlists.api.vk.getUserAlbums
import ru.vladik.playlists.api.vk.models.Album
import ru.vladik.playlists.api.vk.models.AlbumsResponse
import ru.vladik.playlists.api.vk.models.Track
import ru.vladik.playlists.api.vk.models.TracksResponse
import ru.vladik.playlists.dataclasses.albums.AlbumModel
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService
import ru.vladik.playlists.dataclasses.tracks.TrackModel
import ru.vladik.playlists.sqlite.PlatformRegistrationSqlHelper
import ru.vladik.playlists.utils.loaders.TrackLoader.Companion.DEFAULT_COUNT

class VkAlbumLoader(
    context: Context, ownerId: String? = null, override var count: Int = AlbumLoader.DEFAULT_COUNT, ) : AlbumLoader {

    override var currentPage = 0
    override var isLoading = false
    override var hasNext = true
    override var callback: ((MutableList<out AlbumModel>) -> Unit)? = null

    private val ownerId: String = (ownerId ?: PlatformRegistrationSqlHelper(context)
        .getServiceRegistrationInfo(VkMusicService)?.userId).toString()
    private val vkService: VkService? = VkService.getInstance(context)

    private var currentRequest: Call<AlbumsResponse>? = null


    override fun nextPage() {
        callback?.let {
            loadPage(currentPage++, it)
        }
    }

    override fun prevPage() {
        callback?.let {
            loadPage(--currentPage, it)
        }
    }

    override fun loadPage(page: Int, onResponse: (MutableList<out AlbumModel>) -> Unit) {
        currentRequest?.cancel()
        currentRequest = vkService?.getUserAlbums(count = count, offset = count * page)
        currentRequest?.enqueue(LoadTrackResponseCallback(onResponse))
        isLoading = true
    }

    private inner class LoadTrackResponseCallback(private val onResponse: (MutableList<out AlbumModel>) -> Unit) :
        Callback<AlbumsResponse> {

        override fun onResponse(call: Call<AlbumsResponse>?, response: Response<AlbumsResponse>?) {
            isLoading = false
            response?.body()?.response?.let { page ->
                onResponse(page.items)
                hasNext = !(currentPage*count >= page.items.size || page.items.size >= page.count)
                return
            }
            hasNext = false
        }

        override fun onFailure(call: Call<AlbumsResponse>?, t: Throwable?) {
            isLoading = false
        }

    }
}