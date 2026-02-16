package me.loghub.api.dto.topic

import com.querydsl.core.types.OrderSpecifier
import me.loghub.api.entity.article.QArticle

enum class TopicArticleSort(vararg val orders: OrderSpecifier<*>) {
    trending(
        QArticle.article.stats.trendingScore.desc(),
        QArticle.article.stats.starCount.desc(),
    ),
    latest(QArticle.article.publishedAt.desc()),
    oldest(QArticle.article.createdAt.asc());
}