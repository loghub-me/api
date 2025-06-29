package kr.loghub.api.entity.book

import jakarta.persistence.*
import kr.loghub.api.dto.book.PostBookDTO
import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.entity.PublicEntity
import kr.loghub.api.entity.topic.Topic
import kr.loghub.api.entity.user.User
import kr.loghub.api.lib.jpa.TopicsFlatConverter
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Formula
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "books")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class Book(
    @Column(name = "slug", nullable = false, length = 100)
    var slug: String,

    @Column(name = "title", nullable = false, length = 100)
    var title: String,

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "thumbnail", nullable = false)
    var thumbnail: String,

    @Embedded
    var stats: BookStats = BookStats(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,

    @OneToMany(mappedBy = "book", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @OrderBy("sequence ASC")
    var chapters: MutableList<BookChapter> = mutableListOf(),

    @OneToMany(mappedBy = "book", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @OrderBy("createdAt ASC")
    var reviews: MutableList<BookReview> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_topics",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "topic_id")]
    )
    var topics: MutableSet<Topic> = mutableSetOf(),

    @Column(nullable = false, length = 12)
    val writerUsername: String,  // for search(denormalization)

    @Column(nullable = false)
    @Convert(converter = TopicsFlatConverter::class)
    var topicsFlat: List<TopicDTO>,  // for search(denormalization)

    @Formula("tableoid")
    val tableoid: String? = null,

    @Formula("ctid")
    val ctid: String? = null,
) : PublicEntity() {
    fun update(requestBody: PostBookDTO) {
        this.title = requestBody.title
        this.content = requestBody.content
        this.thumbnail = requestBody.thumbnail
    }

    fun updateSlug(slug: String) {
        this.slug = slug
    }

    fun incrementReviewCount() {
        stats.reviewCount++
    }

    fun decrementReviewCount() {
        stats.reviewCount--
    }

    fun incrementStarCount() {
        stats.starCount++
    }
}