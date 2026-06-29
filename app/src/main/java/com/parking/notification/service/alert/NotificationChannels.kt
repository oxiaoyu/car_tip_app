package com.parking.notification.service.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.parking.notification.R

class NotificationChannels(private val context: Context) {

    fun createChannels() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Service channel (low importance, silent)
        val serviceChannel = NotificationChannel(
            CHANNEL_SERVICE,
            context.getString(R.string.channel_service),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "挪车通知后台监听服务"
            setSound(null, null)
            enableVibration(false)
        }
        manager.createNotificationChannel(serviceChannel)

        // Alert channel (high importance, customizable sound/vibration)
        val alertChannel = NotificationChannel(
            CHANNEL_ALERT,
            context.getString(R.string.channel_alert),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "挪车提醒通知"
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
        }
        manager.createNotificationChannel(alertChannel)
    }

    fun updateAlertChannelSound(soundUri: Uri?) {
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
