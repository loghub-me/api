package kr.loghub.api.repository.quesiton

import kr.loghub.api.entity.question.Question
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface QuestionRepository : JpaRepository<Question, Long> {
    companion object {
        const val SELECT_QUESTION = "SELECT q FROM Question q"
        const val EXISTS_QUESTION = "SELECT COUNT(q) > 0 FROM Question q"
        const val BY_ID = "q.id = :id"
        const val BY_COMPOSITE_KEY = "q.writerUsername = :username AND q.slug = :slug"
    }

    @Query("$SELECT_QUESTION WHERE $BY_ID")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterById(id: Long): Question?

    @Query("$SELECT_QUESTION WHERE $BY_COMPOSITE_KEY")
    @EntityGraph(attributePaths = ["writer", "answers"])
    fun findWithWriterAndAnswersByCompositeKey(username: String, slug: String): Question?

    @Query("$EXISTS_QUESTION WHERE $BY_COMPOSITE_KEY")
    fun existsByCompositeKey(username: String, slug: String): Boolean
}