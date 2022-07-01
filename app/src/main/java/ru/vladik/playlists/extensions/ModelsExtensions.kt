package ru.vladik.playlists.extensions

import ru.vladik.playlists.dataclasses.Photo
import ru.vladik.playlists.api.vk.models.Photo as VkPhoto

object ModelsExtensions {
    fun VkPhoto.toPhoto() : Photo {
        return Photo(photo300Link, photo600Link, photo1200Link)
    }
}