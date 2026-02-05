package me.loghub.api.mapper.user

import me.loghub.api.dto.user.*
import me.loghub.api.entity.user.*

object UserMapper {
    fun map(user: User) = UserDTO(
        id = user.id!!,
        username = user.username,
    )

    fun mapDetail(user: User) = UserDetailDTO(
        id = user.id!!,
        email = if (user.privacy.emailPublic) user.email else null,
        username = user.username,
        nickname = user.nickname,
        role = user.role,
        meta = UserMetaDTO(
            profile = mapProfile(user.meta.profile),
            github = mapGitHub(user.meta.github),
            stats = mapStats(user.meta.stats),
        )
    )

    fun mapProfile(profile: UserProfile) = UserProfileDTO(readme = profile.readme)

    fun mapPrivacy(privacy: UserPrivacy) = UserPrivacyDTO(emailPublic = privacy.emailPublic)

    fun mapGitHub(github: UserGitHub) = UserGitHubDTO(
        username = github.username,
        verified = github.verified
    )

    fun mapStats(stats: UserStats) = UserStatsDTO(
        totalPostedCount = stats.totalPostedCount,
        totalAddedStarCount = stats.totalAddedStarCount,
        totalGazedStarCount = stats.totalGazedStarCount,
        topicUsages = stats.topicUsages,
    )
}
