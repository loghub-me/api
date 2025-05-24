package kr.loghub.api.dto.question.answer

import kr.loghub.api.dto.common.ContentDTO

data class AnswerDTO(
    val id: Long,
    val content: ContentDTO,
    val accepted: Boolean,
    val createdAt: String,
    val updatedAt: String,
)