package kr.loghub.api.mapper.article

import kr.loghub.api.dto.article.ArticleDTO
import kr.loghub.api.dto.article.ArticleDetailDTO
import kr.loghub.api.dto.article.ArticleStatsDTO
import kr.loghub.api.dto.common.ContentDTO
import kr.loghub.api.dto.user.UserSimpleDTO
import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.article.ArticleStats
import kr.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object ArticleMapper {
    fun map(article: Article) = ArticleDTO(
        id = article.id!!,
        slug = article.slug,
        title = article.title,
        thumbnail = article.thumbnail,
        writer = UserSimpleDTO(article.writer.id!!, article.writerUsername),
        stats = mapStats(article.stats),
        topics = article.topicsFlat,
        createdAt = article.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = article.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapDetail(article: Article, contentHTML: String) = ArticleDetailDTO(
        id = article.id!!,
        slug = article.slug,
        title = article.title,
        content = ContentDTO(
            markdown = article.content,
            html = contentHTML,
        ),
        thumbnail = article.thumbnail,
        writer = UserMapper.map(article.writer),
        stats = mapStats(article.stats),
        topics = article.topicsFlat,
        createdAt = article.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = article.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    private fun mapStats(article: ArticleStats) = ArticleStatsDTO(article.starCount, article.commentCount)
}