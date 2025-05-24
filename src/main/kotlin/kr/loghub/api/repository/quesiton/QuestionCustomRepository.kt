package kr.loghub.api.repository.quesiton

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import kr.loghub.api.dto.question.QuestionSort
import kr.loghub.api.entity.question.QQuestion
import kr.loghub.api.entity.question.Question
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class QuestionCustomRepository(private val entityManager: EntityManager) {
    fun search(query: String, sort: QuestionSort, pageable: Pageable): Page<Question> {
        val searchQuery = JPAQuery<Question>(entityManager)
            .select(QQuestion.question).from(QQuestion.question)
            .orderBy(sort.order)
            .offset(pageable.offset).limit(pageable.pageSize.toLong())
        val countQuery = JPAQuery<Question>(entityManager)
            .select(QQuestion.question.count()).from(QQuestion.question)

        if (query.isNotBlank()) {
            val fullTextSearch = Expressions.booleanTemplate(
                "ecfts({0}, {1})",
                Expressions.constant(query),
                Expressions.constant("questions_search_index")
            );
            searchQuery.where(fullTextSearch)
            countQuery.where(fullTextSearch)
        }

        val questions = searchQuery.fetch()
        val total = countQuery.fetchOne() ?: 0L

        return PageImpl(questions, pageable, total)
    }
}