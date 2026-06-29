package com.parking.notification.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.parking.notification.data.entity.NotificationHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM notification_history ORDER BY triggered_at DESC")
    fun getAllFlow(): Flow<List<NotificationHistoryEntity>>

    @Query("SELECT * FROM notification_history ORDER BY triggered_at DESC")
    suspend fun getAll(): List<NotificationHistoryEntity>

    @Query("SELECT * FROM notification_history WHERE id = :id")
    suspend fun getById(id: Long): NotificationHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: NotificationHistoryEntity): Long

    @Delete
    suspend fun delete(history: NotificationHistoryEntity)

    @Query("DELETE FROM notification_history")
    suspend fun deleteAll()

    @Query("UPDATE notification_history SET dismissed_at = :dismissedAt, dismissed_by = :dismissedBy WHERE id = :id")
    suspend fun dismiss(id: Long, dismissedAt: Long = System.currentTimeMillis(), dismissedBy: Int = 0)
}
