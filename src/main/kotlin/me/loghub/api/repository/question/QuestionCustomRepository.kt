package me.loghub.api.repository.question

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
        val fullTextSearch = if (query.isNotBlank()) Expressions.booleanTemplate(
            PGroongaHibernateFunction.QUESTIONS_FTS.template,
            query,
        ) else null
        val usernameFilter = if (username.isNullOrBlank()) null else question.writerUsername.eq(username)
        val conditions = listOfNotNull(fullTextSearch, usernameFilter, filter.where).toTypedArray()
        val resolvedSort = sort.takeUnless { fullTextSearch == null && it == QuestionSort.relevant }
            ?: QuestionSort.latest

        val searchQuery = JPAQuery<Question>(entityManager)
            .select(question)
            .from(question)
            .leftJoin(question.writer).fetchJoin()
            .where(*conditions)
            .orderBy(resolvedSort.order)
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