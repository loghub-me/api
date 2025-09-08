package me.loghub.api.dto.question.answer

import me.loghub.api.dto.common.AnchorDTO
import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.user.UserDTO

data class QuestionAnswerDTO(
    val id: Long,
    val title: String,
    val content: ContentDTO,
    val anchors: List<AnchorDTO>,
    val accepted: Boolean,
    val writer: UserDTO,
    val createdAt: String,
    val updatedAt: String,
)