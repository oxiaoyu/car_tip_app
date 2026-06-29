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
        withContext(Dispatchers.IO) {
            val result = processIncomingSmsUseCase(senderNumber, messageBody)

            if (!result.matchResult.matched) {
                Timber.d("No rule matched, skipping alert")
                return@withContext
            }

            val items = result.matchedItemIds.mapNotNull { id ->
                notificationRepository.getById(id)
            }

            if (items.isEmpty()) {
                Timber.w("SMS matched but no notification items found for rule")
                return@withContext
            }

            val enabledItem = items.firstOrNull { it.enabled }
            if (enabledItem != null) {
                alertManager.showAlert(
                    context = context,
                    notificationItem = enabledItem,
                    senderNumber = senderNumber,
                    messageContent = messageBody,
                    historyId = result.historyId ?: 0
                )
                Timber.i("Alert triggered for item: ${enabledItem.name}")
            }
        }
    }
}
