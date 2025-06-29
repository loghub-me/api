package kr.loghub.api.entity.book

import jakarta.persistence.*
import kr.loghub.api.dto.book.chapter.EditBookChapterDTO
import kr.loghub.api.entity.PublicEntity
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "book_chapters")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class BookChapter(
    @Column(name = "title", nullable = false, length = 100)
    var title: String,

    @Column(name = "content", nullable = false)
    var content: String = "",

    @Column(name = "sequence", nullable = false)
    var sequence: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val book: Book,
) : PublicEntity() {
    fun update(requestBody: EditBookChapterDTO) {
        this.title = requestBody.title
        this.content = requestBody.content
    }

    fun updateSequence(newSequence: Int) {
        this.sequence = newSequence
    }
}