package ru.vladik.playlists.api.vk.models

import com.google.gson.annotations.SerializedName
import ru.vladik.playlists.dataclasses.MusicService
import ru.vladik.playlists.extensions.ModelsExtensions.toPhoto
import ru.vladik.playlists.utils.AppServices
import java.io.Serializable

data class Track(@SerializedName("id") override var tId: String,
                 @SerializedName("artist") override var tAuthor: String,
                 @SerializedName("owner_id") var ownerId: Long,
                 @SerializedName("title") override var tName: String,
                 @SerializedName("duration") override var tDuration: Int,
                 @SerializedName("access_key") var accessKey: String,
                 var ads: Ads,
                 @SerializedName("is_explicit") var isExplicit: Boolean,
                 @SerializedName("is_focus_track") var isFocusTrack: Boolean,
                 @SerializedName("is_licensed") var isLicensed: Boolean,
                 @SerializedName("track_code") var trackCode: String,
                 @SerializedName("content_restricted") var contentRestricted: Int,
                 var url: String,
                 var date: Long,
                 var album: AlbumPreview?,
): ru.vladik.playlists.dataclasses.TrackModel(tId, tName, tAuthor, tDuration), Serializable {
    override fun getService(): MusicService {
        return AppServices.vk
    }

    override fun getPhoto(): ru.vladik.playlists.dataclasses.Photo? {
        return album?.thumb?.toPhoto()
    }
}