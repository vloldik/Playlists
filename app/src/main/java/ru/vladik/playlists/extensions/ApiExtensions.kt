package ru.vladik.playlists.extensions

import ru.vladik.playlists.api.vk.models.Album
import ru.vladik.playlists.api.vk.models.AlbumsPage
import ru.vladik.playlists.dataclasses.*
import ru.vladik.playlists.api.vk.models.Track as VkTrack
import ru.vladik.playlists.utils.AppServices

object ApiExtensions {
    fun Album.toPlaylist(): BasicPlaylist {
        val photo =
            if (this.photo != null) {
                Photo(this.photo!!.photo135Link, this.photo!!.photo300Link, this.photo!!.photo1200Link)
            } else {
                null
            }
        return BasicPlaylist(this.id.toString(), this.count.toString(), this.title, this.description, photo, AppServices.vk)
    }

    fun AlbumsPage.toPlaylistList(): ArrayList<BasicPlaylist> {
        val list = ArrayList<BasicPlaylist>()
        for (album in this.items) {
            list.add(album.toPlaylist())
        }
        return list
    }

    fun VkTrack.toTrack(): TrackModel {
        val photo =
            if (album!= null && album!!.thumb != null) {
                Photo(
                    this.album!!.thumb!!.photo135Link,
                    this.album!!.thumb!!.photo300Link,
                    this.album!!.thumb!!.photo1200Link
                )
            } else {
                null
            }
        return BasicTrack(tId.toString(), tName, tAuthor, tDuration, photo)
    }

    fun VkTrack.toPlayableTrack(): PlayableTrack {
        return PlayableTrack(tId, tName, tAuthor, tDuration, getPhoto(), url)
    }

    fun MutableList<VkTrack>.toPlayableTrackList(): ArrayList<PlayableTrack> {
        val tracks: ArrayList<PlayableTrack> = ArrayList()
        for (item in this) {
            tracks.add(item.toPlayableTrack())
        }
        return tracks
    }
}