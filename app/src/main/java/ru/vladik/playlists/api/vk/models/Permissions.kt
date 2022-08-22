package ru.vladik.playlists.api.vk.models

import java.io.Serializable

data class Permissions(
    var play: Boolean,
    var share: Boolean,
    var edit: Boolean,
    var follow: Boolean,
    var delete: Boolean,
    var boom_download: Boolean,
) : Serializable