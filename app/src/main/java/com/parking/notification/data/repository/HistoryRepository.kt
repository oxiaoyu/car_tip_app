package com.parking.notification.data.repository

import com.parking.notification.data.dao.HistoryDao
import com.parking.notification.data.entity.NotificationHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getAllFlow(): Flow<List<NotificationHistoryEntity>> = historyDao.getAllFlow()

    suspend fun getAll(): List<NotificationHistoryEntity> = historyDao.getAll()

    suspend fun getById(id: Long): NotificationHistoryEntity? = historyDao.getById(id)

    suspend fun insert(history: NotificationHistoryEntity): Long =
        historyDao.insert(history)

    suspend fun delete(history: NotificationHistoryEntity) =
        historyDao.delete(history)

    suspend fun deleteAll() = historyDao.deleteAll()

    suspend fun dismiss(id: Long) =
        historyDao.dismiss(id)
}
