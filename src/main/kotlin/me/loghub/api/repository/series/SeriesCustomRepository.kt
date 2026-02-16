package me.loghub.api.repository.series

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import me.loghub.api.dto.series.SeriesSort
import me.loghub.api.dto.topic.TopicSeriesSort
import me.loghub.api.entity.series.QSeries
import me.loghub.api.entity.series.Series
import me.loghub.api.lib.hibernate.PGroongaHibernateFunction
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class SeriesCustomRepository(private val entityManager: EntityManager) {
    private companion object {
        val series = QSeries.series;
    }

    fun search(
        query: String,
        sort: SeriesSort,
        pageable: Pageable,
        username: String? = null
    ): Page<Series> {
        val ftsCondition = query.takeIf { it.isNotBlank() }
            ?.let(::createFullTextSearchCondition)
        val writerCondition = username.takeIf { !it.isNullOrBlank() }
            ?.let(::createWriterCondition)
        val conditions = listOfNotNull(ftsCondition, writerCondition).toTypedArray()
        val resolvedSort = sort.takeUnless { ftsCondition == null && it == SeriesSort.relevant }
            ?: SeriesSort.latest

        return runQueryAndWrapPage(conditions = conditions, orders = resolvedSort.orders, pageable = pageable)
    }

    fun findByTopicSlug(
        topicSlug: String,
        sort: TopicSeriesSort,
        pageable: Pageable,
    ): Page<Series> {
        val topicCondition = series.topics.any().slug.eq(topicSlug)
        return runQueryAndWrapPage(conditions = arrayOf(topicCondition), orders = sort.orders, pageable = pageable)
    }

    private fun createWriterCondition(username: String) = series.writerUsername.eq(username)
    private fun createFullTextSearchCondition(query: String) =
        Expressions.booleanTemplate(PGroongaHibernateFunction.SERIES_FTS.template, query)

    private fun runQueryAndWrapPage(
        conditions: Array<BooleanExpression>,
        orders: Array<out OrderSpecifier<*>>,
        pageable: Pageable,
    ): Page<Series> {
        val searchQuery = JPAQuery<Series>(entityManager)
            .select(series)
            .from(series)
            .where(*conditions)
            .leftJoin(series.writer).fetchJoin()
            .orderBy(*orders)
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