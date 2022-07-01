package ru.vladik.playlists.api.vk.models

import com.google.gson.annotations.SerializedName

data class ApiError(
    @SerializedName("error_code") var errorCode: Int,
    @SerializedName("error_message") var errorMessage: String,
)