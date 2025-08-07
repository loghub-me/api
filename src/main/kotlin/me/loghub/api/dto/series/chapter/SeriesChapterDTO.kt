package me.loghub.api.dto.series.chapter

data class SeriesChapterDTO(
    val id: Long,
    val title: String,
    val sequence: Int,
    val createdAt: String,
    val updatedAt: String,
)