package me.loghub.api.dto.topic

import com.querydsl.core.types.OrderSpecifier
import me.loghub.api.entity.series.QSeries

enum class TopicSeriesSort(vararg val orders: OrderSpecifier<*>) {
    trending(
        QSeries.series.stats.trendingScore.desc(),
        QSeries.series.stats.starCount.desc(),
    ),
    latest(QSeries.series.createdAt.desc()),
    oldest(QSeries.series.createdAt.asc());
}