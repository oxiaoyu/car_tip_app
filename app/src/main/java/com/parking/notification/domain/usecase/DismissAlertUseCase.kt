package com.parking.notification.domain.usecase

import com.parking.notification.data.repository.HistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DismissAlertUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(historyId: Long) {
        historyRepository.dismiss(historyId)
    }
}
