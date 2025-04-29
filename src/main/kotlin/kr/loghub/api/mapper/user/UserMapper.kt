package kr.loghub.api.mapper.user

import kr.loghub.api.dto.user.UserDTO
import kr.loghub.api.entity.user.User

object UserMapper {
    fun map(user: User) = UserDTO(
        username = user.username,
        nickname = user.profile.nickname,
        role = user.role
    )
}