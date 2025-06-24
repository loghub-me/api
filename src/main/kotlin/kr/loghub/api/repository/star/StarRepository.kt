package kr.loghub.api.repository.star

import kr.loghub.api.entity.star.Star
import kr.loghub.api.entity.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface StarRepository : JpaRepository<Star, Long> {
    @EntityGraph(attributePaths = ["article", "question", "user"])
    fun findByUser(user: User, pageable: Pageable): Page<Star>

    fun existsByArticleIdAndUserId(articleId: Long, userId: Long): Boolean

    @EntityGraph(attributePaths = ["article", "user"])
    fun deleteByArticleIdAndUserId(articleId: Long, userId: Long): Int

    fun existsByQuestionIdAndUserId(questionId: Long, userId: Long): Boolean

    @EntityGraph(attributePaths = ["question", "user"])
    fun deleteByQuestionIdAndUserId(questionId: Long, userId: Long): Int
}