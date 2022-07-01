package ru.vladik.playlists.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import android.widget.RemoteViews
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ru.vladik.playlists.R
import ru.vladik.playlists.dataclasses.PlayableTrack
import ru.vladik.playlists.utils.ColorUtils

const val id = 0
const val NOTIFICATION_CHANNEL_ID = "\"VLADIK_MUSIC\""
const val NOTIFICATION_CHANNEL_NAME = "\"Vladik music\""
private lateinit var notificationBuilder: Notification.Builder

class VladikNotificationManager(val context: Context) {

    init {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun showNotification(notification: Notification) {
        with(NotificationManagerCompat.from(context)) {
            notify(id, notification)
        }
    }

    fun showNotificationForTrack(track: PlayableTrack, pause: Boolean, shuffle: Boolean) {
        val shuffleIntent = Intent(context, VladikMusicPlayService::class.java)

        shuffleIntent.action = Intent.ACTION_MEDIA_BUTTON
        shuffleIntent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN,
            PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE.toInt()))

        val shuffleIcon = Icon.createWithResource(context, R.drawable.shuffle)
        val pauseIcon = Icon.createWithResource(context, if (!pause) R.drawable.pause else R.drawable.play)
        val nextIcon = Icon.createWithResource(context, R.drawable.skip_next)
        val prevIcon = Icon.createWithResource(context, R.drawable.skip_previous)

        val shuffleAction = Notification.Action.Builder(shuffleIcon, "shuffle", PendingIntent.getService(
            context, PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE.toInt(), shuffleIntent, 0)).build()
        val defaultIcon = AppCompatResources.getDrawable(context, R.drawable.track)!!.toBitmap()
        notificationBuilder = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)

        val pauseAction = Notification.Action.Builder(pauseIcon,"play", MediaButtonReceiver.buildMediaButtonPendingIntent(
            context, PlaybackStateCompat.ACTION_PAUSE)).build()
        val nextAction = Notification.Action.Builder(nextIcon,"next", MediaButtonReceiver.buildMediaButtonPendingIntent(
            context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)).build()
        val prevAction = Notification.Action.Builder(prevIcon,"previous", MediaButtonReceiver.buildMediaButtonPendingIntent(
            context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)).build()

        notificationBuilder
            .setContentTitle(track.tName)
            .setContentText(track.tAuthor)
            .setColorized(true)
            .setSmallIcon(R.drawable.track_without_bg)
            .setLargeIcon(defaultIcon)
            .setShowWhen(false)
            .setColor(ColorUtils.getAverageColor(defaultIcon))
            .setStyle(Notification.MediaStyle().setShowActionsInCompactView(0, 1, 2))
            .addAction(prevAction)
            .addAction(pauseAction)
            .addAction(nextAction)
            .addAction(shuffleAction)


        val notification = notificationBuilder.build()
        notification.flags = Notification.FLAG_NO_CLEAR
        showNotification(notification)

        if (track.getPhoto() != null) {
            Glide.with(context)
                .asBitmap()
                .load(track.getPhoto()!!.medium)
                .transform(RoundedCornersTransformation(50, 0))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap,
                                                 transition: Transition<in Bitmap>?) {
                        val notificationNew = notificationBuilder.setLargeIcon(resource)
                            .setColor(ColorUtils.getAverageColor(resource))
                            .build()
                        notificationNew.flags = Notification.FLAG_NO_CLEAR

                        showNotification(notificationNew)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }
    }

    fun hideNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }
}