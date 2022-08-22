package ru.vladik.playlists.utils.loaders

import android.content.Context
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService
import ru.vladik.playlists.dataclasses.tracks.TrackModel

interface TrackLoader {
    var hasNext: Boolean
    val currentPage: Int

    fun nextPage(onResponse: (MutableList<out TrackModel>) -> Unit)
    fun prevPage(onResponse: (MutableList<out TrackModel>) -> Unit)
    fun loadAll(onResponse: (MutableList<out TrackModel>) -> Unit)
    fun loadPage(page: Int, onResponse: (MutableList<out TrackModel>) -> Unit)

    class Builder(val context: Context) {

        private var service: MusicService? = null
        private var playlistId: String? = null
        private var count: Int? = null
        private var initCount: Int? = null
        private var ownerId: String? = null

        fun setService(service: MusicService) {
            this.service = service
        }

        fun setCount(count: Int) {
            this.count = count
        }

        fun setInitCount(initCount: Int?) {
            this.initCount = initCount
        }

        fun setPlaylistId(id: String) {
            this.playlistId = id
        }

        fun setOwnerId(ownerId: String) {
            this.ownerId = ownerId
        }

        fun build(): TrackLoader {
            when (service) {
                VkMusicService -> {
                    if (playlistId == null) throw IllegalStateException("id cannot be null")
                    if (ownerId == null) throw IllegalStateException("owner id cannot be null for vk service")
                    if (count != null) {
                        return VkTrackLoader(context, playlistId!!, count!!, ownerId!!)
                    }
                    return VkTrackLoader(
                        context,
                        playlistId!!,
                        ownerId = ownerId!!
                    )
                }
                else -> throw IllegalStateException("service cannot be $service")
            }
        }
    }

    companion object {
        const val DEFAULT_COUNT = 0
    }
}