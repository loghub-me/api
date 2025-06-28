package kr.loghub.api.repository.question

import kr.loghub.api.entity.question.Question
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

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

    @Modifying
    @Query("UPDATE Article a SET a.stats.starCount = a.stats.starCount - 1 WHERE a.id = :id")
    fun decrementStarCount(id: Long)

    @Modifying
    @Query("UPDATE Question q SET q.stats.trendingScore = :trendingScore WHERE q.id = :id")
    fun updateTrendingScoreById(trendingScore: Double, id: Long): Int

    @Modifying
    @Query("UPDATE Question q SET q.stats.trendingScore = 0")
    fun clearTrendingScore(): Int

    @Modifying
    @Query("UPDATE Question q SET q.writerUsername = :newUsername WHERE q.writerUsername = :oldUsername")
    fun updateWriterUsernameByWriterUsername(
        @Param("oldUsername") oldUsername: String,
        @Param("newUsername") newUsername: String
    ): Int
}