package kr.loghub.api.mapper.user

import kr.loghub.api.dto.user.UserDTO
import kr.loghub.api.dto.user.UserPrivacyDTO
import kr.loghub.api.dto.user.UserProfileDTO
import kr.loghub.api.entity.user.User

object UserMapper {
    fun map(user: User) = UserDTO(
        username = user.username,
        nickname = user.profile.nickname,
        role = user.role
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