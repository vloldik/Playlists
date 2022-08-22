package ru.vladik.playlists.api.vk

import android.content.Context
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.vladik.playlists.api.vk.models.*
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService
import ru.vladik.playlists.sqlite.PlatformRegistrationSqlHelper

interface VkService {
    @GET("audio.createPlaylist?v=5.133")
    fun addAlbum(@Query("owner_id") ownerId: Long, @Query("title") title: String,
                 @Query("description") description: String? = null) : Call<AddedAlbumResponse>

    @GET("audio.getPlaylists?v=5.133")
    fun getAlbums(@Query("owner_id") ownerId: String,
                  @Query("offset") offset: Int? = null,
                  @Query("count") count: Int? = 30) : Call<AlbumsResponse>

    @GET("audio.searchPlaylists?v=5.133")
    fun searchAlbums(@Query("q") q: String,
                  @Query("offset") offset: Int? = null,
                  @Query("count") count: Int? = 30) : Call<AlbumsResponse>

    @GET("audio.get?v=5.133")
    fun getTracks(@Query("owner_id") ownerId: String? = null,
                  @Query("album_id") albumId: String? = null,
                  @Query("offset") offset: Int? = null,
                  @Query("count") count: Int? = null) : Call<TracksResponse>

    @GET("audio.addToPlaylist?v=5.133")
    fun addToPlayList(@Query("owner_id") playlistOwnerId: Long,
                      @Query("playlist_id") playlistId: Long,
                      @Query("audio_ids") audioIds: String) : Call<AddedTracksResponse>

    @GET("users.get?v=5.133")
    fun getUser() : Call<UserResponse>

    @GET("execute?v=5.133")
    fun executeForTrackListResponse(@Query("code") code: String) : Call<TracksResponse>

    @GET("execute?v=5.133")
    fun executeForAlbumListResponse(@Query("code") code: String) : Call<AlbumsResponse>

    companion object {
        @Volatile
        private var instance: VkService? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                val token = PlatformRegistrationSqlHelper(context).getServiceRegistrationInfo(VkMusicService)?.token
                if (token != null) {
                    instance ?: VkApi(token).getService()
                        .also { instance = it }
                } else null
            }
    }
}