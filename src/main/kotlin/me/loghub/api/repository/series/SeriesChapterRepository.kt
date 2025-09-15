package me.loghub.api.repository.series

import me.loghub.api.entity.series.Series
import me.loghub.api.entity.series.SeriesChapter
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SeriesChapterRepository : JpaRepository<SeriesChapter, Long> {
    private companion object {
        const val SELECT_CHAPTER = "SELECT bc FROM SeriesChapter bc"
        const val BY_SERIES = "bc.series = :series"
        const val BY_SEQUENCE = "bc.sequence = :sequence"
        const val SEQUENCE_ASC = "bc.sequence ASC"
    }

    @Query("$SELECT_CHAPTER WHERE $BY_SERIES AND $BY_SEQUENCE")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterBySeriesAndSequence(series: Series, sequence: Int): SeriesChapter?

    @Query("$SELECT_CHAPTER WHERE $BY_SERIES ORDER BY $SEQUENCE_ASC")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterAllBySeriesOrderBySequenceAsc(series: Series): List<SeriesChapter>

    fun findAllBySeriesIdAndSequenceGreaterThanOrderBySequenceAsc(seriesId: Long, sequence: Int): List<SeriesChapter>

    fun countBySeries(series: Series): Int
}