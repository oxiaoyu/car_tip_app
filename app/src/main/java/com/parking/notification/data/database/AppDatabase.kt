package com.parking.notification.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.parking.notification.data.dao.HistoryDao
import com.parking.notification.data.dao.ItemRuleDao
import com.parking.notification.data.dao.NotificationDao
import com.parking.notification.data.dao.RuleDao
import com.parking.notification.data.entity.NotificationHistoryEntity
import com.parking.notification.data.entity.NotificationItemEntity
import com.parking.notification.data.entity.NotificationItemRuleEntity
import com.parking.notification.data.entity.TriggerRuleEntity

@Database(
    entities = [
        NotificationItemEntity::class,
        TriggerRuleEntity::class,
        NotificationItemRuleEntity::class,
        NotificationHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
    abstract fun ruleDao(): RuleDao
    abstract fun itemRuleDao(): ItemRuleDao
    abstract fun historyDao(): HistoryDao
}
