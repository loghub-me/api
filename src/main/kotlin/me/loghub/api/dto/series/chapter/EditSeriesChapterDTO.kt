package me.loghub.api.dto.series.chapter

import me.loghub.api.lib.validation.ContentValidation
import me.loghub.api.lib.validation.TitleValidation

data class EditSeriesChapterDTO(
    @field:TitleValidation
    val title: String,

    @field:ContentValidation
    val content: String,
)