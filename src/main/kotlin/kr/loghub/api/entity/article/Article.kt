package kr.loghub.api.entity.article

import jakarta.persistence.*
import kr.loghub.api.dto.article.PostArticleDTO
import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.entity.PublicEntity
import kr.loghub.api.entity.topic.Topic
import kr.loghub.api.entity.user.User
import kr.loghub.api.lib.jpa.TopicsFlatConverter
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "articles")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class Article(
    @Column(name = "slug", nullable = false, length = 100)
    var slug: String,

    @Column(name = "title", nullable = false, length = 100)
    var title: String,

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "thumbnail", nullable = false)
    var thumbnail: String,

    @Embedded
    var stats: ArticleStats = ArticleStats(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,

    @OneToMany(mappedBy = "article", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @OrderBy("id")
    var comments: MutableList<ArticleComment> = mutableListOf(),

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
) : PublicEntity() {
    fun update(requestBody: PostArticleDTO) {
        this.title = requestBody.title
        this.content = requestBody.content
        this.thumbnail = requestBody.thumbnail
    }

    fun updateSlug(slug: String) {
        this.slug = slug
    }

    fun updateTopics(topics: List<TopicDTO>) {
        this.topics.clear()
        this.topicsFlat = topics
    }

    fun incrementCommentCount() {
        stats.commentCount++
    }

    fun decrementCommentCount() {
        stats.commentCount--
    }
}