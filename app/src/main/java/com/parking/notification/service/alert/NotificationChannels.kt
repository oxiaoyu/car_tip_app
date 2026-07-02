package com.parking.notification.service.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.parking.notification.R
import timber.log.Timber

class NotificationChannels(private val context: Context) {

    fun createChannels() {
        val t0 = System.currentTimeMillis()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val serviceChannel = NotificationChannel(
            CHANNEL_SERVICE,
            context.getString(R.string.channel_service),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            setSound(null, null)
            enableVibration(false)
        }
        manager.createNotificationChannel(serviceChannel)

        val alertChannel = NotificationChannel(
            CHANNEL_ALERT,
            context.getString(R.string.channel_alert),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
        }
        manager.createNotificationChannel(alertChannel)

        Timber.i("[TRACE] NOTIF_CHANNELS: createChannels() took %dms, thread=%s, sdk=%d",
            System.currentTimeMillis() - t0, Thread.currentThread().name, Build.VERSION.SDK_INT)
    }

    fun updateAlertChannelSound(soundUri: Uri?) {
        val t0 = System.currentTimeMillis()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(CHANNEL_ALERT) ?: return
        if (soundUri != null) {
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            channel.setSound(soundUri, attrs)
        } else {
            channel.setSound(null, null)
        }
        manager.createNotificationChannel(channel)
        Timber.i("[TRACE] NOTIF_CHANNELS: updateAlertChannelSound() took %dms", System.currentTimeMillis() - t0)
    }

    fun updateAlertChannelVibration(enable: Boolean) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(CHANNEL_ALERT) ?: return
        channel.enableVibration(enable)
        if (enable) {
            channel.vibrationPattern = longArrayOf(0, 300, 200, 300)
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_SERVICE = "channel_service"
        const val CHANNEL_ALERT = "channel_alert"
    }
}
