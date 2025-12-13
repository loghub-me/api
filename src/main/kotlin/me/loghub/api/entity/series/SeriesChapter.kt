package me.loghub.api.entity.series

import jakarta.persistence.*
import me.loghub.api.dto.series.chapter.EditSeriesChapterDTO
import me.loghub.api.entity.PublicEntity
import me.loghub.api.entity.user.User
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "series_chapters")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class SeriesChapter(
    @Column(name = "title", nullable = false, length = 56)
    var title: String,

    @Column(name = "content", nullable = false, length = 8192)
    var content: String = "",

    @Column(name = "sequence", nullable = false)
    var sequence: Int,

    @Column(name = "published", nullable = false)
    var published: Boolean,

    @Column(name = "published_at")
    var publishedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "series_id", nullable = false)
    val series: Series,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,
) : PublicEntity() {
    fun update(requestBody: EditSeriesChapterDTO) {
        this.title = requestBody.title
        this.content = requestBody.content
    }

    fun updateSequence(newSequence: Int) {
        this.sequence = newSequence
    }

    fun publish() {
        this.published = true
        publishedAt = publishedAt ?: LocalDateTime.now()
    }

    fun unpublish() {
        this.published = false
    }
}