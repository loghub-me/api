package kr.loghub.api.dto.user

import kr.loghub.api.entity.question.Question

data class UserPostDTO(
    val path: String,
    val title: String,
    val questionStatus: Question.Status?,
    val updatedAt: String
)
