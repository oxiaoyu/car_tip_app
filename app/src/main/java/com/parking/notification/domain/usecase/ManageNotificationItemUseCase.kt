package com.parking.notification.domain.usecase

import com.parking.notification.data.entity.NotificationItemEntity
import com.parking.notification.data.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageNotificationItemUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    fun getAllFlow() = notificationRepository.getAllFlow()

    suspend fun getAll() = notificationRepository.getAll()

    suspend fun getById(id: Long) = notificationRepository.getById(id)

    suspend fun getEnabled() = notificationRepository.getEnabled()

    suspend fun create(name: String): Long {
        val item = NotificationItemEntity(name = name)
        return notificationRepository.insert(item)
    }

    suspend fun update(item: NotificationItemEntity) {
        notificationRepository.update(item.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun delete(item: NotificationItemEntity) {
        notificationRepository.delete(item)
    }

    suspend fun toggleEnabled(id: Long, enabled: Boolean) {
        notificationRepository.setEnabled(id, enabled)
    }
}
