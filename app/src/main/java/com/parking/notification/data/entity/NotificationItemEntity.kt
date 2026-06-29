package com.parking.notification.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_items")
data class NotificationItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "enable_vibration")
    val enableVibration: Boolean = true,

    @ColumnInfo(name = "ringtone_uri")
    val ringtoneUri: String? = null,

    @ColumnInfo(name = "ringtone_name")
    val ringtoneName: String? = null,

    @ColumnInfo(name = "enabled")
    val enabled: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
