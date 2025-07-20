package kr.loghub.api.repository.question

import kr.loghub.api.entity.question.QuestionAnswer
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface QuestionAnswerRepository : JpaRepository<QuestionAnswer, Long> {
    companion object {
        const val SELECT_ANSWER = "SELECT qa FROM QuestionAnswer qa"
        const val BY_ID = "qa.id = :id"
        const val BY_QUESTION_ID = "qa.question.id = :questionId"
    }

    fun findAllWithWriterByQuestionIdOrderByCreatedAt(questionId: Long): List<QuestionAnswer>

    @Query("$SELECT_ANSWER WHERE $BY_ID AND $BY_QUESTION_ID")
    @EntityGraph(attributePaths = ["writer", "question"])
    fun findWithWriterByIdAndQuestionId(id: Long, questionId: Long): QuestionAnswer?
}