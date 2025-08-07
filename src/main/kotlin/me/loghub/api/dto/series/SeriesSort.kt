package me.loghub.api.dto.series

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.Expressions
import me.loghub.api.entity.series.QSeries

enum class SeriesSort(val order: OrderSpecifier<*>) {
    latest(QSeries.series.createdAt.desc()),
    oldest(QSeries.series.createdAt.asc()),
    relevant(
        Expressions.numberTemplate(
            Double::class.java,
            "pgroonga_score({0}, {1})",
            QSeries.series.tableoid, QSeries.series.ctid
        ).desc()
    ),
    trending(QSeries.series.stats.trendingScore.desc());
}