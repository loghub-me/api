package kr.loghub.api.dto.user.star

import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.entity.user.UserStar

data class UserStarDTO(
    val id: Long,
    val path: String,
    val title: String,
    val writerId: Long,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val target: UserStar.Target,
    val targetLabel: String,
)