package me.loghub.api.mapper.article

import me.loghub.api.dto.article.*
import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.article.ArticleStats
import me.loghub.api.mapper.user.UserMapper

object ArticleMapper {
    fun map(article: Article) = ArticleDTO(
        id = article.persistedId,
        slug = article.slug,
        title = article.title,
        thumbnail = article.thumbnail,
        writer = UserMapper.map(article.writer),
        stats = mapStats(article.stats),
        topics = article.topicsFlat,
        publishedAt = article.publishedAt!!,
    )

    fun mapDetail(article: Article, renderedMarkdown: RenderedMarkdownDTO) = ArticleDetailDTO(
        id = article.persistedId,
        slug = article.slug,
        title = article.title,
        content = ContentDTO(
            html = renderedMarkdown.html,
            normalized = article.normalizedContent,
        ),
        anchors = renderedMarkdown.anchors,
        thumbnail = article.thumbnail,
        writer = UserMapper.map(article.writer),
        stats = mapStats(article.stats),
        topics = article.topicsFlat,
        publishedAt = article.publishedAt!!,
        updatedAt = article.updatedAt,
    )

    fun mapUnpublished(article: Article) = ArticleUnpublishedDTO(
        id = article.persistedId,
        title = article.title,
        topics = article.topicsFlat,
        createdAt = article.createdAt,
    )

    fun mapForEdit(article: Article, draft: String?) = ArticleForEditDTO(
        id = article.persistedId,
        title = article.title,
        content = article.content,
        draft = draft,
        thumbnail = article.thumbnail,
        topicSlugs = article.topicsFlat.map { it.slug },
        published = article.published,
    )

    fun mapForImport(article: Article) = ArticleForImportDTO(
        id = article.persistedId,
        slug = article.slug,
        title = article.title,
        topics = article.topicsFlat,
    )

    private fun mapStats(article: ArticleStats) = ArticleStatsDTO(article.starCount, article.commentCount)
}