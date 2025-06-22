package kr.loghub.api.repository.star

import kr.loghub.api.entity.star.Star
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface StarRepository : JpaRepository<Star, Long> {
    fun existsByArticleIdAndUserId(articleId: Long, userId: Long): Boolean

    @EntityGraph(attributePaths = ["article", "user"])
    fun deleteByArticleIdAndUserId(articleId: Long, userId: Long): Int

    fun existsByQuestionIdAndUserId(questionId: Long, userId: Long): Boolean

    @EntityGraph(attributePaths = ["question", "user"])
    fun deleteByQuestionIdAndUserId(questionId: Long, userId: Long): Int
}