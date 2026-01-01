package me.loghub.api.lib.redis.key.series

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object SeriesChapterDraftRedisKey : RedisKey {
    val TTL = 7.days.toJavaDuration()
    operator fun invoke(seriesId: Long, chapterId: Long) = "series:$seriesId:chapter:$chapterId:draft"
}