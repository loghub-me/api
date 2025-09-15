package me.loghub.api.dto.user.star

import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.dto.user.UserDTO
import me.loghub.api.entity.user.UserStar

data class UserStarDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val writer: UserDTO,
    val topics: List<TopicDTO>,
    val target: UserStar.Target,
)