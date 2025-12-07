package me.loghub.api.mapper.user

import me.loghub.api.dto.user.*
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserGitHub
import me.loghub.api.entity.user.UserPrivacy
import me.loghub.api.entity.user.UserProfile

object UserMapper {
    fun map(user: User) = UserDTO(
        id = user.id!!,
        username = user.username,
    )

    fun mapDetail(user: User) = UserDetailDTO(
        id = user.id!!,
        email = if (user.privacy.emailPublic) user.email else null,
        username = user.username,
        profile = mapProfile(user.profile),
        github = mapGitHub(user.github),
        role = user.role
    )

    fun mapProfile(profile: UserProfile) = UserProfileDTO(
        nickname = profile.nickname,
        readme = profile.readme
    )

    fun mapPrivacy(privacy: UserPrivacy) = UserPrivacyDTO(emailPublic = privacy.emailPublic)

    fun mapGitHub(github: UserGitHub) = UserGitHubDTO(
        username = github.username,
        verified = github.verified
    )
}