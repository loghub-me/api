package me.loghub.api.mapper.article

import me.loghub.api.dto.article.*
import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.article.ArticleStats
import me.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object ArticleMapper {
    fun map(article: Article) = ArticleDTO(
        id = article.id!!,
        slug = article.slug,
        title = article.title,
        thumbnail = article.thumbnail,
        writer = UserMapper.map(article.writer),
        stats = mapStats(article.stats),
        topics = article.topicsFlat,
        publishedAt = article.publishedAt!!.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapDetail(article: Article, renderedMarkdown: RenderedMarkdownDTO) = ArticleDetailDTO(
        id = article.id!!,
        slug = article.slug,
        title = article.title,
        content = ContentDTO(
            markdown = article.content,
            html = renderedMarkdown.html,
        ),
        anchors = renderedMarkdown.anchors,
        thumbnail = article.thumbnail,
        writer = UserMapper.map(article.writer),
        stats = mapStats(article.stats),
        topics = article.topicsFlat,
        publishedAt = article.publishedAt!!.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = article.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapForEdit(article: Article, draft: String?) = ArticleForEditDTO(
        id = article.id!!,
        title = article.title,
        content = article.content,
        draft = draft,
        thumbnail = article.thumbnail,
        topicSlugs = article.topicsFlat.map { it.slug },
        published = article.published,
    )

    fun mapForImport(article: Article) = ArticleForImportDTO(
        id = article.id!!,
        slug = article.slug,
        title = article.title,
        topics = article.topicsFlat,
    )

    private fun mapStats(article: ArticleStats) = ArticleStatsDTO(article.starCount, article.commentCount)
}