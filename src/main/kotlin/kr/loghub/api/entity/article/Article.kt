package kr.loghub.api.entity.article

import jakarta.persistence.*
import kr.loghub.api.entity.PublicEntity
import kr.loghub.api.entity.user.User
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

    @Column(nullable = false, length = 12)
    val writerUsername: String,  // for search(denormalization)

    @Column(nullable = false)
    val topics: String,          // for search(denormalization)
) : PublicEntity()