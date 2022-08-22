package ru.vladik.playlists.api.vk.models

import com.google.gson.annotations.SerializedName
import ru.vladik.playlists.dataclasses.Photo
import ru.vladik.playlists.dataclasses.albums.AlbumModel
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService
import ru.vladik.playlists.extensions.ModelsExtensions.toPhoto
import java.io.Serializable
import ru.vladik.playlists.api.vk.models.Photo as VkPhoto

data class Album(override var id: String,
                 override var title: String,
                 @SerializedName("owner_id") override var ownerId: String,
                 var type: Int,
                 override var description: String,
                 override var count: Int,
                 var followers: Int,
                 var plays: Int,
                 @SerializedName("create_time") var createTime: Long,
                 @SerializedName("update_time") var updateTime: Long,
                 var genres: MutableList<Genre>,
                 @SerializedName("photo") var vkPhoto: VkPhoto?,
                 @SerializedName("is_following") var isFollowing: Boolean,
                 @SerializedName("access_key") var accessKey: String,
                 @SerializedName("album_type") var albumType: String,
                 var permissions: Permissions
): Serializable, AlbumModel() {

    override val photo: Photo?
        get() = vkPhoto?.toPhoto()
    override val service: MusicService
        get() = VkMusicService
    override val isEnabled: Boolean
        get() = permissions.play
}

