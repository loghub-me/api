package me.loghub.api.mapper.user

import me.loghub.api.dto.user.UserDTO
import me.loghub.api.dto.user.UserDetailDTO
import me.loghub.api.dto.user.UserPrivacyDTO
import me.loghub.api.dto.user.UserProfileDTO
import me.loghub.api.entity.user.User

object UserMapper {
    fun map(user: User) = UserDTO(
        id = user.id!!,
        username = user.username,
        nickname = user.profile.nickname,
        role = user.role
    )

    fun mapDetail(user: User) = UserDetailDTO(
        id = user.id!!,
        username = user.username,
        nickname = user.profile.nickname,
        readme = user.profile.readme,
        role = user.role,
    )

    fun mapProfile(user: User) = UserProfileDTO(
        nickname = user.profile.nickname,
        readme = user.profile.readme,
    )

    fun mapPrivacy(user: User) = UserPrivacyDTO(
        emailVisible = user.privacy.emailVisible,
        starVisible = user.privacy.starVisible,
    )
}