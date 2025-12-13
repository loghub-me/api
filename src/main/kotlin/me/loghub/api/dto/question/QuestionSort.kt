package me.loghub.api.dto.question

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.Expressions
import me.loghub.api.entity.question.QQuestion
import me.loghub.api.lib.hibernate.PGroongaHibernateFunction

enum class QuestionSort(vararg val orders: OrderSpecifier<*>) {
    latest(QQuestion.question.createdAt.desc()),
    oldest(QQuestion.question.createdAt.asc()),
    relevant(
        Expressions.numberTemplate(
            Double::class.java,
            PGroongaHibernateFunction.PGROONGA_SCORE.template,
            QQuestion.question.tableoid, QQuestion.question.ctid,
        ).desc()
    ),
    trending(
        QQuestion.question.stats.trendingScore.desc(),
        QQuestion.question.stats.starCount.desc(),
    );
}