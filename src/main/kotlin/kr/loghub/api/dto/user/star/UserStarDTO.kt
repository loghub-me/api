package kr.loghub.api.dto.user.star

import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.entity.user.UserStar

data class UserStarDTO(
    val id: Long,
    val title: String,
    val writerId: Long,
    val topics: List<TopicDTO>,
    val target: UserStar.Target,
)