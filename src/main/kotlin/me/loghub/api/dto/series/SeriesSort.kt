package me.loghub.api.dto.series

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.Expressions
import me.loghub.api.entity.series.QSeries
import me.loghub.api.lib.hibernate.PGroongaHibernateFunction

enum class SeriesSort(vararg val orders: OrderSpecifier<*>) {
    latest(QSeries.series.createdAt.desc()),
    oldest(QSeries.series.createdAt.asc()),
    relevant(
        Expressions.numberTemplate(
            Double::class.java,
            PGroongaHibernateFunction.PGROONGA_SCORE.template,
            QSeries.series.tableoid, QSeries.series.ctid,
        ).desc()
    ),
    trending(
        QSeries.series.stats.trendingScore.desc(),
        QSeries.series.stats.starCount.desc(),
    );
}