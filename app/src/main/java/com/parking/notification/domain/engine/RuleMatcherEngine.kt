package com.parking.notification.domain.engine

import com.parking.notification.data.entity.TriggerRuleEntity
import com.parking.notification.data.repository.RuleRepository
import com.parking.notification.domain.model.MatchResult
import com.parking.notification.domain.model.MatchType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core rule matching engine.
 * Iterates all rules and checks if an incoming SMS matches any trigger rule
 * by sender phone number keyword and/or message content keyword.
 */
@Singleton
class RuleMatcherEngine @Inject constructor(
    private val ruleRepository: RuleRepository
) {

    /**
     * Match an incoming SMS against all active rules.
     * Returns the first matching result, or MatchResult(matched=false) if none.
     */
    suspend fun match(senderNumber: String, messageContent: String): MatchResult {
        val rules = ruleRepository.getAll()

        for (rule in rules) {
            val senderMatch = matchPhoneKeyword(senderNumber, rule)
            val contentMatch = matchContentKeyword(messageContent, rule)

            // A rule matches if either sender or content matches (OR logic)
            if (senderMatch || contentMatch) {
                val matchType = when {
                    senderMatch && contentMatch -> MatchType.BOTH_MATCHED
                    senderMatch -> MatchType.SENDER_MATCHED
                    else -> MatchType.CONTENT_MATCHED
                }
                return MatchResult(
                    matched = true,
                    matchedRuleId = rule.id,
                    matchedRuleKeyword = rule.phoneKeyword,
                    matchType = matchType
                )
            }
        }

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
