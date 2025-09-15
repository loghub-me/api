package me.loghub.api.dto.article

data class ArticleForEditDTO(
    val id: Long,
    val title: String,
    val content: String,
    val thumbnail: String,
    val topicSlugs: List<String>,
)
