package com.parking.notification.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.parking.notification.data.entity.TriggerRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {

    @Query("SELECT * FROM trigger_rules ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<TriggerRuleEntity>>

    @Query("SELECT * FROM trigger_rules ORDER BY created_at DESC")
    suspend fun getAll(): List<TriggerRuleEntity>

    @Query("SELECT * FROM trigger_rules WHERE id = :id")
    suspend fun getById(id: Long): TriggerRuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: TriggerRuleEntity): Long

    @Update
    suspend fun update(rule: TriggerRuleEntity)

    @Delete
    suspend fun delete(rule: TriggerRuleEntity)

    @Query("SELECT COUNT(*) FROM notification_item_rules WHERE rule_id = :ruleId")
    suspend fun getReferenceCount(ruleId: Long): Int
}
