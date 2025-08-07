package me.loghub.api.dto.series.chapter

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import me.loghub.api.lib.validation.Trimmed

data class EditSeriesChapterDTO(
    @field:NotBlank(message = "제목은 필수 입력 항목입니다.")
    @field:Size(min = 2, max = 56, message = "제목은 2자 이상 56자 이하이어야 합니다.")
    @field:Trimmed
    val title: String,

    @field:NotBlank(message = "내용은 필수 입력 항목입니다.")
    @field:Size(min = 10, max = 8192, message = "내용은 10자 이상 8192자 이하이어야 합니다.")
    val content: String,
)