package com.parking.notification.domain.usecase

import com.parking.notification.data.entity.NotificationHistoryEntity
import com.parking.notification.data.entity.NotificationItemEntity
import com.parking.notification.data.repository.HistoryRepository
import com.parking.notification.data.repository.RuleRepository
import com.parking.notification.domain.engine.RuleMatcherEngine
import com.parking.notification.domain.model.MatchResult
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main use case: processes an incoming SMS by matching rules,
 * finding the appropriate notification item, and recording history.
 */
@Singleton
class ProcessIncomingSmsUseCase @Inject constructor(
    private val ruleMatcherEngine: RuleMatcherEngine,
    private val ruleRepository: RuleRepository,
    private val historyRepository: HistoryRepository
) {
    /**
     * Process an incoming SMS.
     * @return Pair of (matchResult, matchedItemIds) — the matched items to alert
     */
    suspend operator fun invoke(
        senderNumber: String,
        messageContent: String
    ): ProcessingResult {
        val t0 = System.currentTimeMillis()
        Timber.i("[TRACE] USE_CASE: ProcessIncomingSmsUseCase start on thread=%s", Thread.currentThread().name)

        val matchResult = ruleMatcherEngine.match(senderNumber, messageContent)
        Timber.i("[TRACE] USE_CASE: ruleMatcherEngine.match() at +%dms, matched=%s",
            System.currentTimeMillis() - t0, matchResult.matched)

        if (!matchResult.matched) {
            return ProcessingResult(matchResult = matchResult)
        }

        val itemIds = matchResult.matchedRuleId?.let {
            ruleRepository.getItemIdsForRule(it)
        } ?: emptyList()
        Timber.i("[TRACE] USE_CASE: ruleRepository.getItemIdsForRule() at +%dms, ids=%s",
            System.currentTimeMillis() - t0, itemIds)

        val historyId = historyRepository.insert(
            NotificationHistoryEntity(
                senderNumber = senderNumber,
                messageContent = messageContent,
                matchedRuleId = matchResult.matchedRuleId,
                triggeredAt = System.currentTimeMillis()
            )
        )
        Timber.i("[TRACE] USE_CASE: historyRepository.insert() at +%dms, historyId=%d, matchedRule='%s'",
            System.currentTimeMillis() - t0, historyId, matchResult.matchedRuleKeyword)

        return ProcessingResult(
            matchResult = matchResult,
            matchedItemIds = itemIds,
            historyId = historyId
        )
    }
}

data class ProcessingResult(
    val matchResult: MatchResult,
    val matchedItemIds: List<Long> = emptyList(),
    val historyId: Long? = null
)
