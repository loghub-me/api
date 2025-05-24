package kr.loghub.api.repository.quesiton.answer

import kr.loghub.api.entity.question.Answer
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AnswerRepository : JpaRepository<Answer, Long> {
    companion object {
        const val SELECT_ANSWER = "SELECT a FROM Answer a"
        const val BY_ID = "a.id = :id"
        const val BY_QUESTION_ID = "a.question.id = :questionId"
    }

    @Query("$SELECT_ANSWER WHERE $BY_ID AND $BY_QUESTION_ID")
    @EntityGraph(attributePaths = ["writer", "question"])
    fun findWithWriterByIdAndQuestionId(id: Long, questionId: Long): Answer?
}