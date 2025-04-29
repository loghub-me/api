package kr.loghub.api.repository.article

import kr.loghub.api.entity.article.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ArticleRepository : JpaRepository<Article, Long> {
    companion object {
        const val SELECT_ARTICLE = "SELECT a FROM Article a"
        const val EXISTS_ARTICLE = "SELECT COUNT(a) > 0 FROM Article a"
        const val BY_COMPOSITE_KEY = "a.writerUsername = :username AND a.slug = :slug"
    }

    @Query("$SELECT_ARTICLE WHERE $BY_COMPOSITE_KEY")
    fun findByCompositeKey(username: String, slug: String): Article?

    @Query("$EXISTS_ARTICLE WHERE $BY_COMPOSITE_KEY")
    fun existsByCompositeKey(username: String, slug: String): Boolean
}