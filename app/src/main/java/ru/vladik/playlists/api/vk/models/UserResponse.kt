package ru.vladik.playlists.api.vk.models

import java.io.Serializable

data class UserResponse(var response: MutableList<User>, var error: ApiError): Serializable