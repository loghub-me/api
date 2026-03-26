package me.loghub.api.dto.series.event

data class SeriesCreatedEvent(
    val seriesId: Long,
    val writerId: Long,
)
