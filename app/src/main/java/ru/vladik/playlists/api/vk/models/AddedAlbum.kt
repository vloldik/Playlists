package ru.vladik.playlists.api.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddedAlbum(@SerializedName("album_id") var id: Long): Serializable
