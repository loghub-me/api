package kr.loghub.api.dto.question.answer

import kr.loghub.api.dto.common.ContentDTO
import kr.loghub.api.dto.user.UserDTO

data class QuestionAnswerDTO(
    val id: Long,
    val title: String,
    val content: ContentDTO,
    val accepted: Boolean,
    val writer: UserDTO,
    val createdAt: String,
    val updatedAt: String,
)