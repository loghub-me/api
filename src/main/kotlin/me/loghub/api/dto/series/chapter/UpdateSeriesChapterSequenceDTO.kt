package me.loghub.api.dto.series.chapter

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdateSeriesChapterSequenceDTO(
    @field:NotNull(message = "시퀀스 목록은 null일 수 없습니다.")
    @field:Size(min = 1, message = "최소 하나 이상의 챕터가 필요합니다.")
    val sequences: List<Int>,
)
