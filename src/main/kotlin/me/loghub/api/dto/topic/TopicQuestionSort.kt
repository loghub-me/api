package me.loghub.api.dto.topic

import com.querydsl.core.types.OrderSpecifier
import me.loghub.api.entity.question.QQuestion

enum class TopicQuestionSort(vararg val orders: OrderSpecifier<*>) {
    trending(
        QQuestion.question.stats.trendingScore.desc(),
        QQuestion.question.stats.starCount.desc(),
    ),
    latest(QQuestion.question.createdAt.desc()),
    oldest(QQuestion.question.createdAt.asc());
}