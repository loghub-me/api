package kr.loghub.api.dto.user

import kr.loghub.api.entity.user.User

data class UserDetailDTO(
    val username: String,
    val nickname: String,
    val readme: String,
    val role: User.Role,
)
