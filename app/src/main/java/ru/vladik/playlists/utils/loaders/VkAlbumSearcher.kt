package ru.vladik.playlists.utils.loaders

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.vladik.playlists.api.vk.VkService
import ru.vladik.playlists.api.vk.models.AlbumsResponse
import ru.vladik.playlists.dataclasses.albums.AlbumModel

class VkAlbumSearcher(
    context: Context,
    override var count: Int = AlbumLoader.DEFAULT_COUNT,
) : AlbumSearcher {

    override var q = ""
        set(value) {
            field = value

            resetPage()
        }
    override var isLoading = false
    override var hasNext = false

    private val vkService: VkService? = VkService.getInstance(context)
    private var currentRequest = vkService?.searchAlbums(q = q, offset = 0)

    override var callback: ((MutableList<out AlbumModel>) -> Unit)? = null
    override var currentPage = 0

    private fun resetPage() {
        currentPage = 0
    }

    override fun nextPage() {
        if (callback != null)
            loadPage(currentPage++, callback!!)
    }

    override fun prevPage() {
        if (callback != null)
            loadPage(--currentPage, callback!!)
    }

    override fun loadPage(page: Int, onResponse: (MutableList<out AlbumModel>) -> Unit) {
        currentRequest?.cancel()

        currentRequest = vkService
            ?.searchAlbums(q = q, count = count, offset = count * page)

        currentRequest?.enqueue(LoadTrackResponseCallback(callback))
    }

    private inner class LoadTrackResponseCallback(private val onResponse: ((MutableList<out AlbumModel>) -> Unit)?) :
        Callback<AlbumsResponse> {

        override fun onResponse(call: Call<AlbumsResponse>?, response: Response<AlbumsResponse>?) {
            response?.body()?.response?.also { page ->
                onResponse?.invoke(page.items)
            }
        }

        override fun onFailure(call: Call<AlbumsResponse>?, t: Throwable?) {
        }

    }
}