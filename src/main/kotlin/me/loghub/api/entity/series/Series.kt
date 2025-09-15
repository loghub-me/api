package me.loghub.api.entity.series

import jakarta.persistence.*
import me.loghub.api.dto.series.PostSeriesDTO
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.entity.PublicEntity
import me.loghub.api.entity.topic.Topic
import me.loghub.api.entity.user.User
import me.loghub.api.lib.jpa.TopicsFlatConverter
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Formula
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "series")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class Series(
    @Column(name = "slug", nullable = false, length = 128)
    var slug: String,

    @Column(name = "title", nullable = false, length = 56)
    var title: String,

    @Column(name = "description", nullable = false, length = 8192)
    var description: String,

    @Column(name = "thumbnail", nullable = false)
    var thumbnail: String,

    @Embedded
    var stats: SeriesStats = SeriesStats(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,

    @OneToMany(mappedBy = "series", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @OrderBy("sequence ASC")
    var chapters: MutableList<SeriesChapter> = mutableListOf(),

    @OneToMany(mappedBy = "series", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @OrderBy("createdAt ASC")
    var reviews: MutableList<SeriesReview> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "series_topics",
        joinColumns = [JoinColumn(name = "series_id")],
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
    fun update(requestBody: PostSeriesDTO) {
        this.title = requestBody.title
        this.description = requestBody.description
        this.thumbnail = requestBody.thumbnail
    }

    fun updateSlug(slug: String) {
        this.slug = slug
    }

    fun updateTopics(topics: Set<Topic>) {
        this.topics.clear()
        this.topics = topics.toMutableSet()
        this.topicsFlat = TopicsFlatConverter.toFlat(topics)
    }

    fun incrementReviewCount() {
        stats.reviewCount++
    }

    fun decrementReviewCount() {
        stats.reviewCount--
    }
}