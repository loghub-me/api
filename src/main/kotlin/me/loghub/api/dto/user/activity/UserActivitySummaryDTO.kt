package me.loghub.api.dto.user.activity

import java.time.LocalDate

data class UserActivitySummaryDTO(
    val date: LocalDate,
    val count: Long,
)
