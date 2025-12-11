package me.loghub.api.lib.ratelimit

import java.time.temporal.ChronoUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RateLimit(
    val limit: Int = 5,
    val window: Long = 1,
    val unit: ChronoUnit = ChronoUnit.HOURS,
)