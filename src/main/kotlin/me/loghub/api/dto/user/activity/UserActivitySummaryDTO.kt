package me.loghub.api.dto.user.activity

import java.time.LocalDate

data class UserActivitySummaryDTO(
    val createdDate: LocalDate,
    val count: Long,
)
