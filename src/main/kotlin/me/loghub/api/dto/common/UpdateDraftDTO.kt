package me.loghub.api.dto.common

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateDraftDTO(
    @field:NotBlank(message = "내용은 필수 입력 항목입니다.")
    @field:Size(min = 10, max = 8192, message = "내용은 10자 이상 8192자 이하이어야 합니다.")
    val content: String,
)