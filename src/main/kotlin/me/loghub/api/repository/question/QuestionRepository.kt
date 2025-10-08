package me.loghub.api.repository.question

import me.loghub.api.entity.question.Question
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface QuestionRepository : JpaRepository<Question, Long> {
    private companion object {
        const val SELECT_QUESTION = "SELECT q FROM Question q"
        const val EXISTS_QUESTION = "SELECT COUNT(q) > 0 FROM Question q"
        const val BY_ID = "q.id = :id"
        const val BY_COMPOSITE_KEY = "q.writerUsername = :username AND q.slug = :slug"
    }

    @Query("$SELECT_QUESTION WHERE $BY_ID")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterById(id: Long): Question?

    @Query("$SELECT_QUESTION WHERE $BY_COMPOSITE_KEY")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterByCompositeKey(username: String, slug: String): Question?

    @Query("$SELECT_QUESTION JOIN q.topics t WHERE t.slug = :topicSlug ORDER BY q.stats.trendingScore DESC LIMIT 10")
    fun findTop10ByTopicIdOrderByTrendingScoreDesc(topicSlug: String): List<Question>

    @Query("SELECT q.answerGenerating FROM Question q WHERE $BY_ID")
    fun findAnswerGeneratingById(id: Long): Boolean?

    @Query("$EXISTS_QUESTION WHERE $BY_COMPOSITE_KEY")
    fun existsByCompositeKey(username: String, slug: String): Boolean

    @Query("$EXISTS_QUESTION WHERE $BY_COMPOSITE_KEY AND q.id <> :id")
    fun existsByCompositeKeyAndIdNot(username: String, slug: String, id: Long): Boolean

    @Modifying
    @Query("UPDATE Question q SET q.stats.trendingScore = :trendingScore WHERE $BY_ID")
    fun updateTrendingScoreById(trendingScore: Double, id: Long): Int

    @Modifying
    @Query("UPDATE Question q SET q.stats.trendingScore = 0")
    fun clearTrendingScore(): Int

    @Modifying
    @Query("UPDATE Question q SET q.answerGenerating = :answerGenerating WHERE $BY_ID")
    fun updateAnswerGeneratingById(answerGenerating: Boolean, id: Long): Int

    @Modifying
    @Query("UPDATE Question q SET q.writerUsername = :newUsername WHERE q.writerUsername = :oldUsername")
    fun updateWriterUsernameByWriterUsername(
        @Param("oldUsername") oldUsername: String,
        @Param("newUsername") newUsername: String
    ): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Question q SET q.stats.starCount = q.stats.starCount + 1 WHERE q.id = :id")
    fun incrementStarCount(id: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Question q SET q.stats.answerCount = q.stats.answerCount + 1 WHERE q.id = :id")
    fun incrementAnswerCount(id: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE Question q
        SET q.stats.starCount = CASE WHEN q.stats.starCount > 0 THEN q.stats.starCount - 1 ELSE 0 END
        WHERE q.id = :id"""
    )
    fun decrementStarCount(id: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE Question q
        SET q.stats.answerCount = CASE WHEN q.stats.answerCount > 0 THEN q.stats.answerCount - 1 ELSE 0 END
        WHERE q.id = :id"""
    )
    fun decrementAnswerCount(id: Long)
}