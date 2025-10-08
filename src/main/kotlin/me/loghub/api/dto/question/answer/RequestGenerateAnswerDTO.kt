package me.loghub.api.dto.question.answer

import jakarta.validation.constraints.Size

data class RequestGenerateAnswerDTO(
    @field:Size(max = 255, message = "답변 지침은 255자 이내로 작성해야 합니다.")
    val instruction: String?
)