package ru.vladik.playlists.services

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import androidx.media.session.MediaButtonReceiver

open class VMediaButtonReciever : MediaButtonReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

        Log.d("main", keyEvent.toString())
        if (keyEvent != null) {
            intent.component = ComponentName(context, VladikMusicPlayService::class.java)
            PendingIntent.getService(context, keyEvent.keyCode, intent, 0).send()
        }
    }
}