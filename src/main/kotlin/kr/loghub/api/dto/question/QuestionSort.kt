package kr.loghub.api.dto.question

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.Expressions
import kr.loghub.api.entity.question.QQuestion

enum class QuestionSort(val order: OrderSpecifier<*>) {
    latest(QQuestion.question.createdAt.desc()),
    oldest(QQuestion.question.createdAt.asc()),
    relevant(
        Expressions.numberTemplate(
            Double::class.java,
            "pgroonga_score({0}, {1})",
            QQuestion.question.rowMetadata.tableoid, QQuestion.question.rowMetadata.ctid
        ).desc()
    ),
    trending(QQuestion.question.stats.trendingScore.desc());
}