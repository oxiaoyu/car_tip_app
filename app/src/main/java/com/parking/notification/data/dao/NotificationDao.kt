package com.parking.notification.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.parking.notification.data.entity.NotificationItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification_items ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<NotificationItemEntity>>

    @Query("SELECT * FROM notification_items ORDER BY created_at DESC")
    suspend fun getAll(): List<NotificationItemEntity>

    @Query("SELECT * FROM notification_items WHERE id = :id")
    suspend fun getById(id: Long): NotificationItemEntity?

    @Query("SELECT * FROM notification_items WHERE enabled = 1")
    suspend fun getEnabled(): List<NotificationItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: NotificationItemEntity): Long

    @Update
    suspend fun update(item: NotificationItemEntity)

    @Delete
    suspend fun delete(item: NotificationItemEntity)

    @Query("UPDATE notification_items SET enabled = :enabled, updated_at = :updatedAt WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean, updatedAt: Long = System.currentTimeMillis())
}
