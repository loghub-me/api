package me.loghub.api.repository.question

import me.loghub.api.entity.question.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface QuestionStatsRepository : JpaRepository<Question, Long> {
    @Modifying
    @Query("UPDATE Question q SET q.stats.starCount = q.stats.starCount + 1 WHERE q.id = :id")
    fun incrementStarCount(id: Long)

    @Modifying
    @Query(
        """
        UPDATE Question q
        SET q.stats.starCount = CASE WHEN q.stats.starCount > 0 THEN q.stats.starCount - 1 ELSE 0 END
        WHERE q.id = :id
        """
    )
    fun decrementStarCount(id: Long)

    @Modifying
    @Query("UPDATE Question q SET q.stats.answerCount = q.stats.answerCount + 1 WHERE q.id = :id")
    fun incrementAnswerCount(id: Long)

    @Modifying
    @Query(
        """
        UPDATE Question q
        SET q.stats.answerCount = CASE WHEN q.stats.answerCount > 0 THEN q.stats.answerCount - 1 ELSE 0 END
        WHERE q.id = :id
        """
    )
    fun decrementAnswerCount(id: Long)
}