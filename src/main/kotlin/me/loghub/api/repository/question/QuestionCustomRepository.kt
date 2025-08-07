package me.loghub.api.repository.question

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import me.loghub.api.dto.question.QuestionFilter
import me.loghub.api.dto.question.QuestionSort
import me.loghub.api.entity.question.QQuestion
import me.loghub.api.entity.question.Question
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
            "ecfts({0}, {1})",
            Expressions.constant(query),
            Expressions.constant("questions_search_index")
        ) else null

        val searchQuery = JPAQuery<Question>(entityManager)
            .select(question)
            .from(question)
            .where(
                username?.let { question.writerUsername.eq(it) },
                filter.where,
                fullTextSearch
            )
            .orderBy(sort.order)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val countQuery = JPAQuery<Long>(entityManager)
            .select(question.count())
            .from(question)
            .where(
                username?.let { question.writerUsername.eq(it) },
                filter.where,
                fullTextSearch
            )

        val questions = searchQuery.fetch()
        val total = countQuery.fetchOne() ?: 0L

        return PageImpl(questions, pageable, total)
    }
}