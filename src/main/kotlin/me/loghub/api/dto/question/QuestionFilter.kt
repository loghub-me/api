package me.loghub.api.dto.question

import com.querydsl.core.types.dsl.BooleanExpression
import me.loghub.api.entity.question.QQuestion
import me.loghub.api.entity.question.Question

enum class QuestionFilter(val where: BooleanExpression) {
    all(QQuestion.question.status.isNotNull()),
    `open`(QQuestion.question.status.eq(Question.Status.OPEN)),
    closed(QQuestion.question.status.eq(Question.Status.CLOSED)),
    solved(QQuestion.question.status.eq(Question.Status.SOLVED)),
}