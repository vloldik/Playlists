package ru.vladik.playlists.api.vk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Ads(@SerializedName("content_id") var contentId: String, var duration: String,
               @SerializedName("account_age_type") var accountAgeType: String,
               @SerializedName("puid22") var puId22: String,): Serializable