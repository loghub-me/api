package me.loghub.api.dto.user.activity

import me.loghub.api.entity.user.UserActivity
import java.time.LocalDateTime

data class UserActivityDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val action: UserActivity.Action,
    val createdAt: LocalDateTime,
)
