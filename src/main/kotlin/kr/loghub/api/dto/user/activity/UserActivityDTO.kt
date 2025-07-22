package kr.loghub.api.dto.user.activity

import java.time.LocalDate

data class UserActivityDTO(
    val createdDate: LocalDate,
    val count: Long,
)
