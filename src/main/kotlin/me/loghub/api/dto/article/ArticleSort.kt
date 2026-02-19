package me.loghub.api.dto.article

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.Expressions
import me.loghub.api.entity.article.QArticle
import me.loghub.api.lib.hibernate.ParadeDBHibernateFunction

enum class ArticleSort(vararg val orders: OrderSpecifier<*>) {
    latest(
        QArticle.article.publishedAt.desc(),
        QArticle.article.updatedAt.desc(),
    ),
    oldest(QArticle.article.createdAt.asc()),
    relevant(
        Expressions.numberTemplate(
            Double::class.java,
            ParadeDBHibernateFunction.SCORE.template,
            QArticle.article.id,
        ).desc()
    ),
    trending(
        QArticle.article.stats.trendingScore.desc(),
        QArticle.article.stats.starCount.desc(),
    );
}