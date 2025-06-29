package kr.loghub.api.mapper.user

import kr.loghub.api.dto.user.*
import kr.loghub.api.entity.user.User
import kr.loghub.api.entity.user.UserPost
import java.time.format.DateTimeFormatter

object UserMapper {
    fun map(user: User) = UserDTO(
        username = user.username,
        nickname = user.profile.nickname,
        role = user.role
    )

    fun mapDetail(user: User) = UserDetailDTO(
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

    fun mapPost(post: UserPost) = UserPostDTO(
        path = post.path,
        title = post.title,
        questionStatus = post.questionStatus,
        updatedAt = post.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )
}