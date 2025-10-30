package me.loghub.api.dto.question.answer

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import me.loghub.api.constant.ai.LogHubChatModel

data class RequestGenerateAnswerDTO(
    @field:Size(max = 255, message = "답변 지침은 255자 이내로 작성해야 합니다.")
    val instruction: String?,

    @field:NotNull(message = "챗봇 모델을 선택해야 합니다.")
    val chatModel: LogHubChatModel,
)