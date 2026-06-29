package com.parking.notification.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trigger_rules")
data class TriggerRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "phone_keyword")
    val phoneKeyword: String,

    @ColumnInfo(name = "content_keyword")
    val contentKeyword: String,

    @ColumnInfo(name = "match_mode")
    val matchMode: Int = 0, // 0 = fuzzy match (LIKE)

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
