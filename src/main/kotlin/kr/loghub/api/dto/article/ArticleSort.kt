package kr.loghub.api.dto.article

import com.querydsl.core.types.OrderSpecifier
import kr.loghub.api.entity.article.QArticle

enum class ArticleSort(val order: OrderSpecifier<*>) {
    latest(QArticle.article.createdAt.desc()),
    oldest(QArticle.article.createdAt.asc()),
    trending(QArticle.article.stats.trendingScore.desc());
}