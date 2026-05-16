package me.loghub.api.dto.question.answer

import me.loghub.api.dto.common.AnchorDTO
import me.loghub.api.dto.user.UserDTO
import java.time.OffsetDateTime

data class QuestionAnswerDTO(
    val id: Long,
    val title: String,
    val contentHTML: String,
    val anchors: List<AnchorDTO>,
    val accepted: Boolean,
    val writer: UserDTO,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)