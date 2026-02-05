package me.loghub.api.dto.topic

data class TopicUsageDTO(
    val slug: String,
    val name: String,
    val count: Int,
) {
    constructor(projection: TopicUsageProjection) : this(
        slug = projection.slug,
        name = projection.name,
        count = projection.count,
    )
}
