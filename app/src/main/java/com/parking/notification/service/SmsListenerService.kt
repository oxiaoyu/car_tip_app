package com.parking.notification.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.parking.notification.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Foreground service to keep SMS listening alive.
 * Android 8+ requires a foreground service with notification for long-running background tasks.
 */
@AndroidEntryPoint
class SmsListenerService : Service() {

    @Inject
    lateinit var notificationChannels: com.parking.notification.service.alert.NotificationChannels

    override fun onCreate() {
        super.onCreate()
        notificationChannels.createChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_SERVICE)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.service_running))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()

        startForeground(NOTIFICATION_ID_SERVICE, notification)

        // Service runs indefinitely; restart if killed
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        // Broadcast to restart
        val restartIntent = Intent(this, SmsReceiver::class.java)
        sendBroadcast(restartIntent)
    }

    companion object {
        const val CHANNEL_ID_SERVICE = "channel_service"
        const val NOTIFICATION_ID_SERVICE = 1001
    }
}
