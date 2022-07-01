package ru.vladik.playlists.api.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(var id: Long,
                @SerializedName("first_name") var firstName: String,
                @SerializedName("last_name") var lastName: String,
                @SerializedName("can_access_closed") var canAccessClosed: Boolean,
                @SerializedName("is_closed") var isClosed: Boolean): Serializable