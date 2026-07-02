package com.parking.notification.domain.engine

import com.parking.notification.data.entity.TriggerRuleEntity
import com.parking.notification.data.repository.RuleRepository
import com.parking.notification.domain.model.MatchResult
import com.parking.notification.domain.model.MatchType
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleMatcherEngine @Inject constructor(
    private val ruleRepository: RuleRepository
) {

    suspend fun match(senderNumber: String, messageContent: String): MatchResult {
        val t0 = System.currentTimeMillis()
        Timber.i("[TRACE] RULE_ENGINE: match() start on thread=%s", Thread.currentThread().name)
        val rules = ruleRepository.getAll()
        Timber.i("[TRACE] RULE_ENGINE: loaded %d rules at +%dms", rules.size, System.currentTimeMillis() - t0)

        for (rule in rules) {
            val senderMatch = matchPhoneKeyword(senderNumber, rule)
            val contentMatch = matchContentKeyword(messageContent, rule)

            if (senderMatch || contentMatch) {
                val matchType = when {
                    senderMatch && contentMatch -> MatchType.BOTH_MATCHED
                    senderMatch -> MatchType.SENDER_MATCHED
                    else -> MatchType.CONTENT_MATCHED
                }
                Timber.i("[TRACE] RULE_ENGINE: matched rule=%d '%s' at +%dms, type=%s",
                    rule.id, rule.phoneKeyword, System.currentTimeMillis() - t0, matchType)
                return MatchResult(
                    matched = true,
                    matchedRuleId = rule.id,
                    matchedRuleKeyword = rule.phoneKeyword,
                    matchType = matchType
                )
            }
        }

        Timber.i("[TRACE] RULE_ENGINE: no match found at +%dms (%d rules checked)",
            System.currentTimeMillis() - t0, rules.size)
        return MatchResult(matched = false)
    }

    /**
     * Match phone number keyword.
     * Checks if the sender number starts with or contains the keyword.
     */
    private fun matchPhoneKeyword(senderNumber: String, rule: TriggerRuleEntity): Boolean {
        if (rule.phoneKeyword.isBlank()) return true // Empty keyword always matches
        // Remove non-digit characters for matching
        val cleanNumber = senderNumber.filter { it.isDigit() }
        val cleanKeyword = rule.phoneKeyword.filter { it.isDigit() }
        return when (rule.matchMode) {
            1 -> cleanNumber == cleanKeyword
            else -> cleanNumber.contains(cleanKeyword)
        }
    }

    /**
     * Match message content keyword.
     * Checks if the SMS body contains the specified keyword.
     */
    private fun matchContentKeyword(messageContent: String, rule: TriggerRuleEntity): Boolean {
        if (rule.contentKeyword.isBlank()) return true
        return when (rule.matchMode) {
            1 -> messageContent.contains(rule.contentKeyword, ignoreCase = true)
            else -> fuzzyMatch(messageContent, rule.contentKeyword)
        }
    }

    /**
     * Fuzzy matching: check if all keyword characters appear in order.
     */
    private fun fuzzyMatch(text: String, keyword: String): Boolean {
        val lowerText = text.lowercase()
        val lowerKeyword = keyword.lowercase()
        var keywordIndex = 0
        for (char in lowerText) {
            if (keywordIndex < lowerKeyword.length && char == lowerKeyword[keywordIndex]) {
                keywordIndex++
            }
        }
        return keywordIndex == lowerKeyword.length
    }
}
