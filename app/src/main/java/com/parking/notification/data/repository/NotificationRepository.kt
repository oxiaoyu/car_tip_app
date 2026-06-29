package com.parking.notification.data.repository

import com.parking.notification.data.dao.NotificationDao
import com.parking.notification.data.entity.NotificationItemEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {
    fun getAllFlow(): Flow<List<NotificationItemEntity>> = notificationDao.getAllFlow()

    suspend fun getAll(): List<NotificationItemEntity> = notificationDao.getAll()

    suspend fun getById(id: Long): NotificationItemEntity? = notificationDao.getById(id)

    suspend fun getEnabled(): List<NotificationItemEntity> = notificationDao.getEnabled()

    suspend fun insert(item: NotificationItemEntity): Long = notificationDao.insert(item)

    suspend fun update(item: NotificationItemEntity) = notificationDao.update(item)

    suspend fun delete(item: NotificationItemEntity) = notificationDao.delete(item)

    suspend fun setEnabled(id: Long, enabled: Boolean) =
        notificationDao.setEnabled(id, enabled)
}
