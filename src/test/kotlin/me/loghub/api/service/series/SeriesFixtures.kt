package me.loghub.api.service.series

import me.loghub.api.dto.series.PostSeriesDTO
import me.loghub.api.dto.series.chapter.EditSeriesChapterDTO
import me.loghub.api.dto.series.chapter.UpdateSeriesChapterSequenceDTO
import me.loghub.api.dto.series.review.PostSeriesReviewDTO
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.series.SeriesChapter
import me.loghub.api.entity.series.SeriesReview
import me.loghub.api.entity.series.SeriesStats
import me.loghub.api.entity.user.User
import me.loghub.api.service.article.ArticleFixtures
import java.time.LocalDateTime

object SeriesFixtures {
    fun writer(
        id: Long = 1L,
        username: String = "series_writer",
    ) = ArticleFixtures.writer(id = id, username = username)

    fun series(
        id: Long = 1L,
        writer: User = writer(),
        slug: String = "test-series",
        title: String = "Test Series",
        description: String = "This is a test series description.",
        thumbnail: String = "1/thumbnail.webp",
        chapterCount: Int = 0,
        chapters: MutableList<SeriesChapter> = mutableListOf(),
        reviews: MutableList<SeriesReview> = mutableListOf(),
    ) = Series(
        slug = slug,
        title = title,
        description = description,
        thumbnail = thumbnail,
        stats = SeriesStats(chapterCount = chapterCount),
        writer = writer,
        chapters = chapters,
        reviews = reviews,
        writerUsername = writer.username,
        topicsFlat = emptyList(),
    ).apply { this.id = id }

    fun chapter(
        id: Long = 1L,
        series: Series = series(),
        writer: User = series.writer,
        title: String = "Chapter $id",
        content: String = "Chapter content $id",
        normalizedContent: String = "Normalized chapter content $id",
        sequence: Int = 1,
        published: Boolean = true,
        publishedAt: LocalDateTime? = if (published) LocalDateTime.now() else null,
    ) = SeriesChapter(
        title = title,
        content = content,
        normalizedContent = normalizedContent,
        sequence = sequence,
        published = published,
        publishedAt = publishedAt,
        series = series,
        writer = writer,
    ).apply { this.id = id }

    fun review(
        id: Long = 1L,
        series: Series = series(),
        writer: User = writer(id = 2L, username = "reviewer"),
        content: String = "Great series",
        rating: Int = 5,
    ) = SeriesReview(
        content = content,
        rating = rating,
        series = series,
        writer = writer,
    ).apply { this.id = id }

    fun postSeriesDTO(
        title: String = "Test Series",
        description: String = "This is a test series description.",
        thumbnail: String = "1/thumbnail.webp",
        topicSlugs: List<String> = emptyList(),
    ) = PostSeriesDTO(
        title = title,
        description = description,
        thumbnail = thumbnail,
        topicSlugs = topicSlugs,
    )

    fun editSeriesChapterDTO(
        title: String = "Edited Chapter",
        content: String = "Edited chapter content",
        published: Boolean = true,
    ) = EditSeriesChapterDTO(
        title = title,
        content = content,
        published = published,
    )

    fun updateSeriesChapterSequenceDTO(
        sequences: List<Int>,
    ) = UpdateSeriesChapterSequenceDTO(sequences = sequences)

    fun postSeriesReviewDTO(
        content: String = "Great series!",
        rating: Int = 5,
    ) = PostSeriesReviewDTO(
        content = content,
        rating = rating,
    )
}
