package me.loghub.api.dto.user

import me.loghub.api.entity.user.User

data class UserDTO(
    val id: Long,
    val username: String,
    val nickname: String,
    val role: User.Role,
)
