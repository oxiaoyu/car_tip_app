package com.parking.notification.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notification_history",
    foreignKeys = [
        ForeignKey(
            entity = NotificationItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = TriggerRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["matched_rule_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("item_id"),
        Index("matched_rule_id"),
        Index("triggered_at")
    ]
)
data class NotificationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "item_id")
    val itemId: Long? = null,

    @ColumnInfo(name = "sender_number")
    val senderNumber: String,

    @ColumnInfo(name = "message_content")
    val messageContent: String,

    @ColumnInfo(name = "matched_rule_id")
    val matchedRuleId: Long? = null,

    @ColumnInfo(name = "triggered_at")
    val triggeredAt: Long,

    @ColumnInfo(name = "dismissed_at")
    val dismissedAt: Long? = null,

    @ColumnInfo(name = "dismissed_by")
    val dismissedBy: Int = 0 // 0 = button close
)
