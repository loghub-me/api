package me.loghub.api.dto.article

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.Expressions
import me.loghub.api.entity.article.QArticle
import me.loghub.api.lib.hibernate.PGroongaHibernateFunction

enum class ArticleSort(vararg val order: OrderSpecifier<*>) {
    latest(QArticle.article.createdAt.desc()),
    oldest(QArticle.article.createdAt.asc()),
    relevant(
        Expressions.numberTemplate(
            Double::class.java,
            PGroongaHibernateFunction.PGROONGA_SCORE.template,
            QArticle.article.tableoid, QArticle.article.ctid,
        ).desc()
    ),
    trending(
        QArticle.article.stats.trendingScore.desc(),
        QArticle.article.stats.starCount.desc(),
    );
}