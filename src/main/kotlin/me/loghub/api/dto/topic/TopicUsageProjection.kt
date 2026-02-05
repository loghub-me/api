package me.loghub.api.dto.topic

interface TopicUsageProjection {
    val slug: String
    val name: String
    val count: Int
}
