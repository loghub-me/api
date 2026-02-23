package me.loghub.api.service.article

import me.loghub.api.dto.article.PostArticleDTO
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.user.*
import java.time.LocalDateTime

object ArticleFixtures {
    fun writer(
        id: Long = 1L,
        username: String = "testuser",
    ) = User(
        email = "$username@loghub.me",
        username = username,
        nickname = username,
        privacy = UserPrivacy(),
        meta = UserMeta(
            profile = UserProfile(),
            github = UserGitHub(),
            stats = UserStats(),
        )
    ).apply { this.id = id }

    fun article(
        id: Long = 1L,
        writer: User = writer(),
        title: String = "Test Article",
        published: Boolean = true,
    ) = Article(
        slug = "test-article",
        title = title,
        content = "$title's content",
        normalizedContent = "content",
        thumbnail = "1/thumbnail.webp",
        published = published,
        publishedAt = if (published) LocalDateTime.now() else null,
        writer = writer,
        writerUsername = writer.username,
        topicsFlat = emptyList()
    ).apply { this.id = id }

    fun postArticleDTO(
        title: String = "Test Article",
        content: String = "Test content",
        thumbnail: String = "1/thumbnail.webp",
        topicSlugs: List<String> = emptyList(),
        published: Boolean = true,
    ) = PostArticleDTO(
        title = title,
        content = content,
        thumbnail = thumbnail,
        topicSlugs = topicSlugs,
        published = published,
    )
}