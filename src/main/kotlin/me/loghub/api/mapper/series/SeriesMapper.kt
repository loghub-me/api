package me.loghub.api.mapper.series

import me.loghub.api.dto.series.SeriesDTO
import me.loghub.api.dto.series.SeriesDetailDTO
import me.loghub.api.dto.series.SeriesForEditDTO
import me.loghub.api.dto.series.SeriesStatsDTO
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.series.SeriesChapter
import me.loghub.api.entity.series.SeriesStats
import me.loghub.api.mapper.user.UserMapper

object SeriesMapper {
    fun map(series: Series) = SeriesDTO(
        id = series.persistedId,
        slug = series.slug,
        title = series.title,
        thumbnail = series.thumbnail,
        writer = UserMapper.map(series.writer),
        stats = mapStats(series.stats),
        topics = series.topicsFlat,
        createdAt = series.createdAt,
    )

    fun mapDetail(series: Series, chapters: List<SeriesChapter>) = SeriesDetailDTO(
        id = series.persistedId,
        slug = series.slug,
        title = series.title,
        description = series.description,
        thumbnail = series.thumbnail,
        writer = UserMapper.map(series.writer),
        stats = mapStats(series.stats),
        topics = series.topicsFlat,
        chapters = chapters.map(SeriesChapterMapper::map),
        createdAt = series.createdAt,
        updatedAt = series.updatedAt,
    )

    fun mapForEdit(series: Series) = SeriesForEditDTO(
        id = series.persistedId,
        slug = series.slug,
        title = series.title,
        description = series.description,
        thumbnail = series.thumbnail,
        topicSlugs = series.topicsFlat.map { it.slug },
        chapters = series.chapters.map(SeriesChapterMapper::map),
    )

    private fun mapStats(series: SeriesStats) = SeriesStatsDTO(series.starCount, series.reviewCount)
}