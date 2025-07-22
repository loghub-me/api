package kr.loghub.api.dto.user.activity

import kr.loghub.api.entity.user.UserActivity
import java.time.LocalDate

data class UserActivityDetailDTO(
    val id: Long,
    val path: String,
    val title: String,
    val createdDate: LocalDate,
    val action: UserActivity.Action,
    val userId: Long,
)
