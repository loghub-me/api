package me.loghub.api.dto.article

data class ArticleForImportDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val topicSlugs: List<String>,
)
