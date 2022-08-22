package ru.vladik.playlists.dataclasses

data class PlatformUserRegistrationInfo(
    val musicPlatformId: Long?,
    val userId: String?,
    val username: String?,
    val token: String?,
    val loggedIn: Boolean
)