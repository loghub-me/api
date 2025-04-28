package kr.loghub.api.mapper

import kr.loghub.api.dto.article.ArticleDTO
import kr.loghub.api.dto.article.ArticleDetailDTO
import kr.loghub.api.dto.article.ArticleStatsDTO
import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.article.ArticleStats

object ArticleMapper {
    const val TOPIC_DELIMITER = ","
    const val TOPIC_ATTRIBUTE_DELIMITER = ":"

    fun map(article: Article) = ArticleDTO(
        id = article.id ?: TODO(),
        slug = article.slug,
        title = article.title,
        thumbnail = article.thumbnail,
        writerUsername = article.writerUsername,
        stats = mapStats(article.stats),
        topics = mapTopics(article.topicsFlat),
        createdAt = article.createdAt.toString(),
        updatedAt = article.updatedAt.toString(),
    )

    fun mapDetail(article: Article) = ArticleDetailDTO(
        id = article.id ?: TODO(),
        slug = article.slug,
        title = article.title,
        content = article.content,
        thumbnail = article.thumbnail,
        writer = UserMapper.map(article.writer),
        stats = mapStats(article.stats),
        topics = mapTopics(article.topicsFlat),
        createdAt = article.createdAt.toString(),
        updatedAt = article.updatedAt.toString(),
    )

    private fun mapStats(article: ArticleStats) = ArticleStatsDTO(article.starCount, article.commentCount)

    private fun mapTopics(topics: String) = topics
        .split(TOPIC_DELIMITER)
        .map {
            TopicDTO(
                slug = it.substringBefore(TOPIC_ATTRIBUTE_DELIMITER),
                name = it.substringAfter(TOPIC_ATTRIBUTE_DELIMITER)
            )
        }
}