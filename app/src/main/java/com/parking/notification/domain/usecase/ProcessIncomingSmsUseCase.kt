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
        // Step 1: Match against rules
        val matchResult = ruleMatcherEngine.match(senderNumber, messageContent)

        if (!matchResult.matched) {
            Timber.d("No rule matched for SMS from $senderNumber")
            return ProcessingResult(matchResult = matchResult)
        }

        // Step 2: Find notification items linked to the matched rule
        val itemIds = matchResult.matchedRuleId?.let {
            ruleRepository.getItemIdsForRule(it)
        } ?: emptyList()

        // Step 3: Record history
        val historyId = historyRepository.insert(
            NotificationHistoryEntity(
                senderNumber = senderNumber,
                messageContent = messageContent,
                matchedRuleId = matchResult.matchedRuleId,
                triggeredAt = System.currentTimeMillis()
            )
        )

        Timber.i("SMS matched! Rule=%s, Items=%s, History=%d",
            matchResult.matchedRuleKeyword, itemIds, historyId)

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
