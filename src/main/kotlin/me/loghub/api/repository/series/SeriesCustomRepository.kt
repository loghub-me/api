package me.loghub.api.repository.series

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import me.loghub.api.constant.hibernate.HibernateFunction
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
            HibernateFunction.SERIES_FTS.template,
            Expressions.constant(query),
        ) else null
        val usernameFilter = if (username.isNullOrBlank()) null else series.writerUsername.eq(username)
        val conditions = arrayOf(fullTextSearch, usernameFilter)

        val searchQuery = JPAQuery<Series>(entityManager)
            .select(series)
            .from(series)
            .leftJoin(series.writer).fetchJoin()
            .where(*conditions)
            .orderBy(sort.order)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val countQuery = JPAQuery<Long>(entityManager)
            .select(series.count())
            .from(series)
            .where(*conditions)

        val series = searchQuery.fetch()
        val total = countQuery.fetchOne() ?: 0L

        return PageImpl(series, pageable, total)
    }
}