package com.parking.notification.service.alert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.parking.notification.domain.usecase.DismissAlertUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * BroadcastReceiver to handle alert dismiss actions from notification buttons.
 */
@AndroidEntryPoint
class AlertDismissReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dismissAlertUseCase: DismissAlertUseCase

    @Inject
    lateinit var alertManager: AlertManager

    override fun onReceive(context: Context, intent: Intent) {
        val historyId = intent.getLongExtra(AlertManager.EXTRA_HISTORY_ID, -1L)
        if (historyId == -1L) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                dismissAlertUseCase(historyId)
                alertManager.dismissAlert(historyId)
                Timber.i("Alert dismissed for history=%d via notification action", historyId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to dismiss alert for history=%d", historyId)
            }
        }
    }
}
