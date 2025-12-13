package me.loghub.api.dto.series.chapter

import jakarta.validation.constraints.NotNull
import me.loghub.api.lib.validation.ContentValidation
import me.loghub.api.lib.validation.TitleValidation

data class EditSeriesChapterDTO(
    @field:TitleValidation
    val title: String,

    @field:ContentValidation
    val content: String,

    @field:NotNull(message = "공개 여부는 필수 입력값입니다.")
    val published: Boolean,
)