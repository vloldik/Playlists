package ru.vladik.playlists.api.playlists

import ru.vladik.playlists.constants.Strings

data class User(val id: String = Strings.DEFAULT_STRING, var mail: String = Strings.DEFAULT_STRING,
                var photoUrl: String = Strings.DEFAULT_STRING, var name: String = Strings.DEFAULT_STRING,
                var description: String = Strings.DEFAULT_STRING, var publishedPlaylistsCount: Int = 0)

data class PlaylistPreview(val id: Long = -1, var name: String = Strings.DEFAULT_STRING,
                        var description: String = Strings.DEFAULT_STRING, val likes: Likes = Likes()
)

data class Playlist(val playlistPreview: PlaylistPreview)

data class Likes(val id: Long = -1, var likesCount: Int = 0, var activeUsersCount: Int = 0,
                 var viewsCount: Int = 0)

data class Track(val id: Long, var name: String)


