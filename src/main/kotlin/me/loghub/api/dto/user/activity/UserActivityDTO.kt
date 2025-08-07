package me.loghub.api.dto.user.activity

import me.loghub.api.entity.user.UserActivity

data class UserActivityDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val action: UserActivity.Action,
)
