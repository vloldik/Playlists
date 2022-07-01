package ru.vladik.playlists.api.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class AlbumPreview(var id: Long, var title: String, @SerializedName("owner_id") var ownerId: Long,
                        @SerializedName("access_key") var accessKey: String, val thumb: Photo?):
    Serializable