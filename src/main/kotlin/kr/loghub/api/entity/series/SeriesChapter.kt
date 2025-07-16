package kr.loghub.api.entity.series

import jakarta.persistence.*
import kr.loghub.api.dto.series.chapter.EditSeriesChapterDTO
import kr.loghub.api.entity.PublicEntity
import kr.loghub.api.entity.user.User
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "series_chapters")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class SeriesChapter(
    @Column(name = "title", nullable = false, length = 100)
    var title: String,

    @Column(name = "content", nullable = false)
    var content: String = "",

    @Column(name = "sequence", nullable = false)
    var sequence: Int,

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
}