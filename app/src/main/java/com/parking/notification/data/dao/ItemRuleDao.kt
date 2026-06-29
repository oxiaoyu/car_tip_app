package com.parking.notification.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.parking.notification.data.entity.NotificationItemRuleEntity

@Dao
interface ItemRuleDao {

    @Query("SELECT * FROM notification_item_rules WHERE item_id = :itemId")
    suspend fun getRulesForItem(itemId: Long): List<NotificationItemRuleEntity>

    @Query("SELECT rule_id FROM notification_item_rules WHERE item_id = :itemId")
    suspend fun getRuleIdsForItem(itemId: Long): List<Long>

    @Query("SELECT item_id FROM notification_item_rules WHERE rule_id = :ruleId")
    suspend fun getItemIdsForRule(ruleId: Long): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(relation: NotificationItemRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(relations: List<NotificationItemRuleEntity>)

    @Delete
    suspend fun delete(relation: NotificationItemRuleEntity)

    @Query("DELETE FROM notification_item_rules WHERE item_id = :itemId")
    suspend fun deleteAllForItem(itemId: Long)

    @Query("DELETE FROM notification_item_rules WHERE item_id = :itemId AND rule_id IN (:ruleIds)")
    suspend fun deleteRulesFromItem(itemId: Long, ruleIds: List<Long>)
}
