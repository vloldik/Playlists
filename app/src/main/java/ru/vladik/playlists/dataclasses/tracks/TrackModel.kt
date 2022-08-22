package ru.vladik.playlists.dataclasses.tracks

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.MimeTypes
import ru.vladik.playlists.dataclasses.Photo
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import java.io.Serializable

abstract class TrackModel : Serializable {

    abstract var id: String
    abstract var title: String
    abstract var artist: String
    abstract var duration: Int
    abstract var url: String?

    abstract val isEnabled: Boolean
    abstract val photo: Photo?
    abstract val service: MusicService

    fun toMediaSource(dataSourceFactory: DataSource.Factory): ProgressiveMediaSource {
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
            MediaItem.fromUri(url!!)
        )
    }

    fun toMediaMetadataCompat() : MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, url)
            .putText(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, artist)
            .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist)
            .putText(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putText(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, photo?.large)
            .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, photo?.large)
            .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
            .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toLong())
            .build()
    }

    fun toBrowserMediaItem() : MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaUri(Uri.parse(url))
            .setTitle(title)
            .setSubtitle(artist)
            .setDescription("")
            .setIconUri(photo?.large?.toUri())
            .setMediaId(id)
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    fun toMediaItem() : MediaItem {
        return MediaItem.Builder()
            .setUri(url)
            .setMediaId(id)
            .setMediaMetadata(with(MediaMetadata.Builder()) {
                setTitle(title)
                setDisplayTitle(title)
                setArtist(artist)
                setAlbumArtist(artist)
                setIsPlayable(true)
                setComposer(artist)
                setWriter(artist)
                if (photo != null) {
                    setArtworkUri(photo?.large?.toUri())
                } else {
                    setArtworkUri(null)
                }
                val extras = Bundle()
                extras.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toLong())
                setExtras(extras)
            }.build())
            .build()
    }
}