package com.parking.notification.data.repository

import com.parking.notification.data.dao.ItemRuleDao
import com.parking.notification.data.dao.RuleDao
import com.parking.notification.data.entity.NotificationItemRuleEntity
import com.parking.notification.data.entity.TriggerRuleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleRepository @Inject constructor(
    private val ruleDao: RuleDao,
    private val itemRuleDao: ItemRuleDao
) {
    fun getAllFlow(): Flow<List<TriggerRuleEntity>> = ruleDao.getAllFlow()

    suspend fun getAll(): List<TriggerRuleEntity> = ruleDao.getAll()

    suspend fun getById(id: Long): TriggerRuleEntity? = ruleDao.getById(id)

    suspend fun insert(rule: TriggerRuleEntity): Long = ruleDao.insert(rule)

    suspend fun update(rule: TriggerRuleEntity) = ruleDao.update(rule)

    suspend fun delete(rule: TriggerRuleEntity) = ruleDao.delete(rule)

    suspend fun getReferenceCount(ruleId: Long): Int = ruleDao.getReferenceCount(ruleId)

    // Item-Rule relations
    suspend fun getRuleIdsForItem(itemId: Long): List<Long> =
        itemRuleDao.getRuleIdsForItem(itemId)

    suspend fun getItemIdsForRule(ruleId: Long): List<Long> =
        itemRuleDao.getItemIdsForRule(ruleId)

    suspend fun addRuleToItem(itemId: Long, ruleId: Long) =
        itemRuleDao.insert(NotificationItemRuleEntity(itemId = itemId, ruleId = ruleId))

    suspend fun setRulesForItem(itemId: Long, ruleIds: List<Long>) {
        itemRuleDao.deleteAllForItem(itemId)
        itemRuleDao.insertAll(ruleIds.map { NotificationItemRuleEntity(itemId = itemId, ruleId = it) })
    }

    suspend fun deleteAllRulesForItem(itemId: Long) =
        itemRuleDao.deleteAllForItem(itemId)
}
