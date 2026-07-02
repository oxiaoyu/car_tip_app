package com.parking.notification.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.parking.notification.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Foreground service to keep SMS listening alive.
 * Android 8+ requires a foreground service with notification for long-running background tasks.
 */
@AndroidEntryPoint
class SmsListenerService : Service() {

    @Inject
    lateinit var notificationChannels: com.parking.notification.service.alert.NotificationChannels

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        val t0 = System.currentTimeMillis()
        super.onCreate()
        Timber.i("[TRACE] SMS_SERVICE: onCreate on thread=%s", Thread.currentThread().name)
        // Channel creation is IPC to NotificationManagerService.
        // Run on background to avoid blocking main thread on slow ROMs.
        serviceScope.launch {
            Timber.i("[TRACE] SMS_SERVICE: creating notification channels at +%dms...", System.currentTimeMillis() - t0)
            notificationChannels.createChannels()
            Timber.i("[TRACE] SMS_SERVICE: channels created at +%dms", System.currentTimeMillis() - t0)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val t0 = System.currentTimeMillis()
        Timber.i("[TRACE] SMS_SERVICE: onStartCommand on thread=%s", Thread.currentThread().name)

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

        Timber.i("[TRACE] SMS_SERVICE: calling startForeground() at +%dms...", System.currentTimeMillis() - t0)
        startForeground(NOTIFICATION_ID_SERVICE, notification)
        Timber.i("[TRACE] SMS_SERVICE: startForeground() returned at +%dms (took %dms), thread=%s",
            System.currentTimeMillis() - t0, System.currentTimeMillis() - t0, Thread.currentThread().name)

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
