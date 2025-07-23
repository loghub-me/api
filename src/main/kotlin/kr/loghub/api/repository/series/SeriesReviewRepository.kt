package kr.loghub.api.repository.series

import kr.loghub.api.entity.series.Series
import kr.loghub.api.entity.series.SeriesReview
import kr.loghub.api.entity.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SeriesReviewRepository : JpaRepository<SeriesReview, Long> {
    private companion object {
        const val SELECT_REVIEW = "SELECT br FROM SeriesReview br"
        const val BY_SERIES_ID = "br.series.id = :seriesId"
    }

    @EntityGraph(attributePaths = ["writer"])
    @Query("$SELECT_REVIEW WHERE $BY_SERIES_ID")
    fun findAllBySeriesId(seriesId: Long, pageable: Pageable): Page<SeriesReview>

    @EntityGraph(attributePaths = ["writer", "series"])
    fun findWithGraphBySeriesIdAndId(seriesId: Long, commentId: Long): SeriesReview?

    fun existsBySeriesAndWriter(series: Series, writer: User): Boolean
}