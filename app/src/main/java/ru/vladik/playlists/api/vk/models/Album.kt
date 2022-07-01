package ru.vladik.playlists.api.vk.models

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Album(var id: Long, var title: String, @SerializedName("owner_id") var ownerId: Long,
                 var type: Int, var description: String, var count: Int, var followers: Int,
                 var plays: Int,
                 @SerializedName("create_time") var createTime: Long,
                 @SerializedName("update_time") var updateTime: Long,
                 var genres: MutableList<Genre>, var photo: Photo?,
                 @SerializedName("is_following") var isFollowing: Boolean,
                 @SerializedName("access_key") var accessKey: String,
                 @SerializedName("album_type") var albumType: String,
): Serializable