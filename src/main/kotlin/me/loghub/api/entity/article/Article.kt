package me.loghub.api.entity.article

import jakarta.persistence.*
import me.loghub.api.dto.article.PostArticleDTO
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.entity.PublicEntity
import me.loghub.api.entity.topic.Topic
import me.loghub.api.entity.user.User
import me.loghub.api.lib.jpa.TopicsFlatConverter
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Formula
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "articles")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class Article(
    @Column(name = "slug", nullable = false, length = 128)
    var slug: String,

    @Column(name = "title", nullable = false, length = 56)
    var title: String,

    @Column(name = "content", nullable = false, length = 16384)
    var content: String,

    @Column(name = "normalized_content", nullable = false, length = 8192)
    var normalizedContent: String,

    @Column(name = "thumbnail", nullable = false)
    var thumbnail: String,

    @Column(name = "published", nullable = false)
    var published: Boolean,

    @Column(name = "published_at")
    var publishedAt: LocalDateTime? = null,

    @Embedded
    var stats: ArticleStats = ArticleStats(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "article_topics",
        joinColumns = [JoinColumn(name = "article_id")],
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
    fun update(requestBody: PostArticleDTO, slug: String, normalizedContent: String) {
        this.title = requestBody.title
        this.content = requestBody.content
        this.thumbnail = requestBody.thumbnail
        this.slug = slug
        this.normalizedContent = normalizedContent
    }

    fun updateTopics(topics: Set<Topic>) {
        this.topics.clear()
        this.topics = topics.toMutableSet()
        this.topicsFlat = TopicsFlatConverter.toFlat(topics)
    }

    fun publish() {
        this.published = true
        this.publishedAt = this.publishedAt ?: LocalDateTime.now()
    }

    fun unpublish() {
        this.published = false
    }
}