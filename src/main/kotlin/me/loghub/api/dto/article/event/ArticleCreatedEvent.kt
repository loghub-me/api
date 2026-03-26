package me.loghub.api.dto.article.event

data class ArticleCreatedEvent(
    val articleId: Long,
    val writerId: Long,
)
