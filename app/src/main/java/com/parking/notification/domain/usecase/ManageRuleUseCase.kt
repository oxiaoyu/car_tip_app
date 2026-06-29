package com.parking.notification.domain.usecase

import com.parking.notification.data.entity.TriggerRuleEntity
import com.parking.notification.data.repository.RuleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageRuleUseCase @Inject constructor(
    private val ruleRepository: RuleRepository
) {
    fun getAllFlow() = ruleRepository.getAllFlow()

    suspend fun getAll() = ruleRepository.getAll()

    suspend fun getById(id: Long) = ruleRepository.getById(id)

    suspend fun create(phoneKeyword: String, contentKeyword: String, matchMode: Int = 0): Long {
        val rule = TriggerRuleEntity(
            phoneKeyword = phoneKeyword,
            contentKeyword = contentKeyword,
            matchMode = matchMode
        )
        return ruleRepository.insert(rule)
    }

    suspend fun update(rule: TriggerRuleEntity) {
        ruleRepository.update(rule.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun delete(rule: TriggerRuleEntity) {
        ruleRepository.delete(rule)
    }

    /** Returns false if rule is referenced by items */
    suspend fun canDelete(ruleId: Long): Boolean {
        return ruleRepository.getReferenceCount(ruleId) == 0
    }

    suspend fun getRuleIdsForItem(itemId: Long) = ruleRepository.getRuleIdsForItem(itemId)

    suspend fun setRulesForItem(itemId: Long, ruleIds: List<Long>) {
        ruleRepository.setRulesForItem(itemId, ruleIds)
    }
}
