package ru.vladik.playlists.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.*
import android.util.Log
import android.view.KeyEvent
import java.util.function.LongFunction

class VladikMusicPlayService : Service() {

    val player: VladikMediaPlayer = VladikMediaPlayer()
    private lateinit var notificationManager: VladikNotificationManager
    private val musicBind = ServiceBinder()

    private val mNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (player.isPlaying) {
                player.pause()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = VladikNotificationManager(this)
        initMusicPlayer()
        initNoisyReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val keyEvent = intent?.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        Log.d("main", keyEvent.toString())
        if (keyEvent != null) {
            handleMediaButtonIntent(keyEvent.keyCode)
        }
        return START_STICKY
    }

    private fun handleMediaButtonIntent(code: Number) {

        when(code) {
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                player.pause()
            }
            KeyEvent.KEYCODE_MEDIA_STOP -> {
                player.sleep()
            }
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                player.skipToPreviousTrack()
            }
            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                player.skipToNextTrack()
            }
            2097152 -> {
                player.isShuffle = !player.isShuffle
            }
        }
    }

    private fun initMusicPlayer() {
        player.setOnPreparedListener {
            player.start()
            notificationManager.showNotificationForTrack(player.currentTrack!!, false, player.isShuffle)
        }
        player.setOnCompletionListener {
            player.skipToNextTrack()
        }
        player.onPauseListener = object : VladikPlayer.OnPauseListener {
            override fun onPause(pause: Boolean) {
                notificationManager.showNotificationForTrack(player.currentTrack!!, pause, player.isShuffle)
            }
        }
        player.onSleepListener = object : VladikPlayer.OnSleepListener {
            override fun onSleep() {
                notificationManager.hideNotification()
            }
        }
    }


    private fun initNoisyReceiver() {
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(mNoisyReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mNoisyReceiver)
    }

    override fun onBind(intent: Intent): IBinder {
        return musicBind
    }

    override fun onUnbind(intent: Intent?): Boolean {
        player.stop()
        return false
    }

    inner class ServiceBinder : Binder() {
        fun getService(): VladikMusicPlayService {
            return this@VladikMusicPlayService
        }
    }
}