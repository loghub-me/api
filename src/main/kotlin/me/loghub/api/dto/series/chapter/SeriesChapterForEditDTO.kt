package me.loghub.api.dto.series.chapter

data class SeriesChapterForEditDTO(
    val id: Long,
    val title: String,
    val content: String,
    val draft: String?,
    val sequence: Int,
    val published: Boolean,
)