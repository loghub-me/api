package kr.loghub.api.repository.book

import kr.loghub.api.entity.book.Book
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BookRepository : JpaRepository<Book, Long> {
    companion object {
        const val SELECT_BOOK = "SELECT b FROM Book b"
        const val EXISTS_BOOK = "SELECT COUNT(b) > 0 FROM Book b"
        const val BY_ID = "b.id = :id"
        const val BY_COMPOSITE_KEY = "b.writerUsername = :username AND b.slug = :slug"
    }

    @Query("$SELECT_BOOK WHERE $BY_ID")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterById(id: Long): Book?

    @Query("$SELECT_BOOK WHERE $BY_COMPOSITE_KEY")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterByCompositeKey(username: String, slug: String): Book?

    @Query("$EXISTS_BOOK WHERE $BY_COMPOSITE_KEY")
    fun existsByCompositeKey(username: String, slug: String): Boolean

    @Modifying
    @Query("UPDATE Book a SET a.stats.starCount = a.stats.starCount - 1 WHERE a.id = :id")
    fun decrementStarCount(id: Long)

    @Modifying
    @Query("UPDATE Book a SET a.stats.trendingScore = :trendingScore WHERE a.id = :id")
    fun updateTrendingScoreById(trendingScore: Double, id: Long): Int

    @Modifying
    @Query("UPDATE Book a SET a.stats.trendingScore = 0")
    fun clearTrendingScore(): Int

    @Modifying
    @Query("UPDATE Book a SET a.writerUsername = :newUsername WHERE a.writerUsername = :oldUsername")
    fun updateWriterUsernameByWriterUsername(
        @Param("oldUsername") oldUsername: String,
        @Param("newUsername") newUsername: String
    ): Int
}