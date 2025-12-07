package me.loghub.api.dto.user

import me.loghub.api.entity.user.User

data class UserDetailDTO(
    val id: Long,
    val email: String?,
    val username: String,
    val profile: UserProfileDTO,
    val github: UserGitHubDTO,
    val role: User.Role,
)
