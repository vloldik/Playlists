package ru.vladik.playlists.dataclasses

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import ru.vladik.playlists.dataclasses.tracks.TrackModel

class MusicSource(var musicList: ArrayList<TrackModel> = ArrayList()) {

    fun toMediaData(): List<MediaMetadataCompat> {
        return musicList.map {
            it.toMediaMetadataCompat()
        }
    }

    fun toMediaSource(dataSourceFactory: DataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        musicList.forEach {
            val mediaSource = it.toMediaSource(dataSourceFactory)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun toBrowserMediaItems(): MutableList<MediaBrowserCompat.MediaItem> {
        return musicList.map {
            it.toBrowserMediaItem()
        }.toMutableList()
    }

    fun toMediaItems(): MutableList<MediaItem> {
        return musicList.map {
            it.toMediaItem()
        }.toMutableList()
    }
}