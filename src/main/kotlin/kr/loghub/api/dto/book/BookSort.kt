package kr.loghub.api.dto.book

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.Expressions
import kr.loghub.api.entity.book.QBook

enum class BookSort(val order: OrderSpecifier<*>) {
    latest(QBook.book.createdAt.desc()),
    oldest(QBook.book.createdAt.asc()),
    relevant(
        Expressions.numberTemplate(
            Double::class.java,
            "pgroonga_score({0}, {1})",
            QBook.book.tableoid, QBook.book.ctid
        ).desc()
    ),
    trending(QBook.book.stats.trendingScore.desc());
}