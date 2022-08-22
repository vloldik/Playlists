package ru.vladik.playlists.musicservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.*
import ru.vladik.playlists.R
import ru.vladik.playlists.activities.TrackActivity

const val NOTIFICATION_ID = 10
const val NOTIFICATION_CHANNEL_ID = "\"VLADIK_MUSIC\""

class VladikNotificationManager(private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener, ) {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val notificationManager: PlayerNotificationManager

    init {
        val appNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.media_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        appNotificationManager.createNotificationChannel(notificationChannel)

        val builder =
            PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
        with(builder) {
            setMediaDescriptionAdapter(DescriptionAdapter())
            setNotificationListener(notificationListener)
            setChannelNameResourceId(R.string.media_notification_channel_name)
            setChannelDescriptionResourceId(R.string.media_notification_channel_description)
            setCustomActionReceiver(VladikCustomActions())
            setChannelImportance(IMPORTANCE_HIGH)
        }
        notificationManager = builder.build()
        with(notificationManager) {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.track_without_bg)
            setUseRewindAction(false)
            setUseFastForwardAction(false)
        }
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    fun showNotificationForPlayer(player: Player) {
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null
        val defaultBitmap = AppCompatResources.getDrawable(context, R.drawable.track)!!.toBitmap()

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return PendingIntent.getActivity(context, 0,
                Intent(context, TrackActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getCurrentContentText(player: Player) =
            player.currentMediaItem?.mediaMetadata?.artist.toString()


        override fun getCurrentContentTitle(player: Player) =
            player.currentMediaItem?.mediaMetadata?.title.toString()


        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = player.currentMediaItem?.mediaMetadata?.artworkUri


            return if (currentIconUri != iconUri || currentBitmap == null) {

                // Cache the bitmap for the current song so that successive calls to
                // `getCurrentLargeIcon` don't cause the bitmap to be recreated.
                currentIconUri = iconUri
                serviceScope.launch {
                    currentBitmap = iconUri?.let {
                        resolveUriAsBitmap(it)
                    }
                    currentBitmap?.let { callback.onBitmap(it) }
                }
                defaultBitmap
            } else {
                currentBitmap
            }
        }

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                Glide.with(context).applyDefaultRequestOptions(glideOptions)
                    .asBitmap()
                    .load(uri)
                    .submit()
                    .get()
            }
        }
    }

    private inner class VladikCustomActions : PlayerNotificationManager.CustomActionReceiver {
        override fun createCustomActions(
            context: Context,
            instanceId: Int
        ): MutableMap<String, NotificationCompat.Action> {
            val action = NotificationCompat.Action(
                R.drawable.close, "close", PendingIntent.getService(
                    context,
                    KeyEvent.KEYCODE_MEDIA_STOP,
                    Intent(context, VMusicPlayService::class.java).apply {
                        action = Intent.ACTION_MEDIA_BUTTON
                        putExtra(
                            Intent.EXTRA_KEY_EVENT, KeyEvent(
                                KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_MEDIA_STOP
                            )
                        )
                    }, 0
                )
            )
            return mapOf(Pair("close", action))
                    as MutableMap<String, NotificationCompat.Action>
        }

        override fun getCustomActions(player: Player): MutableList<String> {
            return listOf("close") as MutableList<String>
        }

        override fun onCustomAction(player: Player, action: String, intent: Intent) {
        }

    }

}

    private val glideOptions = RequestOptions()
        .fallback(R.drawable.track)
        .diskCacheStrategy(DiskCacheStrategy.DATA)