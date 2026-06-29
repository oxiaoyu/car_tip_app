package com.parking.notification.domain.usecase

import com.parking.notification.data.entity.NotificationHistoryEntity
import com.parking.notification.data.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    fun getAllFlow(): Flow<List<NotificationHistoryEntity>> = historyRepository.getAllFlow()

    suspend fun getAll(): List<NotificationHistoryEntity> = historyRepository.getAll()

    suspend fun getById(id: Long) = historyRepository.getById(id)

    suspend fun deleteAll() = historyRepository.deleteAll()
}
