package com.parking.notification.service.alert

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.parking.notification.R
import com.parking.notification.data.entity.NotificationItemEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationChannels: NotificationChannels
) {
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    @SuppressLint("MissingPermission")
    fun showAlert(
        context: Context,
        notificationItem: NotificationItemEntity,
        senderNumber: String,
        messageContent: String,
        historyId: Long
    ) {
        notificationChannels.createChannels()

        val fullScreenIntent = Intent(context, com.parking.notification.ui.AlertFullScreenActivity::class.java).apply {
            putExtra(EXTRA_HISTORY_ID, historyId)
            putExtra(EXTRA_SENDER, senderNumber)
            putExtra(EXTRA_MESSAGE, messageContent)
            putExtra(EXTRA_ITEM_NAME, notificationItem.name)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, AlertDismissReceiver::class.java).apply {
            putExtra(EXTRA_HISTORY_ID, historyId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, historyId.toInt(), dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = notificationItem.ringtoneUri
            ?.takeIf { it.isNotBlank() }
            ?.let { Uri.parse(it) }
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_ALERT)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(notificationItem.name.ifBlank { context.getString(R.string.app_name) })
            .setContentText("来自 $senderNumber：${messageContent.take(100)}")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSound(soundUri, AudioManager.STREAM_NOTIFICATION)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                context.getString(R.string.close_alert),
                dismissPendingIntent
            )

        if (notificationItem.enableVibration) {
            builder.setVibrate(longArrayOf(0, 400, 200, 400, 200, 400))
            vibrate()
        }

        val notification = builder.build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_TAG, historyId.toInt(), notification)
        Timber.i("Alert notification shown for history=%d, item=%s", historyId, notificationItem.name)
    }

    fun dismissAlert(historyId: Long) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_TAG, historyId.toInt())
        vibrator?.cancel()
    }

    private fun vibrate() {
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 400, 200, 400, 200, 400),
                            -1
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(longArrayOf(0, 400, 200, 400, 200, 400), -1)
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Vibration failed")
        }
    }

    companion object {
        const val EXTRA_HISTORY_ID = "extra_history_id"
        const val EXTRA_SENDER = "extra_sender"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_ITEM_NAME = "extra_item_name"
        const val NOTIFICATION_TAG = "parking_alert"
    }
}
