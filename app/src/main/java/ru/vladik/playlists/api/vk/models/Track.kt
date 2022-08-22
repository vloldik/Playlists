package ru.vladik.playlists.api.vk.models

import android.content.Context
import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.vladik.playlists.api.vk.VkService
import ru.vladik.playlists.dataclasses.Photo
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService
import ru.vladik.playlists.dataclasses.tracks.TrackModel
import ru.vladik.playlists.extensions.ModelsExtensions.toPhoto
import ru.vladik.playlists.utils.AsyncUtils
import java.io.Serializable

data class Track(@SerializedName("id") override var id: String,
                 @SerializedName("artist") override var artist: String,
                 @SerializedName("owner_id") var ownerId: Long,
                 @SerializedName("title") override var title: String,
                 @SerializedName("duration") override var duration: Int,
                 @SerializedName("access_key") var accessKey: String,
                 var ads: Ads,
                 @SerializedName("is_explicit") var isExplicit: Boolean,
                 @SerializedName("is_focus_track") var isFocusTrack: Boolean,
                 @SerializedName("is_licensed") var isLicensed: Boolean,
                 @SerializedName("track_code") var trackCode: String,
                 @SerializedName("content_restricted") var contentRestricted: Int,
                 override var url: String?,
                 var date: Long,
                 var album: AlbumPreview?,
): TrackModel(), Serializable {

    override val photo: Photo?
        get() = album?.thumb?.toPhoto()

    override val service: MusicService
        get() = VkMusicService

    override val isEnabled: Boolean
        get() = contentRestricted != 1
}