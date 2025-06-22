package kr.loghub.api.dto.article

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.Expressions
import kr.loghub.api.entity.article.QArticle

enum class ArticleSort(val order: OrderSpecifier<*>) {
    latest(QArticle.article.createdAt.desc()),
    oldest(QArticle.article.createdAt.asc()),
    relevant(
        Expressions.numberTemplate(
            Double::class.java,
            "pgroonga_score({0}, {1})",
            QArticle.article.tableoid, QArticle.article.ctid
        ).desc()
    ),
    trending(QArticle.article.stats.trendingScore.desc());
}