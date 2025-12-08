package me.loghub.api.dto.series

import me.loghub.api.dto.series.chapter.SeriesChapterDTO

data class SeriesForEditDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val topicSlugs: List<String>,
    val chapters: List<SeriesChapterDTO>,
)
