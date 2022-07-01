package ru.vladik.playlists.api.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Photo(var width: Int, var height: Int,
                 @SerializedName("photo_34") var photo34Link: String,
                 @SerializedName("photo_68") var photo68Link: String,
                 @SerializedName("photo_135") var photo135Link: String,
                 @SerializedName("photo_270") var photo270Link: String,
                 @SerializedName("photo_300") var photo300Link: String,
                 @SerializedName("photo_600") var photo600Link: String,
                 @SerializedName("photo_1200") var photo1200Link: String): Serializable