package kr.loghub.api.dto.user

import kr.loghub.api.entity.user.User

data class UserDTO(
    val username: String,
    val nickname: String,
    val role: User.Role,
)
