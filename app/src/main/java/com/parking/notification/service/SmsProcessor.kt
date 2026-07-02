package com.parking.notification.service

import android.content.Context
import com.parking.notification.data.repository.NotificationRepository
import com.parking.notification.domain.usecase.ProcessIncomingSmsUseCase
import com.parking.notification.service.alert.AlertManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsProcessor @Inject constructor(
    private val processIncomingSmsUseCase: ProcessIncomingSmsUseCase,
    private val notificationRepository: NotificationRepository,
    private val alertManager: AlertManager,
    @ApplicationContext private val context: Context
) {
    suspend fun process(senderNumber: String, messageBody: String) {
        val t0 = System.currentTimeMillis()
        Timber.i("[TRACE] SMS_PROCESSOR: process() start at +%dms on thread=%s",
            t0 - getAppStartTime(), Thread.currentThread().name)
        withContext(Dispatchers.IO) {
            Timber.i("[TRACE] SMS_PROCESSOR: running UseCase at +%dms",
                System.currentTimeMillis() - getAppStartTime())
            val result = processIncomingSmsUseCase(senderNumber, messageBody)

            if (!result.matchResult.matched) {
                Timber.i("[TRACE] SMS_PROCESSOR: no rule matched, done at +%dms",
                    System.currentTimeMillis() - getAppStartTime())
                return@withContext
            }

            Timber.i("[TRACE] SMS_PROCESSOR: matched! rule=%s, matchedItemIds=%s at +%dms",
                result.matchResult.matchedRule, result.matchedItemIds,
                System.currentTimeMillis() - getAppStartTime())

            val items = result.matchedItemIds.mapNotNull { id ->
                notificationRepository.getById(id)
            }

            if (items.isEmpty()) {
                Timber.w("[TRACE] SMS_PROCESSOR: matched but no notification items found at +%dms",
                    System.currentTimeMillis() - getAppStartTime())
                return@withContext
            }

            val enabledItem = items.firstOrNull { it.enabled }
            if (enabledItem != null) {
                Timber.i("[TRACE] SMS_PROCESSOR: calling alertManager.showAlert() for '%s' at +%dms",
                    enabledItem.name, System.currentTimeMillis() - getAppStartTime())
                alertManager.showAlert(
                    context = context,
                    notificationItem = enabledItem,
                    senderNumber = senderNumber,
                    messageContent = messageBody,
                    historyId = result.historyId ?: 0
                )
                Timber.i("[TRACE] SMS_PROCESSOR: showAlert() returned at +%dms",
                    System.currentTimeMillis() - getAppStartTime())
            }
        }
    }

    private fun getAppStartTime(): Long =
        System.currentTimeMillis() - 60000
}
