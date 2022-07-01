package ru.vladik.playlists.api.vk

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.vladik.playlists.api.vk.models.*
import ru.vladik.playlists.dataclasses.TrackModel

interface VkService {
    @GET("audio.createPlaylist?v=5.133")
    fun addAlbum(@Query("owner_id") ownerId: Long, @Query("title") title: String,
                 @Query("description") description: String? = null) : Call<AddedAlbumResponse>

    @GET("audio.getPlaylists?v=5.133")
    fun getAlbums(@Query("owner_id") ownerId: Long,
                  @Query("offset") offset: Int? = null,
                  @Query("count") count: Int? = 30) : Call<AlbumsResponse>

    @GET("audio.get?v=5.133")
    fun getTracks(@Query("owner_id") ownerId: Long? = null,
                  @Query("album_id") albumId: Long? = null,
                  @Query("offset") offset: Int? = null,
                  @Query("count") count: Int? = null) : Call<TracksResponse>

    @GET("audio.addToPlaylist?v=5.133")
    fun addToPlayList(@Query("owner_id") playlistOwnerId: Long,
                      @Query("playlist_id") playlistId: Long,
                      @Query("audio_ids") audioIds: String) : Call<AddedTracksResponse>

    @GET("users.get?v=5.133")
    fun getUser() : Call<UserResponse>

    @GET("execute?v=5.133")
    fun executeForTrackListResponse(@Query("code") code: String) : Call<TrackListResponse>
}