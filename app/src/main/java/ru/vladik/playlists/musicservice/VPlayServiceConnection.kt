package ru.vladik.playlists.musicservice

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat

import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayer
import ru.vladik.playlists.dataclasses.tracks.TrackModel

class VPlayServiceConnection(context: Context) {

    companion object {
        @Volatile
        private var instance: VPlayServiceConnection? = null

        fun getInstance(context: Context) : VPlayServiceConnection {
            synchronized(this) {
                if (instance?.player == null) {
                    return VPlayServiceConnection(context)
                }
                return instance!!
            }
        }

    }

    val initialized: Boolean
        get() {
            return vMusicPlayService != null
        }

    private var listeners = ArrayList<((VPlayServiceConnection) -> Unit)>()

    private val mediaBrowser: MediaBrowserCompat

    private var vMusicPlayService: VMusicPlayService? = null
    val player: ExoPlayer?
        get() {return vMusicPlayService?.player}

    fun registerInitListenerForOneTime(listener: (VPlayServiceConnection) -> Unit) {
        if (initialized) {
            listener(this)
            return
        } else {
            listeners.add(listener)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            vMusicPlayService = (service as VMusicPlayService.LocalBinder).getService()
            listeners.forEach {
                it(this@VPlayServiceConnection)
            }
            listeners.clear()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }

    }

    init {
        Intent(context, VMusicPlayService::class.java).apply {
            action = VMusicPlayService.ACTION_BIND
            context.bindService(this, serviceConnection, 0)
        }
        mediaBrowser = MediaBrowserCompat(
            context,
            ComponentName(context, VMusicPlayService::class.java),
            VConnectionCallback(context),
            null
        )
        mediaBrowser.connect()
    }

    private inner class VConnectionCallback(private val context: Context) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            listeners.forEach {
                it(this@VPlayServiceConnection)
                listeners.remove(it)
            }
            super.onConnected()
        }
    }

    fun playTrackById(tracks: MutableList<out TrackModel>, mediaId: String) {
        vMusicPlayService?.prepareAndPlay(tracks.find { it.id == mediaId }, true, tracks)
    }
}
