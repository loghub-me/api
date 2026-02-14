package me.loghub.api.repository.series

import me.loghub.api.entity.series.Series
import me.loghub.api.entity.series.SeriesChapter
import me.loghub.api.entity.user.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SeriesChapterRepository : JpaRepository<SeriesChapter, Long> {
    private companion object {
        const val SELECT_CHAPTER = "SELECT sc FROM SeriesChapter sc"
        const val BY_ID = "sc.id = :id"
        const val BY_SERIES = "sc.series = :series"
        const val BY_SEQUENCE = "sc.sequence = :sequence"
        const val SEQUENCE_ASC = "sc.sequence ASC"
    }

    @Query("$SELECT_CHAPTER WHERE $BY_SERIES AND $BY_ID")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterBySeriesAndId(series: Series, id: Long): SeriesChapter?

    @Query("$SELECT_CHAPTER WHERE $BY_SERIES AND $BY_SEQUENCE")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterBySeriesAndSequence(series: Series, sequence: Int): SeriesChapter?

    @Query("$SELECT_CHAPTER WHERE $BY_SERIES ORDER BY $SEQUENCE_ASC")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterAllBySeriesOrderBySequenceAsc(series: Series): List<SeriesChapter>

    fun findAllBySeriesIdAndSequenceGreaterThanOrderBySequenceAsc(seriesId: Long, sequence: Int): List<SeriesChapter>

    fun existsBySeriesAndIdAndWriter(series: Series, id: Long, writer: User): Boolean
}