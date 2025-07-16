package kr.loghub.api.mapper.series

import kr.loghub.api.dto.series.SeriesDTO
import kr.loghub.api.dto.series.SeriesDetailDTO
import kr.loghub.api.dto.series.SeriesStatsDTO
import kr.loghub.api.entity.series.Series
import kr.loghub.api.entity.series.SeriesStats
import kr.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object SeriesMapper {
    fun map(series: Series) = SeriesDTO(
        id = series.id!!,
        slug = series.slug,
        title = series.title,
        thumbnail = series.thumbnail,
        writerUsername = series.writerUsername,
        stats = mapStats(series.stats),
        topics = series.topicsFlat,
        createdAt = series.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = series.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapDetail(series: Series) = SeriesDetailDTO(
        id = series.id!!,
        slug = series.slug,
        title = series.title,
        content = series.content,
        thumbnail = series.thumbnail,
        writer = UserMapper.map(series.writer),
        stats = mapStats(series.stats),
        topics = series.topicsFlat,
        chapters = series.chapters.map(SeriesChapterMapper::map),
        createdAt = series.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = series.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    private fun mapStats(series: SeriesStats) = SeriesStatsDTO(series.starCount, series.reviewCount)
}