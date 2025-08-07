package me.loghub.api.repository.series

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import me.loghub.api.dto.series.SeriesSort
import me.loghub.api.entity.series.QSeries
import me.loghub.api.entity.series.Series
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class SeriesCustomRepository(private val entityManager: EntityManager) {
    private companion object {
        val series = QSeries.series
    }

    fun search(
        query: String,
        sort: SeriesSort,
        pageable: Pageable,
        username: String? = null
    ): Page<Series> {
        val fullTextSearch = if (query.isNotBlank()) Expressions.booleanTemplate(
            "ecfts({0}, {1})",
            Expressions.constant(query),
            Expressions.constant("series_search_index")
        ) else null

        val searchQuery = JPAQuery<Series>(entityManager)
            .select(series)
            .from(series)
            .where(
                username?.let { series.writerUsername.eq(it) },
                fullTextSearch
            )
            .orderBy(sort.order)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val countQuery = JPAQuery<Long>(entityManager)
            .select(series.count())
            .from(series)
            .where(
                username?.let { series.writerUsername.eq(it) },
                fullTextSearch
            )

        val series = searchQuery.fetch()
        val total = countQuery.fetchOne() ?: 0L

        return PageImpl(series, pageable, total)
    }
}