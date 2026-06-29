package com.parking.notification.domain.model

data class MatchResult(
    val matched: Boolean,
    val matchedRuleId: Long? = null,
    val matchedRuleKeyword: String? = null,
    val matchType: MatchType = MatchType.NONE
)

enum class MatchType {
    NONE,
    SENDER_MATCHED,
    CONTENT_MATCHED,
    BOTH_MATCHED
}
