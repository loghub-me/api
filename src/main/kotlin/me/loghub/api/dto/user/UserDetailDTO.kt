package me.loghub.api.dto.user

import me.loghub.api.entity.user.User

data class UserDetailDTO(
    val id: Long,
    val email: String?,
    val username: String,
    val nickname: String,
    val role: User.Role,
    val meta: UserMetaDTO,
)
