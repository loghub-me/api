package me.loghub.api.service.common

interface TrendingScoreService {
    fun updateTrendingScore(id: Long, delta: Double)
    fun clearTrendingScore(id: Long)
}