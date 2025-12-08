package me.loghub.api.dto.user.activity

import me.loghub.api.entity.user.UserActivity

interface UserActivityProjection {
    val id: Long;
    val title: String;
    val href: String;
    val action: UserActivity.Action;
}
