package me.loghub.api.repository.question

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import me.loghub.api.dto.question.QuestionFilter
import me.loghub.api.dto.question.QuestionSort
import me.loghub.api.entity.question.QQuestion
import me.loghub.api.entity.question.Question
import me.loghub.api.lib.hibernate.PGroongaHibernateFunction
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class QuestionCustomRepository(private val entityManager: EntityManager) {
    private companion object {
        val question = QQuestion.question
    }

    fun search(
        query: String,
        sort: QuestionSort,
        filter: QuestionFilter,
        pageable: Pageable,
        username: String? = null
    ): Page<Question> {
        val filterCondition = filter.where
        val ftsCondition = query.takeIf { it.isNotBlank() }
            ?.let(::createFullTextSearchCondition)
        val writerCondition = username.takeIf { !it.isNullOrBlank() }
            ?.let(::createWriterCondition)
        val conditions = listOfNotNull(filterCondition, ftsCondition, writerCondition).toTypedArray()
        val resolvedSort = sort.takeUnless { ftsCondition == null && it == QuestionSort.relevant }
            ?: QuestionSort.latest

        return runQueryAndWrapPage(conditions = conditions, orders = resolvedSort.orders, pageable = pageable)
    }

    private fun createWriterCondition(username: String) = question.writerUsername.eq(username)
    private fun createFullTextSearchCondition(query: String) =
        Expressions.booleanTemplate(PGroongaHibernateFunction.ARTICLES_FTS.template, query)

    private fun runQueryAndWrapPage(
        conditions: Array<BooleanExpression>,
        orders: Array<out OrderSpecifier<*>>,
        pageable: Pageable,
    ): Page<Question> {
        val searchQuery = JPAQuery<Question>(entityManager)
            .select(question)
            .from(question)
            .where(*conditions)
            .leftJoin(question.writer).fetchJoin()
            .orderBy(*orders)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
        val countQuery = JPAQuery<Long>(entityManager)
            .select(question.count())
            .from(question)
            .where(*conditions)

        val questions = searchQuery.fetch()
        val total = countQuery.fetchOne() ?: 0L

        return PageImpl(questions, pageable, total)
    }
}