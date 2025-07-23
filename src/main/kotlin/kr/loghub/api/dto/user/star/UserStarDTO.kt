package kr.loghub.api.dto.user.star

import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.dto.user.UserSimpleDTO
import kr.loghub.api.entity.user.UserStar

data class UserStarDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val writer: UserSimpleDTO,
    val topics: List<TopicDTO>,
    val target: UserStar.Target,
)